package g_mungus.ship_in_a_bottle.forge.networking

import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

/**
 * Packet containing serialized ship data for Forge networking.
 * Forge requires explicit packet classes for SimpleChannel.
 */
class ShipDataPacket {
    val serializedData: String

    /**
     * Constructor for creating a new packet with serialized data.
     */
    constructor(serializedData: String) {
        this.serializedData = serializedData
    }

    /**
     * Constructor for decoding a packet from a buffer.
     * Required by Forge's SimpleChannel packet registration.
     */
    constructor(buf: FriendlyByteBuf) {
        this.serializedData = buf.readUtf()
    }

    /**
     * Encode this packet into a buffer.
     * Called by Forge when sending the packet.
     */
    fun encode(buf: FriendlyByteBuf) {
        buf.writeUtf(serializedData)
    }

    /**
     * Handle this packet on the client.
     * Called by Forge when the packet is received.
     */
    fun handle(ctx: Supplier<NetworkEvent.Context>) {
        val context = ctx.get()
        context.enqueueWork {
            // Access the handler registered in ForgeNetworking
            ForgeNetworking.clientPacketHandler?.invoke(serializedData)
        }
        context.packetHandled = true
    }
}
