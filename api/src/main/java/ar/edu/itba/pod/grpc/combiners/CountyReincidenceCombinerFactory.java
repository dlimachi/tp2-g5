package ar.edu.itba.pod.grpc.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import java.util.HashSet;
import java.util.Set;

public class CountyReincidenceCombinerFactory implements CombinerFactory<String, String, Set<String>> {
    @Override
    public Combiner<String, Set<String>> newCombiner(String key) {
        return new CountyReincidenceCombiner();
    }

    private static class CountyReincidenceCombiner extends Combiner<String, Set<String>> {
        private final Set<String> plates = new HashSet<>();

        @Override
        public void combine(String plate) {
            plates.add(plate);
        }

        @Override
        public Set<String> finalizeChunk() {
            return new HashSet<>(plates);
        }
    }
}