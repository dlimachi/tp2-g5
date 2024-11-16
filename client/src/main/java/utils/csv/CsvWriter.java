package utils.csv;

import ar.edu.itba.pod.grpc.CsvWritable;
import exceptions.ClientFileException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class CsvWriter {
    public static <T extends CsvWritable> void writeCsv(String filePath, String header, Collection<T> data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(header);
            writer.newLine();
            for (T record : data) {
                writer.write(record.toCsv());
                writer.newLine();
            }
        }
        catch (IOException e) {
            throw new ClientFileException("Error writing CSV file", e);
        }
    }
}