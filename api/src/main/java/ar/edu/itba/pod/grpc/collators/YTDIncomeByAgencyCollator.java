package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.AgencyIncomeDto;
import com.hazelcast.mapreduce.Collator;
import java.math.BigDecimal;
import java.util.*;

public class YTDIncomeByAgencyCollator implements Collator<Map.Entry<String, Map<String, BigDecimal>>, List<AgencyIncomeDto>> {
    @Override
    public List<AgencyIncomeDto> collate(Iterable<Map.Entry<String, Map<String, BigDecimal>>> values) {
        Map<String, Map<Integer, TreeMap<Integer, BigDecimal>>> agencyYearMonthMap = new TreeMap<>();

        // Recolectar datos
        for (Map.Entry<String, Map<String, BigDecimal>> entry : values) {
            String fullKey = entry.getKey();
            String agency = fullKey.split("-")[0];  // Extraer solo el nombre de la agencia
            Map<String, BigDecimal> monthlyData = entry.getValue();

            for (Map.Entry<String, BigDecimal> monthEntry : monthlyData.entrySet()) {
                String[] parts = monthEntry.getKey().split("-");
                int year = Integer.parseInt(parts[1]);
                int month = Integer.parseInt(parts[2]);
                BigDecimal amount = monthEntry.getValue();

                agencyYearMonthMap
                        .computeIfAbsent(agency, k -> new TreeMap<>())
                        .computeIfAbsent(year, k -> new TreeMap<>())
                        .put(month, amount);
            }
        }

        List<AgencyIncomeDto> result = new ArrayList<>();

        // Procesar agencia por agencia
        for (Map.Entry<String, Map<Integer, TreeMap<Integer, BigDecimal>>> agencyEntry : agencyYearMonthMap.entrySet()) {
            String agency = agencyEntry.getKey(); // Ya está limpia porque la limpiamos arriba

            // Procesar año por año
            for (Map.Entry<Integer, TreeMap<Integer, BigDecimal>> yearEntry : agencyEntry.getValue().entrySet()) {
                int year = yearEntry.getKey();
                BigDecimal yearTotal = BigDecimal.ZERO;

                // Procesar mes por mes, acumulando el YTD
                for (Map.Entry<Integer, BigDecimal> monthEntry : yearEntry.getValue().entrySet()) {
                    int month = monthEntry.getKey();
                    yearTotal = yearTotal.add(monthEntry.getValue());
                    result.add(new AgencyIncomeDto(agency, year, month, yearTotal.intValue()));
                }
            }
        }

        Collections.sort(result);
        return result;
    }
}