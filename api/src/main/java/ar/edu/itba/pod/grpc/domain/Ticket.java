package ar.edu.itba.pod.grpc.domain;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.Date;

public class Ticket implements DataSerializable {
  public static final Integer FIELD_COUNT = 6;
  private String plate;
  private String infractionId;
  private Double fineAmount;
  private String issuingAgency;
  private Date issueDate;
  private String countyName;

  public Ticket() {
  }

  public Ticket(String plate, Date issueDate, String infractionId, Double fineAmount, String countyName, String issuingAgency) {
    this.plate = plate;
    this.infractionId = infractionId;
    this.fineAmount = fineAmount;
    this.issueDate = issueDate;
    this.issuingAgency = issuingAgency;
    this.countyName = countyName;
  }

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    out.writeUTF(plate);
    out.writeLong(issueDate.getTime());
    out.writeUTF(infractionId);
    out.writeDouble(fineAmount);
    out.writeUTF(countyName);
    out.writeUTF(issuingAgency);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    plate = in.readUTF();
    issueDate = new Date(in.readLong());
    infractionId = in.readUTF();
    fineAmount = in.readDouble();
    countyName = in.readUTF();
    issuingAgency = in.readUTF();
  }

  @Override
  public String toString() {
    return "Ticket{" +
        "plate='" + plate + '\'' +
        ", issueDate=" + issueDate +
        ", infractionCode=" + infractionId +
        ", fineAmount=" + fineAmount +
        ", countyName='" + countyName + '\'' +
        ", issuingAgency='" + issuingAgency + '\'' +
        '}';
  }

  public String getPlate() {
    return plate;
  }

  public Date getIssueDate() {
    return issueDate;
  }

  public String getInfractionId() {
    return infractionId;
  }

  public Double getFineAmount() {
    return fineAmount;
  }

  public String getCountyName() {
    return countyName;
  }

  public String getIssuingAgency() {
    return issuingAgency;
  }
}

