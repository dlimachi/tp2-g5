package ar.edu.itba.pod.grpc.collators;

import ar.edu.itba.pod.grpc.dto.InfractionRangeDto;
import com.hazelcast.mapreduce.Collator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfractionRangeCollator implements Collator<Map.Entry<String, String>, List<InfractionRangeDto>> {

    @Override
    public List<InfractionRangeDto> collate(Iterable<Map.Entry<String, String>> values) {
        List<InfractionRangeDto> result = new ArrayList<>();

        for (Map.Entry<String, String> entry : values) {
            String infractionKey = entry.getKey();  // Esto es el 'year-month-day'
            String validity = entry.getValue();    // Esto es "valid" o "invalid"

            if ("valid".equals(validity)) {
                // Si es "valid", construimos un InfractionRangeDto y lo agregamos a la lista
                InfractionRangeDto dto = new InfractionRangeDto();
                dto.setInfractionCode(infractionKey);
                dto.setValidity(validity);  // Aquí indicamos que la infracción es válida
                result.add(dto);
            }
        }

        return result;
    }
}
