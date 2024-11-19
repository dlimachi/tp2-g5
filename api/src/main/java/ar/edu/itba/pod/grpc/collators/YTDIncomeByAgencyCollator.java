package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.AgencyIncomeDto;
import com.hazelcast.mapreduce.Collator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
public class YTDIncomeByAgencyCollator implements Collator<Map.Entry<String, Map<String, BigDecimal>>, List<AgencyIncomeDto>> {

    @Override
    public List<AgencyIncomeDto> collate(Iterable<Map.Entry<String, Map<String, BigDecimal>>> values) {
        List<AgencyIncomeDto> result = new ArrayList<>();

        for (Map.Entry<String, Map<String, BigDecimal>> entry : values) {
            String agency = entry.getKey().split("-")[0];
            Map<String, BigDecimal> monthlyData = entry.getValue();

            BigDecimal cumulativeSum = BigDecimal.ZERO;

            List<Map.Entry<String, BigDecimal>> sortedEntries = new ArrayList<>(monthlyData.entrySet());
            sortedEntries.sort(Comparator.comparing(e -> e.getKey()));

            for (Map.Entry<String, BigDecimal> monthEntry : sortedEntries) {
                String yearMonth = monthEntry.getKey();
                BigDecimal monthlyIncome = monthEntry.getValue();

                cumulativeSum = cumulativeSum.add(monthlyIncome);

                String[] ymParts = yearMonth.split("-");
                int year = Integer.parseInt(ymParts[0]);
                int month = Integer.parseInt(ymParts[1]);

                result.add(new AgencyIncomeDto(agency, year, month, cumulativeSum.intValue()));
            }
        }

        return result.stream()
                .sorted(Comparator.comparing(AgencyIncomeDto::getAgency)
                        .thenComparing(AgencyIncomeDto::getYear)
                        .thenComparing(AgencyIncomeDto::getMonth))
                .collect(Collectors.toList());
    }
}
