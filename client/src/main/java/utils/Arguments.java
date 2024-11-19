package utils;

import exceptions.ClientIllegalArgumentException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

public class Arguments {
    private final Integer query;
    private final String[] addresses;
    private final String inPath;
    private final String outPath;
    private final String city;
    private final Integer n;
    private final Date from;
    private final Date to;
    private final String agency;
    private String separator;

    private Arguments(Builder builder) {
        this.query = builder.query;
        this.addresses = builder.addresses;
        this.inPath = builder.inPath;
        this.outPath = builder.outPath;
        this.city = builder.city;
        this.n = builder.n;
        this.from = builder.from;
        this.to = builder.to;
        this.agency = builder.agency;
        this.separator = builder.separator;

        if (query == null || addresses == null || addresses.length == 0 || inPath == null || outPath == null) {
            throw new ClientIllegalArgumentException("Parameters -Daddresses, -DinPath and -DoutPath must be provided");
        }
    }

    public Integer getQuery() {
        return query;
    }

    public String[] getAddresses() {
        return addresses;
    }

    public String getInPath() {
        return inPath;
    }

    public String getOutPath() {
        return outPath;
    }

    public String getCity() {
        return city;
    }

    public Integer getN() {
        return n;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public String getAgency() {
        return agency;
    }

    public String getSeparator() {
        return separator;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "query=" + query +
                ", addresses=" + Arrays.toString(addresses) +
                ", inPath='" + inPath + '\'' +
                ", outPath='" + outPath + '\'' +
                ", city='" + city + '\'' +
                ", n=" + n +
                ", from=" + from +
                ", to=" + to +
                ", agency='" + agency + '\'' +
                '}';
    }

    public static class Builder {
        private Integer query;
        private String[] addresses;
        private String inPath;
        private String outPath;
        private String city;
        private Integer n;
        private Date from;
        private Date to;
        private String agency;
        private String separator = ";";

        public Builder query(Integer query) {
            this.query = query;
            return this;
        }

        public Builder addresses(String[] addresses) {
            this.addresses = addresses;
            return this;
        }

        public Builder inPath(String inPath) {
            this.inPath = inPath;
            return this;
        }

        public Builder outPath(String outPath) {
            this.outPath = outPath;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder n(Integer limit) {
            this.n = limit;
            return this;
        }

        public Builder from(Date from) {
            this.from = from;
            return this;
        }

        public Builder to(Date to) {
            this.to = to;
            return this;
        }

        public Builder agency(String agency) {
            this.agency = agency;
            return this;
        }

        public Builder separator(String separator) {
            this.separator = separator;
            return this;
        }

        public Arguments build() {
            return new Arguments(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}