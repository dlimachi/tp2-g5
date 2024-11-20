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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    return (fields, config, id) -> {
      if (fields.length >= Constants.FIELD_COUNT) {
        try {
          String issueDate = fields[config.getColumnIndex("issueDate")];
          String infractionCode = fields[config.getColumnIndex("infractionId")];

          if (issueDate == null || issueDate.trim().isEmpty()) {
            logger.warn("Invalid date format, skipping ticket");
            return;
          }

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          LocalDateTime localDateTime = LocalDateTime.parse(issueDate, formatter);

          Infraction infraction = infractions.get(infractionCode);
          if (infraction == null) {
            logger.warn("Infraction code {} not found, skipping ticket.", infractionCode);
            return;
          }

          tickets.putIfAbsent(id, new Infraction24x7RangeDto(
                  infraction.getDefinition(),
                  localDateTime.getYear(),
                  localDateTime.getMonthValue(),
                  localDateTime.getDayOfMonth(),
                  localDateTime.getHour()
          ));
        } catch (DateTimeParseException e) {
          logger.error("Error parsing date: {}", e.getMessage());
        } catch (Exception e) {
          logger.error("Error processing ticket data: {}", e.getMessage());
        }
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
}
