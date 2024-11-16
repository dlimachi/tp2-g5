package ar.edu.itba.pod.grpc.dto;

import ar.edu.itba.pod.grpc.CsvWritable;

public class TicketByAgencyAndInfractionDto implements CsvWritable, Comparable<TicketByAgencyAndInfractionDto> {
    private String infraction;
    private String agency;
    private int tickets;

    public TicketByAgencyAndInfractionDto() {}

    public TicketByAgencyAndInfractionDto(String infraction, String agency, int tickets) {
        this.infraction = infraction;
        this.agency = agency;
        this.tickets = tickets;
    }

    public String getInfraction() {
        return infraction;
    }

    public int getTickets() {
        return tickets;
    }

    public String getAgency() {
        return agency;
    }

    @Override
    public int compareTo(TicketByAgencyAndInfractionDto other) {
        int countComparison = Integer.compare(other.tickets, this.tickets);
        return countComparison != 0 ? countComparison : this.infraction.compareTo(other.infraction);
    }

    @Override
    public String toString() {
        return infraction + ";" + agency + "; " + tickets;
    }

    @Override
    public String toCsv() {
        return infraction + ";" + agency + ";" + tickets ;
    }
}