package utils.csv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;

public class CsvMappingConfig {
    private final Map<String, Integer> columnMappings;

    public CsvMappingConfig(Map<String, Integer> columnMappings) {
        this.columnMappings = columnMappings;
    }

    public int getColumnIndex(String columnName) {
        return columnMappings.getOrDefault(columnName, -1);
    }

    public static CsvMappingConfig fromJson(String filePath) throws IOException {
        Map<String, Integer> columnMappings = new HashMap<>();

        try (InputStream fis = new FileInputStream(filePath); JsonReader reader = Json.createReader(fis)) {
            JsonObject jsonObject = reader.readObject();
            for (String key : jsonObject.keySet()) {
                columnMappings.put(key, jsonObject.getInt(key));
            }
        }

        return new CsvMappingConfig(columnMappings);
    }
}
