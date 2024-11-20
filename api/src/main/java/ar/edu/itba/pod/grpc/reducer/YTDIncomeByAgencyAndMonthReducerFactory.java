package ar.edu.itba.pod.grpc.reducer;

import com.hazelcast.core.Client;
import com.hazelcast.mapreduce.Reducer;
import com.hazelcast.mapreduce.ReducerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("deprecation")
public class YTDIncomeByAgencyAndMonthReducerFactory implements ReducerFactory<String, BigDecimal, Map<String, BigDecimal>> {
    @Override
    public Reducer<BigDecimal, Map<String, BigDecimal>> newReducer(String key) {
        return new YTDIncomeByAgencyAndMonthReducer(key);
    }

    private static class YTDIncomeByAgencyAndMonthReducer extends Reducer<BigDecimal, Map<String, BigDecimal>> {
        private final String key;
        private Map<Integer, Map<Integer, BigDecimal>> yearMonthTotals; // year -> (month -> amount)

        public YTDIncomeByAgencyAndMonthReducer(String key) {
            this.key = key;
        }

        @Override
        public void beginReduce() {
            yearMonthTotals = new TreeMap<>();
        }

        @Override
        public void reduce(BigDecimal value) {
            String[] parts = key.split("-");
            int year = Integer.parseInt(parts[1]);
            int month = Integer.parseInt(parts[2]);

            yearMonthTotals
                    .computeIfAbsent(year, k -> new TreeMap<>())
                    .merge(month, value, BigDecimal::add);
        }

        @Override
        public Map<String, BigDecimal> finalizeReduce() {
            Map<String, BigDecimal> ytdResults = new TreeMap<>();
            String agency = key.split("-")[0];

            for (Map.Entry<Integer, Map<Integer, BigDecimal>> yearEntry : yearMonthTotals.entrySet()) {
                int year = yearEntry.getKey();
                BigDecimal yearTotal = BigDecimal.ZERO;

                for (Map.Entry<Integer, BigDecimal> monthEntry : yearEntry.getValue().entrySet()) {
                    int month = monthEntry.getKey();
                    yearTotal = yearTotal.add(monthEntry.getValue());
                    String resultKey = String.format("%s-%d-%d", agency, year, month);
                    ytdResults.put(resultKey, yearTotal);
                }
            }

            return ytdResults;
        }
    }
}