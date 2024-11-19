package ar.edu.itba.pod.grpc;

public enum HazelcastCollections {
    INFRACTIONS_MAP("infractions"),
    TICKETS_BY_INFRACTION_AND_AGENCY_MAP("ticketsByInfraction"),
    AGENCIES_MAP("agencyFine"),
    TICKETS_BY_AGENCY_AND_DATE_MAP("ticketsByAgencyAndDate"),
    TICKETS_BY_INFRACTION_AND_AMOUNT_MAP("ticketsByInfractionAndAmount"),
    TICKETS_BY_24_X_7_MAP("ticketsBy24x7");

    private final String name;

    HazelcastCollections(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
