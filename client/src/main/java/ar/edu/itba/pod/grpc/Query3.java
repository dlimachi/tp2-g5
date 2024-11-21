package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.collators.CountyReincidenceCollator;
import ar.edu.itba.pod.grpc.combiners.CountyReincidenceCombinerFactory;
import ar.edu.itba.pod.grpc.domain.Infraction;
import ar.edu.itba.pod.grpc.dto.CountyReincidenceDto;
import ar.edu.itba.pod.grpc.dto.ReincidentTicketsDto;
import ar.edu.itba.pod.grpc.mapper.CountyReincidenceMapper;
import ar.edu.itba.pod.grpc.reducer.CountyReincidenceReducerFactory;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import utils.Constants;
import utils.csv.CsvMappingConfig;

@SuppressWarnings("deprecation")
public class Query3 extends Query {
    private static final String OUT_CSV_HEADER = "County;Percentage\n";

    @Override
    protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
        IMap<Integer, ReincidentTicketsDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_COUNTY_MAP.getName());
        IMap<String, Infraction> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());

        return (fields, config, id) -> {
            if (fields.length >= Constants.FIELD_COUNT) {
                try {
                    String infractionCode = fields[config.getColumnIndex("infractionId")];
                    String plate = fields[config.getColumnIndex("plate")];
                    String county = fields[config.getColumnIndex("countyName")];
                    String date = fields[config.getColumnIndex("issueDate")];

                    Infraction infraction = infractions.get(infractionCode);
                    if (infraction == null) {
                        logger.warn(String.format("Infraction code %s not found in infractions map. Skipping ticket.", infractionCode));
                        return;
                    }

                    // Convertir la fecha según el formato
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date ticketDate = dateFormat.parse(date);

                    // Verificar si la fecha está en el rango [from, to]
                    if (ticketDate.before(arguments.getFrom()) || ticketDate.after(arguments.getTo())) {
                        return;
                    }

                    String infractionDefinition = infraction.getDefinition();
                    tickets.putIfAbsent(id, new ReincidentTicketsDto(county, infractionDefinition, plate, ticketDate));
                } catch (Exception e) {
                    logger.error("Error processing ticket data", e);
                }
            } else {
                logger.error(String.format("Invalid line format, expected %d fields, found %d", Constants.FIELD_COUNT, fields.length));
            }
        };
    }

    @Override
    protected void executeJob() throws ExecutionException, InterruptedException {
        IMap<Integer, ReincidentTicketsDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_COUNTY_MAP.getName());

        JobTracker jobTracker = hazelcastInstance.getJobTracker(Constants.QUERY_3_JOB_TRACKER_NAME);
        KeyValueSource<Integer, ReincidentTicketsDto> source = KeyValueSource.fromMap(tickets);
        Job<Integer, ReincidentTicketsDto> job = jobTracker.newJob(source);

        ICompletableFuture<TreeSet<CountyReincidenceDto>> future = job
                .mapper(new CountyReincidenceMapper())
                .combiner(new CountyReincidenceCombinerFactory())
                .reducer(new CountyReincidenceReducerFactory())
                .submit(new CountyReincidenceCollator(arguments.getN(), tickets));

        TreeSet<CountyReincidenceDto> result = future.get();
        writeData(OUT_CSV_HEADER, result);
        tickets.clear();
    }
}