package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.collators.InfractionAmountRangeCollator;
import ar.edu.itba.pod.grpc.combiners.InfractionAmountRangeCombinerFactory;
import ar.edu.itba.pod.grpc.domain.Infraction;
import ar.edu.itba.pod.grpc.dto.InfractionAmountDto;
import ar.edu.itba.pod.grpc.dto.InfractionAmountRangeDto;
import ar.edu.itba.pod.grpc.mapper.InfractionAmountRangeMapper;
import ar.edu.itba.pod.grpc.reducer.InfractionAmountRangeReducerFactory;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import utils.Constants;
import utils.csv.CsvMappingConfig;

import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

public class Query4 extends Query {
    private static final String OUT_CSV_HEADER = "Infraction;Min;Max;Diff\n";

    @Override
    protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
        IMap<Integer, InfractionAmountDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_INFRACTION_AND_AMOUNT_MAP.getName());
        IMap<String, Infraction> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());

        return (fields, config, id) -> {
            if (fields.length >= Constants.FIELD_COUNT) {
                try {
                    String infractionCode = fields[config.getColumnIndex("infractionId")];
                    String issuingAgency = fields[config.getColumnIndex("issuingAgency")];
                    double amount = Double.parseDouble(fields[config.getColumnIndex("fineAmount")]);

                    // Solo procesar tickets de la agencia especificada
                    if (!issuingAgency.equals(arguments.getAgency().replace("_", " "))) {
                        return;
                    }

                    Infraction infraction = infractions.get(infractionCode);
                    if (infraction == null) {
                        logger.warn("Infraction code {} not found in infractions map. Skipping ticket.", infractionCode);
                        return;
                    }

                    tickets.putIfAbsent(id, new InfractionAmountDto(infraction.getDefinition(), amount));
                } catch (Exception e) {
                    logger.error("Error processing ticket data", e);
                }
            }
        };
    }

    @Override
    protected void executeJob() throws ExecutionException, InterruptedException {
        IMap<Integer, InfractionAmountDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_INFRACTION_AND_AMOUNT_MAP.getName());

        JobTracker jobTracker = hazelcastInstance.getJobTracker(Constants.QUERY_4_JOB_TRACKER_NAME);
        KeyValueSource<Integer, InfractionAmountDto> source = KeyValueSource.fromMap(tickets);
        Job<Integer, InfractionAmountDto> job = jobTracker.newJob(source);

        ICompletableFuture<TreeSet<InfractionAmountRangeDto>> future = job
                .mapper(new InfractionAmountRangeMapper())
                .combiner(new InfractionAmountRangeCombinerFactory())
                .reducer(new InfractionAmountRangeReducerFactory())
                .submit(new InfractionAmountRangeCollator(arguments.getN()));

        TreeSet<InfractionAmountRangeDto> result = future.get();
        writeData(OUT_CSV_HEADER, result);
        tickets.clear();
    }
}