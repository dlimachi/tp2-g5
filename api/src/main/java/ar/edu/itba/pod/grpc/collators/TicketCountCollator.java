package ar.edu.itba.pod.grpc.collators;

import com.hazelcast.mapreduce.Collator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketCountCollator implements Collator<Map.Entry<String, Integer>, List<Map.Entry<String, Integer>>> {
    @Override
    public List<Map.Entry<String, Integer>> collate(Iterable<Map.Entry<String, Integer>> values) {
        return ((List<Map.Entry<String, Integer>>) values).stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(e -> e.getKey().split(";")[0])
                        .thenComparing(e -> e.getKey().split(";")[1]))
                .collect(Collectors.toList());
    }
}
