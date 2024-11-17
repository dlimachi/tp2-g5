package ar.edu.itba.pod.grpc.combiners;

import com.hazelcast.mapreduce.Combiner;
import com.hazelcast.mapreduce.CombinerFactory;
import java.math.BigDecimal;

@SuppressWarnings("deprecation")
public class TotalIncomeByAgencyAndMonthCombinerFactory implements CombinerFactory<String, BigDecimal, BigDecimal> {

    @Override
    public Combiner<BigDecimal, BigDecimal> newCombiner(String key) {
        return new Combiner<BigDecimal, BigDecimal>() {
            private BigDecimal sum = BigDecimal.ZERO;

            @Override
            public void combine(BigDecimal value) {
                sum = sum.add(value);
            }

            @Override
            public BigDecimal finalizeChunk() {
                return sum;
            }

            @Override
            public void reset() {
                sum = BigDecimal.ZERO;
            }
        };
    }
}
