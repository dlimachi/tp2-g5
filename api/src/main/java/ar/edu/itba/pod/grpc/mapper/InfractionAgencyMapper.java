package ar.edu.itba.pod.grpc.mapper;

import com.hazelcast.mapreduce.Context;
import com.hazelcast.mapreduce.Mapper;

import java.util.Map;

public class InfractionAgencyMapper implements Mapper<String, Integer, String, Integer> {
    private final Map<String, String> infractions;
    private final Map<String, String> agencies;

    public InfractionAgencyMapper(Map<String, String> infractions, Map<String, String> agencies) {
        this.infractions = infractions;
        this.agencies = agencies;
    }

    @Override
    public void map(String key, Integer count, Context<String, Integer> context) {
        String[] parts = key.split(";");
        String infractionId = parts[0];
        String agencyId = parts[1];

        String infraction = infractions.get(infractionId);
        String agency = agencies.get(agencyId);

        if (infraction != null && agency != null) {
            context.emit(infraction + ";" + agency, count);
        }
    }
}
