package ar.edu.itba.pod.grpc.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;

public class TicketCountCombinerFactory implements CombinerFactory<String, Integer, Integer> {
    @Override
    public Combiner<Integer, Integer> newCombiner(String key) {
        return new Combiner<Integer, Integer>() {
            private int sum = 0;

            @Override
            public void combine(Integer count) {
                sum += count;
            }

            @Override
            public Integer finalizeChunk() {
                int result = sum;
                sum = 0;
                return result;
            }
        };
    }
}
