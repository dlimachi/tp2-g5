package ar.edu.itba.pod.grpc.reducer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

@SuppressWarnings("deprecation")
public class Infractions24x7ReducerFactory implements ReducerFactory<String, Set<String>, Boolean> {
    @Override
    public Reducer<Set<String>, Boolean> newReducer(String key) {
        return new Reducers24x7();
    }

    private static class Reducers24x7 extends Reducer<Set<String>, Boolean> {
        private final Map<String, Set<Integer>> dayHoursMap = new HashMap<>();

        @Override
        public void reduce(Set<String> dayHours) {
            for (String dayHour : dayHours) {
                String[] parts = dayHour.split("-");
                String date = String.format("%s-%s-%s", parts[0], parts[1], parts[2]);
                int hour = Integer.parseInt(parts[3]);

                dayHoursMap.computeIfAbsent(date, k -> new HashSet<>()).add(hour);
            }
        }

        @Override
        public Boolean finalizeReduce() {
            // Si no hay datos, retornar false
            if (dayHoursMap.isEmpty()) {
                return false;
            }

            // Verificar que cada día tenga las 24 horas
            for (Map.Entry<String, Set<Integer>> entry : dayHoursMap.entrySet()) {
                Set<Integer> hoursForDay = entry.getValue();

                // Si algún día no tiene las 24 horas, retornar false
                if (hoursForDay.size() != 24) {
                    return false;
                }

                // Verificar que estén todas las horas de 0 a 23
                for (int hour = 0; hour < 24; hour++) {
                    if (!hoursForDay.contains(hour)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}