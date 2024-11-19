package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.InfractionAmountRangeDto;
import ar.edu.itba.pod.grpc.dto.MinMaxAmount;
import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;


public class InfractionAmountRangeCollator implements Collator<Map.Entry<String, MinMaxAmount>, TreeSet<InfractionAmountRangeDto>> {
    private final int n;

    public InfractionAmountRangeCollator(int n) {
        this.n = n;
    }

    @Override
    public TreeSet<InfractionAmountRangeDto> collate(Iterable<Map.Entry<String, MinMaxAmount>> values) {
        TreeSet<InfractionAmountRangeDto> resultSet = new TreeSet<>(
                Comparator.<InfractionAmountRangeDto>comparingDouble(dto -> dto.getMaxAmount() - dto.getMinAmount())
                        .reversed()
                        .thenComparing(InfractionAmountRangeDto::getDefinition)
        );

        for (Map.Entry<String, MinMaxAmount> entry : values) {
            MinMaxAmount amount = entry.getValue();
            resultSet.add(new InfractionAmountRangeDto(
                    entry.getKey(),
                    amount.getMin(),
                    amount.getMax()
            ));
        }

        // Solo retornar los primeros N elementos
        TreeSet<InfractionAmountRangeDto> topN = new TreeSet<>(resultSet.comparator());
        int count = 0;
        for (InfractionAmountRangeDto dto : resultSet) {
            if (count >= n) break;
            topN.add(dto);
            count++;
        }

        return topN;
    }
}