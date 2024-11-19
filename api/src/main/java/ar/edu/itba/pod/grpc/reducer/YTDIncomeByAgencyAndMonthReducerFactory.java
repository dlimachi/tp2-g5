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

        protected static final Logger logger = LoggerFactory.getLogger(YTDIncomeByAgencyAndMonthReducerFactory.class);

        private Map<String, BigDecimal> monthlyTotals;
        private BigDecimal cumulativeSum;

        public YTDIncomeByAgencyAndMonthReducer(String key) {
            this.key = key;
        }

        @Override
        public void beginReduce() {
            monthlyTotals = new TreeMap<>();  // Orden cronológico
            cumulativeSum = BigDecimal.ZERO;
        }

        @Override
        public void reduce(BigDecimal value) {
            String currentMonthKey = extractYearMonthFromKey(key);
            logger.info("la key es : " + key + "y current : " + currentMonthKey);
            monthlyTotals.merge(currentMonthKey, value, BigDecimal::add);
        }

        @Override
        public Map<String, BigDecimal> finalizeReduce() {
            Map<String, BigDecimal> ytdResults = new TreeMap<>();

            for (Map.Entry<String, BigDecimal> entry : monthlyTotals.entrySet()) {
                cumulativeSum = cumulativeSum.add(entry.getValue());
                logger.info("cumulative sum es " + cumulativeSum);
                ytdResults.put(entry.getKey(), cumulativeSum);
                logger.info("se guardo {}", ytdResults.get(entry.getKey()));
            }

            return ytdResults;
        }

        private String extractYearMonthFromKey(String fullKey) {
            String[] parts = fullKey.split("-");
            return parts[1] + "-" + parts[2];  // "YYYY-MM"
        }
    }
}
