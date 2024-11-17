package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.domain.Infraction;
import ar.edu.itba.pod.grpc.dto.InfractionRangeDto;
import com.hazelcast.core.IMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import utils.Constants;
import utils.csv.CsvMappingConfig;

public class Query5 extends Query{
  private static final String OUT_HEADER = "Infraction";

  @Override
  protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
    IMap<Integer, InfractionRangeDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_INFRACTION_AND_AGENCY_MAP.getName());
    IMap<String, Infraction> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());

    return (fields, config, id) -> {
      if (fields.length >= Constants.FIELD_COUNT) {
        try {
          String infractionCode = fields[config.getColumnIndex("violationCode")];
          String issueDate = fields[config.getColumnIndex("issueDate")];

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          LocalDateTime localDateTime = LocalDateTime.parse(issueDate, formatter);

          int year = localDateTime.getYear();
          int month = localDateTime.getMonthValue();
          int day = localDateTime.getDayOfMonth();
          int hour = localDateTime.getHour();
          int minute = localDateTime.getMinute();
          int second = localDateTime.getSecond();

          Infraction infraction = infractions.get(infractionCode);
          if (infraction == null) {
            logger.warn(String.format("Infraction code %s not found in infractions map. Skipping ticket.", infractionCode));
            return;
          }

          tickets.putIfAbsent(id, new InfractionRangeDto(infractionCode, year, month, day, hour));
        } catch (Exception e) {
          logger.error("Error processing ticket data", e);
        }
      } else {
        logger.error(String.format("Invalid line format, expected %d fields, found %d bla", Constants.FIELD_COUNT, fields.length));
      }
    };
  }

  @Override
  protected void executeJob() throws ExecutionException, InterruptedException {

  }
}
