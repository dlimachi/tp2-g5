package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.collators.TotalTicketsByInfractionAndAgencyCollator;
import ar.edu.itba.pod.grpc.combiners.TotalTicketsByInfractionAndAgencyCombinerFactory;
import ar.edu.itba.pod.grpc.combiners.TotalTicketsByInfractionAndAgencyReducerFactory;
import ar.edu.itba.pod.grpc.dto.InfractionAndAgencyDto;
import ar.edu.itba.pod.grpc.dto.InfractionDto;
import ar.edu.itba.pod.grpc.dto.TicketByAgencyAndInfractionDto;
import ar.edu.itba.pod.grpc.mapper.TotalTicketsByInfractionAndAgencyMapper;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import utils.Constants;
import utils.csv.CsvMappingConfig;

@SuppressWarnings("deprecation")
public class Query1 extends Query {
    private static final String OUT_CSV_HEADER = "Infraction;Agency;Tickets\n";
    private static final String QUERY_1_CSV_NAME = "query1.csv";

    @Override
    protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
        IMap<Integer, InfractionAndAgencyDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_INFRACTION_AND_AGENCY_MAP.getName());
        IMap<String, InfractionDto> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());
        IMap<String, String> agencies = hazelcastInstance.getMap(HazelcastCollections.AGENCIES_MAP.getName()); // Suponiendo que tienes este mapa para agencias

        return (fields, config, id) -> {
            if (fields.length >= Constants.FIELD_COUNT) {
                try {
                    String infractionCode = fields[config.getColumnIndex("infractionCode")];
                    String issuingAgency = fields[config.getColumnIndex("issuingAgency")];

                    InfractionDto infractionDto = infractions.get(infractionCode);
                    if (infractionDto == null) {
                        logger.warn(String.format("Infraction code %s not found in infractions map. Skipping ticket.", infractionCode));
                        return;
                    }

                    if (!agencies.containsKey(issuingAgency)) {
                        logger.warn(String.format("Issuing agency %s not found in agencies map. Skipping ticket.", issuingAgency));
                        return;
                    }

                    String infractionDefinition = infractionDto.getDefinition();
                    tickets.putIfAbsent(id, new InfractionAndAgencyDto(infractionDefinition, issuingAgency));
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
        IMap<Integer, InfractionAndAgencyDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_INFRACTION_AND_AGENCY_MAP.getName());
        IMap<String, InfractionDto> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());

        JobTracker jobTracker = hazelcastInstance.getJobTracker(Constants.QUERY_1_JOB_TRACKER_NAME);
        KeyValueSource<Integer, InfractionAndAgencyDto> source = KeyValueSource.fromMap(tickets);
        Job<Integer, InfractionAndAgencyDto> job = jobTracker.newJob(source);

        final ICompletableFuture<TreeSet<TicketByAgencyAndInfractionDto>> future = job
                .mapper(new TotalTicketsByInfractionAndAgencyMapper())
                .combiner(new TotalTicketsByInfractionAndAgencyCombinerFactory())
                .reducer(new TotalTicketsByInfractionAndAgencyReducerFactory())
                .submit(new TotalTicketsByInfractionAndAgencyCollator());

        TreeSet<TicketByAgencyAndInfractionDto> result = future.get();
        writeData(OUT_CSV_HEADER, result);
        tickets.clear();
        infractions.clear();
    }
}
