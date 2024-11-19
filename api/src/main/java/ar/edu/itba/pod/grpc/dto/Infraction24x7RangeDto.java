package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

public class Infraction24x7RangeDto implements DataSerializable {
  private String infractionDefinition;
  private int year;
  private int month;
  private int day;
  private int hour;

  public Infraction24x7RangeDto() {
  }

  public Infraction24x7RangeDto(String infractionDefinition, int year, int month, int day, int hour) {
    this.infractionDefinition = infractionDefinition;
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
  }

  public String getInfractionDefinition() {
    return infractionDefinition;
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
    out.writeUTF(infractionDefinition);
    out.writeInt(year);
    out.writeInt(month);
    out.writeInt(day);
    out.writeInt(hour);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    infractionDefinition = in.readUTF();
    year = in.readInt();
    month = in.readInt();
    day = in.readInt();
    hour = in.readInt();
  }
}
