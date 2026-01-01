package g_mungus.ship_in_a_bottle.fabric.networking

import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.ship_in_a_bottle.networking.PlatformNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

object FabricNetworking : PlatformNetworking {

    private var serverJoinHandler: ((MinecraftServer, ServerPlayer) -> Unit)? = null
    private var clientPacketHandler: ((String) -> Unit)? = null
    private var clientDisconnectHandler: (() -> Unit)? = null

    override fun initializeServer() {
        // Register server join event
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            serverJoinHandler?.invoke(server, handler.player)
        }
    }

    override fun initializeClient() {
        // Register client packet receiver
        ClientPlayNetworking.registerGlobalReceiver(NetworkUtils.SHIP_PACKET_ID) { client, _, buf, _ ->
            val serializedData = buf.readUtf()

            // Execute on main thread
            client.execute {
                clientPacketHandler?.invoke(serializedData)
            }
        }

        // Register disconnect handler
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            clientDisconnectHandler?.invoke()
        }
    }

    override fun sendShipDataPacket(serializedData: String, recipients: List<ServerPlayer>) {
        // Create buffer and write data
        val buf = PacketByteBufs.create()
        buf.writeUtf(serializedData)

        // Send to each recipient
        recipients.forEach { player ->
            ServerPlayNetworking.send(player, NetworkUtils.SHIP_PACKET_ID, buf)
        }
    }

    override fun registerServerJoinHandler(handler: (MinecraftServer, ServerPlayer) -> Unit) {
        this.serverJoinHandler = handler
    }

    override fun registerClientPacketHandler(handler: (String) -> Unit) {
        this.clientPacketHandler = handler
    }

    override fun registerClientDisconnectHandler(handler: () -> Unit) {
        this.clientDisconnectHandler = handler
    }
}
