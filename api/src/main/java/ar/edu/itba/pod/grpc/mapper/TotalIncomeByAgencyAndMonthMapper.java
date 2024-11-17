package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.AgencyAndDateDto;
import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;
import java.math.BigDecimal;

@SuppressWarnings("deprecation")
public class TotalIncomeByAgencyAndMonthMapper implements Mapper<Integer, AgencyAndDateDto, String, BigDecimal> {

    @Override
    public void map(Integer integer, AgencyAndDateDto agencyDto, Context<String, BigDecimal> context) {
        String key = agencyDto.getAgency() + "-" + agencyDto.getYear() + "-" + agencyDto.getMonth();
        context.emit(key, BigDecimal.valueOf(agencyDto.getFineAmount()));
    }
}