package ar.edu.itba.pod.grpc.dto;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;

public class MinMaxAmount implements DataSerializable {
    private double min;
    private double max;

    public MinMaxAmount() {}

    public MinMaxAmount(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeDouble(min);
        out.writeDouble(max);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        min = in.readDouble();
        max = in.readDouble();
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
}