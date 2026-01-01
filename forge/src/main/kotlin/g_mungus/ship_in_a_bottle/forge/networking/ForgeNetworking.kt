package g_mungus.ship_in_a_bottle.forge.networking

import g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.ship_in_a_bottle.networking.PlatformNetworking
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.client.event.ClientPlayerNetworkEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import net.minecraftforge.network.simple.SimpleChannel
import java.util.Optional

/**
 * Forge-native networking implementation using SimpleChannel.
 * This replaces Fabric API networking with Forge's built-in systems.
 */
object ForgeNetworking : PlatformNetworking {
    private const val PROTOCOL_VERSION = "1"
    private var discriminator = 0

    // Channel is initialized during FMLCommonSetupEvent
    private lateinit var channel: SimpleChannel

    // Handlers stored here, called by packet classes
    private var serverJoinHandler: ((MinecraftServer, ServerPlayer) -> Unit)? = null
    internal var clientPacketHandler: ((String) -> Unit)? = null
    private var clientDisconnectHandler: (() -> Unit)? = null

    /**
     * Call this during FMLCommonSetupEvent to setup the channel.
     * Must be called before initializeServer/initializeClient.
     */
    fun setupChannel(event: FMLCommonSetupEvent) {
        channel = NetworkRegistry.newSimpleChannel(
            ResourceLocation(MOD_ID, "main"),
            { PROTOCOL_VERSION },
            { clientVersion -> clientVersion == PROTOCOL_VERSION },
            { serverVersion -> serverVersion == PROTOCOL_VERSION }
        )

        // Register packet types
        channel.registerMessage(
            discriminator++,
            ShipDataPacket::class.java,
            ShipDataPacket::encode,
            ::ShipDataPacket,
            ShipDataPacket::handle,
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )
    }

    override fun initializeServer() {
        // Server initialization happens via event bus registration
        // The actual handler is set via registerServerJoinHandler
    }

    override fun initializeClient() {
        // Client initialization happens when packets are received
        // The actual handler is set via registerClientPacketHandler
    }

    override fun sendShipDataPacket(serializedData: String, recipients: List<ServerPlayer>) {
        if (!::channel.isInitialized) {
            throw IllegalStateException("ForgeNetworking channel not initialized - call setupChannel() first")
        }

        val packet = ShipDataPacket(serializedData)

        recipients.forEach { player ->
            channel.send(PacketDistributor.PLAYER.with { player }, packet)
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

    /**
     * Event handler for player login - must be registered on FORGE event bus.
     * Call MinecraftForge.EVENT_BUS.register(ForgeNetworking) from ShipInABottleModForge init.
     */
    @SubscribeEvent
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as? ServerPlayer ?: return
        val server = player.server ?: return

        serverJoinHandler?.invoke(server, player)
    }

    /**
     * Event handler for player logout - must be registered on FORGE event bus.
     * This handles client-side cleanup.
     */
    @SubscribeEvent
    fun onClientDisconnect(event: ClientPlayerNetworkEvent.LoggingOut) {
        clientDisconnectHandler?.invoke()
    }
}
