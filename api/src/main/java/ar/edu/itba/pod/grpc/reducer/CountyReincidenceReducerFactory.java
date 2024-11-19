package ar.edu.itba.pod.grpc.reducer;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CountyReincidenceReducerFactory implements ReducerFactory<String, Set<String>, CountyStats> {
    @Override
    public Reducer<Set<String>, CountyStats> newReducer(String key) {
        return new CountyReincidenceReducer();
    }

    private static class CountyReincidenceReducer extends Reducer<Set<String>, CountyStats> {
        private final Set<String> allPlates = new HashSet<>();
        private final Set<String> reincidentPlates = new HashSet<>();

        @Override
        public void reduce(Set<String> plates) {
            allPlates.addAll(plates);
            // La lógica de reincidencia se maneja en el collator
        }

        @Override
        public CountyStats finalizeReduce() {
            return new CountyStats(allPlates);
        }
    }
}

