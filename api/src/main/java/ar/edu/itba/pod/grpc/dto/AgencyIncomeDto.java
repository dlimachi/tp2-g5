package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

public class AgencyIncomeDto implements DataSerializable {
  private String agency;
  private int year;
  private int month;
  private int ydt;

  public AgencyIncomeDto() {
  }

  public AgencyIncomeDto(String agency, int year, int month, int ydt) {
    this.agency = agency;
    this.year = year;
    this.month = month;
    this.ydt = ydt;
  }

  public String getAgency() {
    return agency;
  }

  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getYdt() {
    return ydt;
  }

  @Override
  public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
    objectDataOutput.writeUTF(agency);
    objectDataOutput.writeInt(year);
    objectDataOutput.writeInt(month);
    objectDataOutput.writeInt(ydt);
  }

  @Override
  public void readData(ObjectDataInput objectDataInput) throws IOException {
    agency = objectDataInput.readUTF();
    year = objectDataInput.readInt();
    month = objectDataInput.readInt();
    ydt = objectDataInput.readInt();
  }
}
