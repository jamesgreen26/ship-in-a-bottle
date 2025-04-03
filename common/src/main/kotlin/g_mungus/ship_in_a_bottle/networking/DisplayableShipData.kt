package g_mungus.ship_in_a_bottle.networking

import net.minecraft.core.BlockPos
import java.io.*
import java.util.*


class DisplayableShipData(
    var shipName: String,
    val updated: Long,
    val sizeX: Int,
    val sizeY: Int,
    val sizeZ: Int
) : Serializable {
    var data: MutableList<BlockInfo> = mutableListOf()


    class BlockInfo(
        val x: Int,
        val y: Int,
        val z: Int,
        val stateId: Int
    ) : Serializable {
        constructor(pos: BlockPos, stateId: Int) : this(pos.x, pos.y, pos.z, stateId)
    }

    companion object {
        @Throws(IOException::class)
        fun serialize(obj: DisplayableShipData?): String {
            ByteArrayOutputStream().use { bos ->
                ObjectOutputStream(bos).use { oos ->
                    oos.writeObject(obj)
                    return Base64.getEncoder().encodeToString(bos.toByteArray())
                }
            }
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        fun deserialize(bytes: String?): DisplayableShipData {
            ByteArrayInputStream(Base64.getDecoder().decode(bytes)).use { bis ->
                ObjectInputStream(bis).use { ois ->
                    return ois.readObject() as DisplayableShipData
                }
            }
        }
    }
}