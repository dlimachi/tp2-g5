package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.InfractionAndAgencyDto;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

@SuppressWarnings("deprecation")
public class TotalTicketsByInfractionAndAgencyMapper implements Mapper<Integer, InfractionAndAgencyDto, String, Integer> {
  @Override
  public void map(Integer ticketId, InfractionAndAgencyDto ticket, Context<String, Integer> context) {
      String compositeKey = ticket.getDefinition() + ":" + ticket.getIssuingAgency();
      context.emit(compositeKey, 1);
  }

}