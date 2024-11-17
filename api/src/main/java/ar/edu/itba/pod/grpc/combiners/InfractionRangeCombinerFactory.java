package ar.edu.itba.pod.grpc.combiners;

import ar.edu.itba.pod.grpc.dto.InfractionRangeDto;
import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("deprecation")
public class InfractionRangeCombinerFactory implements CombinerFactory<String, InfractionRangeDto, Set<Integer>> {

    @Override
    public Combiner<InfractionRangeDto, Set<Integer>> newCombiner(String key) {
        return new InfractionRangeCombiner();
    }

    public static class InfractionRangeCombiner extends Combiner<InfractionRangeDto, Set<Integer>> {

        private Set<Integer> combinedHours = new HashSet<>();

        @Override
        public void combine(InfractionRangeDto infractionRangeDto) {
            // Acumular las horas de la infracción
            combinedHours.add(infractionRangeDto.getHour());
        }

        @Override
        public Set<Integer> finalizeChunk() {
            // Devolver el conjunto de horas acumuladas
            return combinedHours;
        }
    }
}

