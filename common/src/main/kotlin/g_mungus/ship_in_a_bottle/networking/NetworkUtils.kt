package g_mungus.ship_in_a_bottle.networking

import g_mungus.ship_in_a_bottle.ShipInABottle.LOGGER
import g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import g_mungus.ship_in_a_bottle.util.BlockInfoListProvider
import g_mungus.ship_in_a_bottle.networking.DisplayableShipData.BlockInfo
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object NetworkUtils {
    // Platform implementation - will be set by platform module during initialization
    lateinit var platformNetworking: PlatformNetworking

    val SHIP_PACKET_ID = ResourceLocation(MOD_ID, "ship-packet")

    fun initializeServer() {
        platformNetworking.initializeServer()
        platformNetworking.registerServerJoinHandler { server, player ->
            onPlayerJoin(server, player)
        }
    }

    fun initializeClient() {
        platformNetworking.initializeClient()
        platformNetworking.registerClientPacketHandler { serializedData ->
            onClientReceiveShipData(serializedData)
        }
        platformNetworking.registerClientDisconnectHandler {
            ShipInABottleClient.shipDisplayData.clear()
        }
    }

    private fun onPlayerJoin(server: MinecraftServer, player: ServerPlayer) {
        server.overworld().structureManager.listTemplates()
            .filter { identifier -> identifier.namespace == MOD_ID }
            .forEach { identifier ->
                val shipName: String = identifier.toString().replace("$MOD_ID:", "")
                updateClientShipData(server, shipName, listOf(player))
            }
    }

    private fun onClientReceiveShipData(serializedData: String) {
        try {
            val data = DisplayableShipData.deserialize(serializedData)

            // Use atomic compute to handle concurrent packet arrival safely
            ShipInABottleClient.shipDisplayData.compute(data.shipName) { _, existingData ->
                if (existingData == null || existingData.updated != data.updated) {
                    // New ship or updated ship - replace completely
                    data
                } else {
                    // Same timestamp - this is a continuation packet, append blocks
                    existingData.data.addAll(data.data)
                    existingData
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to process ship data packet", e)
        }
    }

    fun updateClientShipData(server: MinecraftServer, shipName: String, players: List<ServerPlayer>) {
        val world: ServerLevel = server.overworld()
        val manager = world.structureManager

        if (manager.get(ResourceLocation(MOD_ID, shipName)).isEmpty) {
            LOGGER.warn("template not found for {}", shipName)
            return
        }

        val template: StructureTemplate = manager.get(ResourceLocation(MOD_ID, shipName)).get()
        val palettes: List<StructureTemplate.Palette> =
            (template as BlockInfoListProvider).`ship_in_a_bottle$getBlockInfoList`()

        val data = DisplayableShipData(
            shipName,
            Calendar.getInstance().timeInMillis,
            template.size.x,
            template.size.y,
            template.size.z
        )

        val i = AtomicInteger()

        val outputs: MutableList<String> = ArrayList()

        palettes.forEach { it: StructureTemplate.Palette ->
            it.blocks().forEach { structureBlockInfo ->
                data.data.add(BlockInfo(structureBlockInfo.pos(), Block.getId(structureBlockInfo.state())))
                i.getAndIncrement()
                if (i.get() >= 256) {
                    try {
                        val ser = DisplayableShipData.serialize(data)
                        outputs.add(ser)
                    } catch (e: Exception) {
                        LOGGER.error("Failed to serialize ship data chunk for {}", shipName, e)
                    }
                    data.data.clear()
                    i.set(0)
                }
            }
        }
        if (i.get() != 0) {
            try {
                val ser = DisplayableShipData.serialize(data)
                outputs.add(ser)
            } catch (e: Exception) {
                LOGGER.error("Failed to serialize final ship data chunk for {}", shipName, e)
            }
        }

        for (output in outputs) {
            try {
                platformNetworking.sendShipDataPacket(output, players)
            } catch (e: Exception) {
                LOGGER.error("Failed to send ship data packet for {}", shipName, e)
            }
        }
    }
}
