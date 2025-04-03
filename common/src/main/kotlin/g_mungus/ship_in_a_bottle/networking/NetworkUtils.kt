package g_mungus.ship_in_a_bottle.networking

import g_mungus.ship_in_a_bottle.ShipInABottle.LOGGER
import g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import g_mungus.ship_in_a_bottle.util.BlockInfoListProvider
import g_mungus.ship_in_a_bottle.networking.DisplayableShipData.BlockInfo
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.network.ServerGamePacketListenerImpl
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object NetworkUtils {
    private val SHIP_PACKET_ID = ResourceLocation(MOD_ID, "ship-packet")

    fun initializeServer() {
        ServerPlayConnectionEvents.JOIN.register(ServerPlayConnectionEvents.Join { handler: ServerGamePacketListenerImpl, _: PacketSender?, server: MinecraftServer ->
            server.overworld().structureManager.listTemplates()
                .filter { identifier -> identifier.namespace == MOD_ID }
                .forEach { identifier ->
                    LOGGER.info("Found ship: {}", identifier)
                    val shipName: String = identifier.toString().replace("$MOD_ID:", "")

                    updateClientShipData(server, shipName, listOf(handler.player))
                }
        })
    }

    fun initializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(
            SHIP_PACKET_ID
        ) { client: Minecraft, _: ClientPacketListener?, buf: FriendlyByteBuf, _: PacketSender? ->
            val bytes: String = buf.readUtf()
            client.execute {
                try {
                    val data = DisplayableShipData.deserialize(bytes)
                    if (ShipInABottleClient.shipDisplayData[data.shipName] == null || ShipInABottleClient.shipDisplayData[data.shipName]!!.updated != data.updated
                    ) {
                        println("creating new ship")
                        ShipInABottleClient.shipDisplayData[data.shipName] = data
                    } else {
                        println("adding " + data.data.size + " elements to existing ship")
                        ShipInABottleClient.shipDisplayData[data.shipName]!!.data.addAll(data.data)
                    }

                    println("received. New ship size: " + ShipInABottleClient.shipDisplayData[data.shipName]!!.data.size + "Packet timestamp: " + data.updated)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { handler: ClientPacketListener?, client: Minecraft? ->
            ShipInABottleClient.shipDisplayData.clear()
        })
    }

    fun updateClientShipData(server: MinecraftServer, shipName: String, players: List<ServerPlayer>) {
        val world: ServerLevel = server.overworld()
        val manager = world.structureManager

        if (manager.get(ResourceLocation(MOD_ID, shipName)).isEmpty) {
            LOGGER.warn("template not found for {}", shipName)
            return
        } else {
            LOGGER.info("Syncing ship data from {}", shipName)
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
                        e.printStackTrace()
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
                e.printStackTrace()
            }
        }

        for (output in outputs) {
            val buf: FriendlyByteBuf = PacketByteBufs.create()
            buf.writeUtf(output)

            players.forEach { serverPlayerEntity: ServerPlayer ->
                ServerPlayNetworking.send(
                    serverPlayerEntity,
                    SHIP_PACKET_ID,
                    buf
                )
                LOGGER.info("Sending packet to {}", serverPlayerEntity.name.string)
            }
        }
    }
}
