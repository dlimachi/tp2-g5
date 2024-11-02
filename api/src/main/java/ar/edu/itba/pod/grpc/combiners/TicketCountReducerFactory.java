package ar.edu.itba.pod.grpc.combiners;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

public class TicketCountReducerFactory implements ReducerFactory<String, Integer, Integer> {
    @Override
    public Reducer<Integer, Integer> newReducer(String key) {
        return new Reducer<Integer, Integer>() {
            private volatile int sum = 0;

            @Override
            public void reduce(Integer count) {
                sum += count;
            }

            @Override
            public Integer finalizeReduce() {
                return sum;
            }
        };
    }
}
