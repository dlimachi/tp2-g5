package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.InfractionRangeDto;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Context;

@SuppressWarnings("deprecation")
public class InfractionRangeMapper implements Mapper<Integer, InfractionRangeDto, String, InfractionRangeDto> {

    @Override
    public void map(Integer key, InfractionRangeDto infractionRangeDto, Context<String, InfractionRangeDto> context) {
        String keyString = infractionRangeDto.getYear() + "-" + infractionRangeDto.getMonth() + "-" + infractionRangeDto.getDay();
        context.emit(keyString, infractionRangeDto);
    }
}
