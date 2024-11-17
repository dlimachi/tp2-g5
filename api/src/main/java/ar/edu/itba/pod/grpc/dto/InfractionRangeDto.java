package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

public class InfractionRangeDto implements DataSerializable {
  private String infractionId;
  private int year;
  private int month;
  private int day;
  private int hour;

  public InfractionRangeDto(String infractionId, int year, int month, int day, int hour) {
    this.infractionId = infractionId;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
  }

  public String getInfractionId() {
    return infractionId;
  }


  public int getYear() {
    return year;
  }

  public int getMonth() {
    return month;
  }

  public int getDay() {
    return day;
  }

  public int getHour() {
    return hour;
  }

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    out.writeUTF(infractionId);
    out.writeInt(year);
    out.writeInt(month);
    out.writeInt(day);
    out.writeInt(hour);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    infractionId = in.readUTF();
    year = in.readInt();
    month = in.readInt();
    day = in.readInt();
    hour = in.readInt();
  }
}
