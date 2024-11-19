package ar.edu.itba.pod.grpc.dto;

import ar.edu.itba.pod.grpc.CsvWritable;

public class CountyReincidenceDto implements CsvWritable, Comparable<CountyReincidenceDto> {
    private String county;
    private double percentage;

    public CountyReincidenceDto(String county, double percentage) {
        this.county = county;
        this.percentage = percentage;
    }

    @Override
    public String toCsv() {
        return String.format("%s;%.2f%%", county, percentage);
    }

    @Override
    public int compareTo(CountyReincidenceDto o) {
        int percentageComparison = Double.compare(o.percentage, this.percentage);
        if (percentageComparison != 0) {
            return percentageComparison;
        }
        return this.county.compareTo(o.county);
    }

    public String getCounty() { return county; }
    public double getPercentage() { return percentage; }
}