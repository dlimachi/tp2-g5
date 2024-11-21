package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.collators.Infractions24x7Collator;
import ar.edu.itba.pod.grpc.combiners.Infractions24x7CombinerFactory;
import ar.edu.itba.pod.grpc.domain.Infraction;
import ar.edu.itba.pod.grpc.dto.InfractionDto;
import ar.edu.itba.pod.grpc.dto.Infraction24x7RangeDto;
import ar.edu.itba.pod.grpc.mapper.Infractions24x7Mapper;
import ar.edu.itba.pod.grpc.reducer.Infractions24x7ReducerFactory;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import utils.Constants;
import utils.csv.CsvMappingConfig;

public class Query5 extends Query{

  private static final String OUT_CSV_HEADER = "Infraction";

  @Override
  protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
    IMap<Integer, Infraction24x7RangeDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_24_X_7_MAP.getName());
    IMap<String, Infraction> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());

    logger.info("fechas: {} {}", arguments.getFrom(), arguments.getTo());

    return (fields, config, id) -> {
      logger.info(fields[config.getColumnIndex("infractionId")]);

      if (fields.length >= Constants.FIELD_COUNT) {
        try {
          String issueDate = fields[config.getColumnIndex("issueDate")];
          String infractionCode = fields[config.getColumnIndex("infractionId")];

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          LocalDateTime localDateTime = LocalDateTime.parse(issueDate, formatter);

          LocalDate date = localDateTime.toLocalDate();
          if (date.isBefore(arguments.getFrom()) || date.isAfter(arguments.getTo())) {
            return;
          }

          int year = localDateTime.getYear();
          int month = localDateTime.getMonthValue();
          int day = localDateTime.getDayOfMonth();
          int hour = localDateTime.getHour();

          Infraction infraction = infractions.get(infractionCode);
          if (infraction == null) {
            logger.warn(String.format("Infraction code %s not found in infractions map. Skipping ticket.", infractionCode));
            return;
          }
          String infractionDefinition = infraction.getDefinition();


          tickets.putIfAbsent(id, new Infraction24x7RangeDto(infractionDefinition, year, month, day, hour));
        } catch (Exception e) {
          logger.error("Error processing ticket data", e);
        }
      } else {
        logger.error(String.format("Invalid line format, expected %d fields, found %d", Constants.FIELD_COUNT, fields.length));
      }
    };
  }

  @Override
  protected void executeJob() throws ExecutionException, InterruptedException {
    IMap<Integer, Infraction24x7RangeDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_24_X_7_MAP.getName());

    JobTracker jobTracker = hazelcastInstance.getJobTracker(Constants.QUERY_5_JOB_TRACKER_NAME);
    KeyValueSource<Integer, Infraction24x7RangeDto> source = KeyValueSource.fromMap(tickets);
    Job<Integer, Infraction24x7RangeDto> job = jobTracker.newJob(source);

    final ICompletableFuture<Set<InfractionDto>> future = job
        .mapper(new Infractions24x7Mapper(arguments.getFrom(), arguments.getTo()))
        .combiner(new Infractions24x7CombinerFactory())
        .reducer(new Infractions24x7ReducerFactory())
        .submit(new Infractions24x7Collator());

    Set<InfractionDto> result = future.get();
    writeData(OUT_CSV_HEADER, result);
    tickets.clear();
  }

  private static LocalDate convertToLocalDate(Date date) {
    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
  }

}
