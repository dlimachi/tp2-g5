package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.Infraction24x7RangeDto;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@SuppressWarnings("deprecation")
public class Infractions24x7Mapper implements Mapper<Integer, Infraction24x7RangeDto, String, String> {

    private final Date fromDate;
    private final Date toDate;

    public Infractions24x7Mapper(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public void map(Integer key, Infraction24x7RangeDto value, Context<String, String> context) {
        LocalDateTime dateTime = LocalDateTime.of(value.getYear(), value.getMonth(), value.getDay(), value.getHour(), 0);
        LocalDate date = dateTime.toLocalDate();

        /*if (!date.isBefore(fromDate) && !date.isAfter(toDate)) {
            String dayHour = String.format("%s-%02d", date, value.getHour());
            context.emit(value.getInfractionDefinition(), dayHour);
        }*/
    }
}
