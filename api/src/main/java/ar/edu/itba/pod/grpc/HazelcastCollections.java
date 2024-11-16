package ar.edu.itba.pod.grpc;

public enum HazelcastCollections {
    INFRACTIONS_MAP("infractions"),
    TICKETS_BY_INFRACTION_AND_AGENCY_MAP("ticketsByInfraction"),
    AGENCIES_MAP("agencyFine");

    private final String name;

    HazelcastCollections(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
