package utils.csv;

import java.io.IOException;

public class CsvMappingConfigFactory {

    public static CsvMappingConfig getTicketConfig(String inPath, String city) throws IOException {
        String filePath = inPath + "/tickets" + city.toUpperCase() + ".json";
        return CsvMappingConfig.fromJson(filePath);
    }

    public static CsvMappingConfig getInfractionConfig(String inPath, String city) throws IOException {
        String filePath = inPath + "/infractions" + city.toUpperCase() + ".json";
        return CsvMappingConfig.fromJson(filePath);
    }

    public static CsvMappingConfig getAgencyConfig(String inPath, String city) throws IOException {
        String filePath = inPath + "/agencies" + city.toUpperCase() + ".json";
        return CsvMappingConfig.fromJson(filePath);
    }
}