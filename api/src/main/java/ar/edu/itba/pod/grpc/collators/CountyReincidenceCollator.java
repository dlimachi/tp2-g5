package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.CountyReincidenceDto;
import ar.edu.itba.pod.grpc.dto.ReincidentTicketsDto;
import ar.edu.itba.pod.grpc.reducer.CountyStats;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Collator;

import java.util.*;

public class CountyReincidenceCollator implements Collator<Map.Entry<String, CountyStats>, TreeSet<CountyReincidenceDto>> {
    private final int n;
    private final IMap<Integer, ReincidentTicketsDto> tickets;

    public CountyReincidenceCollator(int n, IMap<Integer, ReincidentTicketsDto> tickets) {
        this.n = n;
        this.tickets = tickets;
    }

    @Override
    public TreeSet<CountyReincidenceDto> collate(Iterable<Map.Entry<String, CountyStats>> entries) {
        TreeSet<CountyReincidenceDto> result = new TreeSet<>();

        for (Map.Entry<String, CountyStats> entry : entries) {
            String county = entry.getKey();
            Set<String> totalPlates = entry.getValue().getPlates();

            // Calcular reincidentes
            Set<String> reincidentPlates = calculateReincidentPlates(county, totalPlates);

            if (!totalPlates.isEmpty()) {
                double percentage = (reincidentPlates.size() * 100.0) / totalPlates.size();
                result.add(new CountyReincidenceDto(county, percentage));
            }
        }

        return result;
    }

    private Set<String> calculateReincidentPlates(String county, Set<String> plates) {
        Map<String, Map<String, Integer>> plateInfractionCount = new HashMap<>();
        Set<String> reincidentPlates = new HashSet<>();

        for (ReincidentTicketsDto ticket : tickets.values()) {
            if (ticket.getCounty().equals(county) && plates.contains(ticket.getPlate())) {
                String key = ticket.getPlate() + ":" + ticket.getInfractionDefinition();
                plateInfractionCount
                        .computeIfAbsent(key, k -> new HashMap<>())
                        .merge(ticket.getPlate(), 1, Integer::sum);

                if (plateInfractionCount.get(key).get(ticket.getPlate()) >= n) {
                    reincidentPlates.add(ticket.getPlate());
                }
            }
        }

        return reincidentPlates;
    }
}