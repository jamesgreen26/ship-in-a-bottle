package g_mungus.ship_in_a_bottle.networking

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

/**
 * Platform-agnostic networking interface for handling ship data synchronization.
 * Implementations exist for both Fabric and Forge platforms.
 */
interface PlatformNetworking {
    /**
     * Initialize server-side networking.
     * Registers handlers for server events and configures packet sending.
     */
    fun initializeServer()

    /**
     * Initialize client-side networking.
     * Registers handlers for receiving packets and disconnect events.
     */
    fun initializeClient()

    /**
     * Send a ship data packet to specific players.
     * Platform implementations handle buffer creation and packet delivery.
     *
     * @param serializedData Base64-encoded serialized DisplayableShipData
     * @param recipients List of players to send the packet to
     */
    fun sendShipDataPacket(serializedData: String, recipients: List<ServerPlayer>)

    /**
     * Register a handler to be called when a player joins the server.
     * This is typically where we send existing ship data to new players.
     *
     * @param handler Function receiving server and the player who joined
     */
    fun registerServerJoinHandler(handler: (MinecraftServer, ServerPlayer) -> Unit)

    /**
     * Register a handler to be called when the client receives a ship data packet.
     *
     * @param handler Function receiving the Base64-encoded serialized data
     */
    fun registerClientPacketHandler(handler: (String) -> Unit)

    /**
     * Register a handler to be called when the client disconnects.
     * This is typically where we clear cached ship data.
     *
     * @param handler Function to be called on disconnect
     */
    fun registerClientDisconnectHandler(handler: () -> Unit)
}
