package g_mungus.ship_in_a_bottle.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DisplayableShipData implements Serializable {
    public List<BlockInfo> data = new ArrayList<>();
    public String shipName;
    public final Long updated;

    public DisplayableShipData(String shipName, Long updated) {
        this.shipName = shipName;
        this.updated = updated;
    }

    public static class BlockInfo implements Serializable {
        public final int x;
        public final int y;
        public final int z;
        public final String id;
        public final String state;

        public BlockInfo(int x, int y, int z, String id, String state) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.id = id;
            this.state = state;
        }

        public BlockInfo(BlockPos pos, Identifier id, String state) {
            this(pos.getX(), pos.getY(), pos.getZ(), id.toString(), state);
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
