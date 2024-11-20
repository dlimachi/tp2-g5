package ar.edu.itba.pod.grpc.mapper;

import ar.edu.itba.pod.grpc.dto.Infraction24x7RangeDto;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.Context;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SuppressWarnings("deprecation")
public class Infractions24x7Mapper implements Mapper<Integer, Infraction24x7RangeDto, String, String> {
    private final LocalDate fromDate;
    private final LocalDate toDate;

    public Infractions24x7Mapper(Date fromDate, Date toDate) {
        this.fromDate = convertToLocalDate(fromDate);
        this.toDate = convertToLocalDate(toDate);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public void map(Integer key, Infraction24x7RangeDto value, Context<String, String> context) {
        LocalDate date = LocalDate.of(value.getYear(), value.getMonth(), value.getDay());

        if (!date.isBefore(fromDate) && !date.isAfter(toDate)) {
            String dayHour = String.format("%s-%02d",
                    date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    value.getHour());
            context.emit(value.getInfractionDefinition(), dayHour);
        }
    }
}