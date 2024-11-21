package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.InfractionAmountDto;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

public class InfractionAmountRangeMapper implements Mapper<Integer, InfractionAmountDto, String, Double> {
    @Override
    public void map(Integer ticketId, InfractionAmountDto ticket, Context<String, Double> context) {
        context.emit(ticket.getInfractionDefinition(), ticket.getAmount());
    }
}
