package ar.edu.itba.pod.grpc.dto;

import ar.edu.itba.pod.grpc.CsvWritable;
import java.util.Objects;

public class InfractionDto implements CsvWritable, Comparable<InfractionDto> {
    private String infraction;

    public InfractionDto() {
    }

    public InfractionDto(String infraction) {
        this.infraction = infraction;
    }

    @Override
    public String toString() {
        return "Infraction{" +
                "infraction='" + infraction + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfractionDto that = (InfractionDto) o;
        return Objects.equals(infraction, that.infraction);
    }

    public String getInfraction() {
        return infraction;
    }

    @Override
    public String toCsv() {
        return infraction;
    }

    @Override
    public int compareTo(InfractionDto o) {
        return infraction.compareTo(o.getInfraction());
    }
}
