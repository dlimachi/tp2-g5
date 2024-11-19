package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;

// InfractionAmountDto.java
public class InfractionAmountDto implements DataSerializable {
    private String infractionDefinition;
    private double amount;

    public InfractionAmountDto() {
    }

    public InfractionAmountDto(String infractionDefinition, double amount) {
        this.infractionDefinition = infractionDefinition;
        this.amount = amount;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(infractionDefinition);
        out.writeDouble(amount);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        infractionDefinition = in.readUTF();
        amount = in.readDouble();
    }

    // Getters
    public String getInfractionDefinition() { return infractionDefinition; }
    public double getAmount() { return amount; }
}
