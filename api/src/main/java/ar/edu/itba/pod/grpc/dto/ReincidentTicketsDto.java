package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.Date;

public class ReincidentTicketsDto implements DataSerializable {
    private String county;
    private String infractionDefinition;
    private String plate;
    private Date date;

    public ReincidentTicketsDto() {}

    public ReincidentTicketsDto(String county, String infractionDefinition, String plate, Date date) {
        this.county = county;
        this.infractionDefinition = infractionDefinition;
        this.plate = plate;
        this.date = date;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(county);
        out.writeUTF(infractionDefinition);
        out.writeUTF(plate);
        out.writeLong(date.getTime());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        county = in.readUTF();
        infractionDefinition = in.readUTF();
        plate = in.readUTF();
        date = new Date(in.readLong());
    }

    public String getCounty() { return county; }
    public String getInfractionDefinition() { return infractionDefinition; }
    public String getPlate() { return plate; }
    public Date getDate() { return date; }
}