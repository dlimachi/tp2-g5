package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.AgencyIncomeDto;
import com.hazelcast.mapreduce.Collator;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@SuppressWarnings("deprecation")
public class YTDIncomeByAgencyCollator
    implements Collator<Map.Entry<String, Map<String, BigDecimal>>, List<AgencyIncomeDto>> {

  @Override
  public List<AgencyIncomeDto> collate(Iterable<Map.Entry<String, Map<String, BigDecimal>>> values) {
    return StreamSupport.stream(values.spliterator(), false)
        .flatMap(entry -> entry.getValue().entrySet().stream())
        .map(innerEntry -> {
          String[] parts = innerEntry.getKey().split("-");
          String agency = parts[0];
          int year = Integer.parseInt(parts[1]);
          int month = Integer.parseInt(parts[2]);
          int ytdIncome = innerEntry.getValue().intValue();

          return new AgencyIncomeDto(agency, year, month, ytdIncome);
        })
        .sorted(Comparator
            .comparing(AgencyIncomeDto::getAgency)
            .thenComparing(AgencyIncomeDto::getYear)
            .thenComparing(AgencyIncomeDto::getMonth))
        .collect(Collectors.toList());
  }
}

