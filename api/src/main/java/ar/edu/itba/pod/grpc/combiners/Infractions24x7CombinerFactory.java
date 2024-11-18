package ar.edu.itba.pod.grpc.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class Infractions24x7CombinerFactory implements CombinerFactory<String, String, Set<String>> {

    @Override
    public Combiner<String, Set<String>> newCombiner(String key) {
        return new Combiner<>() {
            private final Set<String> localDayHourSet = new HashSet<>();

            @Override
            public void combine(String value) {
                localDayHourSet.add(value);
            }

            @Override
            public Set<String> finalizeChunk() {
                return new HashSet<>(localDayHourSet);
            }

            @Override
            public void reset() {
                localDayHourSet.clear();
            }
        };
    }
}

