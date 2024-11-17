package ar.edu.itba.pod.grpc.reducer;

import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("deprecation")
public class YTDIncomeByAgencyAndMonthReducerFactory
    implements ReducerFactory<String, BigDecimal, Map<String, BigDecimal>> {

    @Override
    public Reducer<BigDecimal, Map<String, BigDecimal>> newReducer(String key) {
        return new YTDReducer(key);
    }

    private static class YTDReducer extends Reducer<BigDecimal, Map<String, BigDecimal>> {
        private final String initialKey;
        private final Map<String, BigDecimal> monthlyTotals = new TreeMap<>();
        private BigDecimal cumulativeSum = BigDecimal.ZERO;

        public YTDReducer(String key) {
            this.initialKey = key;
        }

        @Override
        public void reduce(BigDecimal value) {
            monthlyTotals.put(initialKey, monthlyTotals.getOrDefault(initialKey, BigDecimal.ZERO).add(value));
        }

        @Override
        public Map<String, BigDecimal> finalizeReduce() {
            cumulativeSum = BigDecimal.ZERO;
            Map<String, BigDecimal> ytdResults = new TreeMap<>();

            for (Map.Entry<String, BigDecimal> entry : monthlyTotals.entrySet()) {
                cumulativeSum = cumulativeSum.add(entry.getValue());
                ytdResults.put(entry.getKey(), cumulativeSum);
            }
            return ytdResults;
        }
    }
}

