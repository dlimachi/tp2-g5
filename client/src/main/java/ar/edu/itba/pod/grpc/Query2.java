package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.collators.YTDIncomeByAgencyCollator;
import ar.edu.itba.pod.grpc.combiners.TotalIncomeByAgencyAndMonthCombinerFactory;
import ar.edu.itba.pod.grpc.domain.Agency;
import ar.edu.itba.pod.grpc.dto.AgencyAndDateDto;
import ar.edu.itba.pod.grpc.dto.AgencyIncomeDto;
import ar.edu.itba.pod.grpc.mapper.TotalIncomeByAgencyAndMonthMapper;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.IMap;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyValueSource;
import java.util.List;
import java.util.concurrent.ExecutionException;
import ar.edu.itba.pod.grpc.reducer.YTDIncomeByAgencyAndMonthReducerFactory;
import utils.Constants;
import utils.csv.CsvMappingConfig;

@SuppressWarnings("deprecation")
public class Query2 extends Query {

  private static final String OUT_CSV_HEADER = "Agency;Year;Month;YTD\n";


  @Override
  protected TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer() {
    IMap<Integer, AgencyAndDateDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_AGENCY_AND_DATE_MAP.getName());
    IMap<String, Agency> agencies = hazelcastInstance.getMap(HazelcastCollections.AGENCIES_MAP.getName());

    return (fields, config, id) -> {
      if (fields.length >= Constants.FIELD_COUNT) {
        try {
          String issuingAgency = fields[config.getColumnIndex("issuingAgency")];
          String date = fields[config.getColumnIndex("issueDate")];
          double amount = Double.parseDouble(fields[config.getColumnIndex("fineAmount")]);
          String[] dateParts = date.split("-");
          int year = Integer.parseInt(dateParts[0]);
          int month = Integer.parseInt(dateParts[1]);

          if (!agencies.containsKey(issuingAgency)) {
            logger.warn(String.format("Issuing agency %s not found in agencies map. Skipping ticket.", issuingAgency));
            return;
          }

          tickets.putIfAbsent(id, new AgencyAndDateDto(issuingAgency, amount, year, month));
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
    IMap<Integer, AgencyAndDateDto> tickets = hazelcastInstance.getMap(HazelcastCollections.TICKETS_BY_AGENCY_AND_DATE_MAP.getName());
    IMap<String, Agency> agencies = hazelcastInstance.getMap(HazelcastCollections.AGENCIES_MAP.getName());

    JobTracker jobTracker = hazelcastInstance.getJobTracker(Constants.QUERY_2_JOB_TRACKER_NAME);
    KeyValueSource<Integer, AgencyAndDateDto> source = KeyValueSource.fromMap(tickets);
    Job<Integer, AgencyAndDateDto> job = jobTracker.newJob(source);

    ICompletableFuture<List<AgencyIncomeDto>> future = job
        .mapper(new TotalIncomeByAgencyAndMonthMapper())
        .combiner(new TotalIncomeByAgencyAndMonthCombinerFactory())
        .reducer(new YTDIncomeByAgencyAndMonthReducerFactory())
        .submit(new YTDIncomeByAgencyCollator());

    List<AgencyIncomeDto> result = future.get();
    writeData(OUT_CSV_HEADER, result);
    tickets.clear();
    agencies.clear();
  }
}
