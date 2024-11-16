package ar.edu.itba.pod.grpc.mapper;
import ar.edu.itba.pod.grpc.dto.InfractionAndAgencyDto;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class InfractionDefinitionMapper implements Mapper<Integer, InfractionAndAgencyDto, String, Integer> {
  @Override
  public void map(Integer integer, InfractionAndAgencyDto dto, Context<String, Integer> context) {
    context.emit(dto.getDefinition(), 1);
  }
}

