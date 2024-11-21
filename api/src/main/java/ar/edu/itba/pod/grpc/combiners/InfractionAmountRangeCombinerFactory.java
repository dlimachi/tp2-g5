// InfractionAmountRangeCombinerFactory.java
package ar.edu.itba.pod.grpc.combiners;

import ar.edu.itba.pod.grpc.dto.MinMaxAmount;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class InfractionAmountRangeCombinerFactory implements CombinerFactory<String, Double, MinMaxAmount> {
    @Override
    public Combiner<Double, MinMaxAmount> newCombiner(String key) {
        return new InfractionAmountRangeCombiner();
    }

    private static class InfractionAmountRangeCombiner extends Combiner<Double, MinMaxAmount> {
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;

        @Override
        public void combine(Double amount) {
            min = Math.min(min, amount);
            max = Math.max(max, amount);
        }

        @Override
        public MinMaxAmount finalizeChunk() {
            return new MinMaxAmount(min, max);
        }
    }
}