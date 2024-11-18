package ar.edu.itba.pod.grpc.reducer;

import java.util.HashSet;
import java.util.Set;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;

@SuppressWarnings("deprecation")
public class Infractions24x7ReducerFactory implements ReducerFactory<String, Set<String>, Boolean> {

    @Override
    public Reducer<Set<String>, Boolean> newReducer(String key) {
        return new Reducer<>() {
            private final Set<String> combinedDayHourSet = new HashSet<>();

            @Override
            public void reduce(Set<String> value) {
                combinedDayHourSet.addAll(value);
            }

            @Override
            public Boolean finalizeReduce() {
                return hasFullCoverage(combinedDayHourSet);
            }

            private boolean hasFullCoverage(Set<String> dayHourSet) {
                return dayHourSet.stream()
                    .map(entry -> entry.substring(0, 10)) // Extract "yyyy-MM-dd"
                    .distinct()
                    .allMatch(day -> hasAllHoursForDay(day, dayHourSet));
            }

            private boolean hasAllHoursForDay(String day, Set<String> dayHourSet) {
                for (int hour = 0; hour < 24; hour++) {
                    String expectedDayHour = String.format("%s-%02d", day, hour);
                    if (!dayHourSet.contains(expectedDayHour)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }
}

