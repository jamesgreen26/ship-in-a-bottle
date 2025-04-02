package g_mungus.ship_in_a_bottle.util;


import net.minecraft.core.BlockPos;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayableShipData implements Serializable {
    public List<BlockInfo> data = new ArrayList<>();
    public String shipName;
    public final Long updated;

    public final int sizeX;
    public final int sizeY;
    public final int sizeZ;


    public DisplayableShipData(String shipName, Long updated, int sizeX, int sizeY, int sizeZ) {
        this.shipName = shipName;
        this.updated = updated;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public static class BlockInfo implements Serializable {
        public final int x;
        public final int y;
        public final int z;

        public final int stateId;

        public BlockInfo(int x, int y, int z, int stateId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.stateId = stateId;
        }

        public BlockInfo(BlockPos pos, int stateId) {
            this(pos.getX(), pos.getY(), pos.getZ(), stateId);
        }
    }

    public static String serialize(DisplayableShipData obj) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return java.util.Base64.getEncoder().encodeToString(bos.toByteArray());
        }
    }

    public static DisplayableShipData deserialize(String bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(java.util.Base64.getDecoder().decode(bytes));
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (DisplayableShipData) ois.readObject();
        }
    }
}
