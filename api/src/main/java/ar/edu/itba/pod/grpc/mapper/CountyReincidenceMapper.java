package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.ReincidentTicketsDto;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class CountyReincidenceMapper implements Mapper<Integer, ReincidentTicketsDto, String, String> {
    @Override
    public void map(Integer key, ReincidentTicketsDto value, Context<String, String> context) {
        // Clave compuesta de county:plate:infraction
        String compositeKey = value.getCounty() + ":" + value.getPlate() + ":" + value.getInfractionDefinition();
        context.emit(value.getCounty(), value.getPlate());
    }
}