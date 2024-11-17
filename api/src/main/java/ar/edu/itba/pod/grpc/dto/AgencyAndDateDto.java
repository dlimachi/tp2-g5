package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;


public class AgencyAndDateDto implements DataSerializable {
    private String agency;
    private double fineAmount;
    private int year;
    private int month;

    public AgencyAndDateDto() {
    }

    public AgencyAndDateDto(String agency, double fineAmount, int year, int month) {
        this.agency = agency;
        this.fineAmount = fineAmount;
        this.year = year;
        this.month = month;
    }

    public String getAgency() {
        return agency;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
        objectDataOutput.writeUTF(agency);
        objectDataOutput.writeDouble(fineAmount);
        objectDataOutput.writeInt(year);
        objectDataOutput.writeInt(month);
    }

    @Override
    public void readData(ObjectDataInput objectDataInput) throws IOException {
        agency = objectDataInput.readUTF();
        fineAmount = objectDataInput.readDouble();
        year = objectDataInput.readInt();
        month = objectDataInput.readInt();
    }
}
