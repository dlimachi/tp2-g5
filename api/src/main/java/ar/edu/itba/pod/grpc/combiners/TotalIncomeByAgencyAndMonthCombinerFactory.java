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
            //agencia1-2004-10 -> 300
            //agencia1-2005-12 -> 100
            //agencia1-2004-11 -> 200
            //agencia2-2004-12 -> 100

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
