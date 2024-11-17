package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InfractionAndAgencyDto implements DataSerializable {
  protected static final Logger logger = LoggerFactory.getLogger(InfractionAndAgencyDto.class);
  private String issuingAgency;
  private String definition;

  public InfractionAndAgencyDto() {
  }
  public InfractionAndAgencyDto(String agency, String definition) {
    logger.info("write data 1 {}, {}", agency, definition);
    this.issuingAgency = agency;
    this.definition = definition;
  }

  @Override
  public void writeData(ObjectDataOutput out) throws IOException {
    logger.info("write data {}",out.toString());
    out.writeUTF(issuingAgency);
    out.writeUTF(definition);
  }

  @Override
  public void readData(ObjectDataInput in) throws IOException {
    issuingAgency = in.readUTF();
    definition = in.readUTF();
  }

  public String getIssuingAgency() {
    return issuingAgency;
  }


  public String getDefinition() {
    return definition;
  }
}