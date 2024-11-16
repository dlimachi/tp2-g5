package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

public class InfractionAndAgencyDto implements DataSerializable {
  private String issuingAgency;
  private String definition;


  public InfractionAndAgencyDto(String agency, String definition) {
    this.issuingAgency = agency;
    this.definition = definition;
  }

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    out.writeUTF(definition);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    definition = in.readUTF();
  }

  public String getIssuingAgency() {
    return issuingAgency;
  }


  public String getDefinition() {
    return definition;
  }
}