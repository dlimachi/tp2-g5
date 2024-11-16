package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

public class AgencyDto implements DataSerializable {
  private String issuingAgency;

  public AgencyDto() {
  }

  public AgencyDto(String issuingAgency) {
    this.issuingAgency = issuingAgency;
  }


  @Override
  public void writeData(ObjectDataOutput objectDataOutput) throws IOException {
    objectDataOutput.writeUTF(issuingAgency);
  }

  @Override
  public void readData(ObjectDataInput objectDataInput) throws IOException {
    issuingAgency = objectDataInput.readUTF();
  }

  @Override
  public String toString() {
    return "Agency{" +
        "issuingAgency='" + issuingAgency + '\'' +
        '}';
  }

  public String getIssuingAgency() {
    return issuingAgency;
  }
}