package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.combiners.TicketCountCombinerFactory;
import ar.edu.itba.pod.grpc.combiners.TicketCountReducerFactory;
import ar.edu.itba.pod.grpc.collators.*;
import ar.edu.itba.pod.grpc.mapper.*;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Query1 implements Runnable {
    private static final String OUT_CSV_HEADER = "Infraction;Agency;Tickets\n";
    private static final String QUERY_1_CSV_NAME = "query1.csv";

    private final String jobName;
    private final HazelcastInstance hazelcast;
    private final IMap<String, Integer> ticketCountMap;
    private final Map<String, String> infractions;
    private final Map<String, String> agencies;
    private final String outPath;

    public Query1(String jobName, HazelcastInstance hazelcast,
                  IMap<String, Integer> ticketCountMap, Map<String, String> infractions,
                  Map<String, String> agencies, String outPath) {
        this.jobName = jobName;
        this.hazelcast = hazelcast;
        this.ticketCountMap = ticketCountMap;
        this.infractions = infractions;
        this.agencies = agencies;
        this.outPath = outPath;
    }

    @Override
    public void run() {
        JobTracker jobTracker = hazelcast.getJobTracker(jobName);
        KeyValueSource<String, Integer> source = KeyValueSource.fromMap(ticketCountMap);

        JobCompletableFuture<List<Map.Entry<String, Integer>>> future = jobTracker.newJob(source)
                .mapper(new InfractionAgencyMapper(infractions, agencies))
                .combiner(new TicketCountCombinerFactory())
                .reducer(new TicketCountReducerFactory())
                .submit(new TicketCountCollator());

        List<Map.Entry<String, Integer>> result;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        writeResultToFile(result);
    }

    private void writeResultToFile(List<Map.Entry<String, Integer>> result) {
        try (BufferedWriter buffWriter = new BufferedWriter(new FileWriter(outPath + QUERY_1_CSV_NAME))) {
            buffWriter.write(OUT_CSV_HEADER);
            for (Map.Entry<String, Integer> entry : result) {
                buffWriter.write(entry.getKey() + ";" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
