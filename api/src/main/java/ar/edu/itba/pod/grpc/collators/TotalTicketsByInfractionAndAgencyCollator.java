package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.TicketByAgencyAndInfractionDto;
import com.hazelcast.mapreduce.Collator;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

@SuppressWarnings("deprecation")
public class TotalTicketsByInfractionAndAgencyCollator implements Collator<Map.Entry<String, Integer>, TreeSet<TicketByAgencyAndInfractionDto>> {

    @Override
    public TreeSet<TicketByAgencyAndInfractionDto> collate(Iterable<Map.Entry<String, Integer>> values) {
        TreeSet<TicketByAgencyAndInfractionDto> resultSet = new TreeSet<>(
            Comparator.comparing(TicketByAgencyAndInfractionDto::getTickets).reversed()
                .thenComparing(TicketByAgencyAndInfractionDto::getInfraction)
                .thenComparing(TicketByAgencyAndInfractionDto::getAgency));

        for (Map.Entry<String, Integer> entry : values) {
            String[] parts = entry.getKey().split(":");
            String definition = parts[0];
            String agency = parts[1];
            int totalTickets = entry.getValue();

            resultSet.add(new TicketByAgencyAndInfractionDto(definition, agency, totalTickets));
        }

        return resultSet;
    }
}

