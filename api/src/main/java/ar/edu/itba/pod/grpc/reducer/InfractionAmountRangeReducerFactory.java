// InfractionAmountRangeReducerFactory.java
package ar.edu.itba.pod.grpc.reducer;

import ar.edu.itba.pod.grpc.dto.MinMaxAmount;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class InfractionAmountRangeReducerFactory implements ReducerFactory<String, MinMaxAmount, MinMaxAmount> {
    @Override
    public Reducer<MinMaxAmount, MinMaxAmount> newReducer(String key) {
        return new InfractionAmountRangeReducer();
    }

    private static class InfractionAmountRangeReducer extends Reducer<MinMaxAmount, MinMaxAmount> {
        private double min = Double.MAX_VALUE;
        private double max = Double.MIN_VALUE;

        @Override
        public void reduce(MinMaxAmount value) {
            min = Math.min(min, value.getMin());
            max = Math.max(max, value.getMax());
        }

        @Override
        public MinMaxAmount finalizeReduce() {
            return new MinMaxAmount(min, max);
        }
    }
}