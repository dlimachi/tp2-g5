package ar.edu.itba.pod.grpc;

import ar.edu.itba.pod.grpc.client.Client;
import ar.edu.itba.pod.grpc.domain.Agency;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Arguments;
import utils.Constants;
import ar.edu.itba.pod.grpc.domain.Infraction;
import utils.csv.CsvFileIterator;
import utils.csv.CsvFileType;
import utils.csv.CsvMappingConfig;
import utils.csv.CsvWriter;

public abstract class Query {
    protected HazelcastInstance hazelcastInstance;
    protected Arguments arguments;
    protected static final Logger logger = LoggerFactory.getLogger(Client.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat(Constants.TIMESTAMP_LOGS_DATE_FORMAT);
    String timestampLogFilePath;
    String queryOutputFilePath;

    public void execute(HazelcastInstance hazelcastInstance, Arguments arguments) {
        this.hazelcastInstance = hazelcastInstance;
        this.arguments = arguments;

        timestampLogFilePath = String.format(arguments.getOutPath() + Constants.TIMESTAMP_LOGS_FILE_TEMPLATE, arguments.getQuery());
        queryOutputFilePath = String.format(arguments.getOutPath() + Constants.QUERY_OUTPUT_FILE_TEMPLATE, arguments.getQuery());

        // Log and write the start time of data loading
        long startLoadingTime = System.currentTimeMillis();
        String startLoadingLog = sdf.format(new Date(startLoadingTime)) + Constants.FILE_READ_START_MESSAGE;
        logAndWrite(startLoadingLog, timestampLogFilePath);

        loadData();

        // Log and write the end time of data loading
        long endLoadingTime = System.currentTimeMillis();
        String endLoadingLog = sdf.format(new Date(endLoadingTime)) + Constants.FILE_READ_END_MESSAGE;
        logAndWrite(endLoadingLog, timestampLogFilePath);

        // Log and write the start time of job execution
        long startJobTime = System.currentTimeMillis();
        String startJobLog = sdf.format(new Date(startJobTime)) + Constants.MAP_REDUCE_START_MESSAGE;
        logAndWrite(startJobLog, timestampLogFilePath);

        try {
            executeJob();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Log and write the end time of job execution
        long endJobTime = System.currentTimeMillis();
        String endJobLog = sdf.format(new Date(endJobTime)) + Constants.MAP_REDUCE_END_MESSAGE;
        logAndWrite(endJobLog, timestampLogFilePath);
    }

    private void logAndWrite(String message, String filePath) {
        logger.info(message.trim());
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeData(String header, Collection<? extends CsvWritable> result) {
        try {
            CsvWriter.writeCsv(queryOutputFilePath, header, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadData() {
        if (infractionsConsumer() != null) {
            CsvFileIterator.readCsv(arguments, CsvFileType.INFRACTIONS, infractionsConsumer());
        }

        if (ticketsConsumer() != null) {
            CsvFileIterator.readCsv(arguments, CsvFileType.TICKETS, ticketsConsumer());
        }

        if (agenciesConsumer() != null) {
            CsvFileIterator.readCsv(arguments, CsvFileType.AGENCIES, agenciesConsumer());
        }
    }

    // Assuming infractions are always loaded in the same way
    protected TriConsumer<String[], CsvMappingConfig, Integer> infractionsConsumer() {
        IMap<String, Infraction> infractions = hazelcastInstance.getMap(HazelcastCollections.INFRACTIONS_MAP.getName());
        return (fields, config, id) -> {
            if (fields.length == Infraction.FIELD_COUNT) {
                String code = fields[config.getColumnIndex("code")];
                String definition = fields[config.getColumnIndex("definition")];
                infractions.put(code, new Infraction(code, definition));
            } else {
                logger.error(String.format("Invalid line format, expected %d fields, found %d", Infraction.FIELD_COUNT, fields.length));
            }
        };
    }

    protected TriConsumer<String[], CsvMappingConfig, Integer> agenciesConsumer() {
        IMap<String, Agency> agencies = hazelcastInstance.getMap(HazelcastCollections.AGENCIES_MAP.getName());
        return (fields, config, id) -> {
            if (fields.length == 1) {
                String issuingAgency = fields[config.getColumnIndex("issuingAgency")];
                agencies.put(issuingAgency, new Agency(issuingAgency));
            } else {
                logger.error(String.format("Invalid line format, expected %d fields, found %d", 1, fields.length));
            }
        };
    }

    protected abstract TriConsumer<String[], CsvMappingConfig, Integer> ticketsConsumer();

    protected abstract void executeJob() throws ExecutionException, InterruptedException;
}