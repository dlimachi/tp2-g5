package ar.edu.itba.pod.grpc.reducer;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import java.util.Set;
import java.util.HashSet;

public class InfractionRangeReducerFactory implements ReducerFactory<String, Set<Integer>, String> {

    @Override
    public Reducer<Set<Integer>, String> newReducer(String key) {
        return new InfractionRangeReducer();
    }

    public static class InfractionRangeReducer extends Reducer<Set<Integer>, String> {

        private Set<Integer> allHoursSet = new HashSet<>();

        @Override
        public void reduce(Set<Integer> hours) {
            allHoursSet.addAll(hours);
        }

        @Override
        public String finalizeReduce() {
            if (allHoursSet.size() == 24) {
                return "valid";
            }
            return "invalid";
        }
    }
}
