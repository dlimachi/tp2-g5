package ar.edu.itba.pod.grpc.dto;
import ar.edu.itba.pod.grpc.CsvWritable;


public class InfractionAmountRangeDto implements CsvWritable, Comparable<InfractionAmountRangeDto> {
    private String definition;
    private double minAmount;
    private double maxAmount;
    private double difference;

    public InfractionAmountRangeDto(String definition, double minAmount, double maxAmount) {
        this.definition = definition;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.difference = maxAmount - minAmount;
    }

    @Override
    public String toCsv() {
        return String.format("%s;%.0f;%.0f;%.0f", definition, minAmount, maxAmount, difference);
    }

    @Override
    public int compareTo(InfractionAmountRangeDto o) {
        int diffComparison = Double.compare(o.difference, this.difference);
        if (diffComparison != 0) {
            return diffComparison;
        }
        return this.definition.compareTo(o.definition);
    }

    public String getDefinition() {
        return definition;
    }

    public double getDifference() {
        return difference;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }
}