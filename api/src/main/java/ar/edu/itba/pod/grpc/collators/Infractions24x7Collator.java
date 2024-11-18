package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.InfractionDto;
import com.hazelcast.mapreduce.Collator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("deprecation")
public class Infractions24x7Collator implements Collator<Map.Entry<String, Boolean>, Set<InfractionDto>> {

    @Override
    public Set<InfractionDto> collate(Iterable<Map.Entry<String, Boolean>> entries) {
        Set<InfractionDto> infractions24x7 = new TreeSet<>();

        for (Map.Entry<String, Boolean> entry : entries) {
            if (entry.getValue()) {
                infractions24x7.add(new InfractionDto(entry.getKey()));
            }
        }

        return infractions24x7;
    }
}

