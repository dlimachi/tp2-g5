package ar.edu.itba.pod.grpc.reducer;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CountyStats implements DataSerializable {
    private Set<String> plates;

    public CountyStats() {
        plates = new HashSet<>();
    }

    public CountyStats(Set<String> plates) {
        this.plates = plates;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(plates.size());
        for (String plate : plates) {
            out.writeUTF(plate);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        plates = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            plates.add(in.readUTF());
        }
    }

    public Set<String> getPlates() {
        return plates;
    }
}
