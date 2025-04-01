package g_mungus.ship_in_a_bottle;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.item.ModItems;
import g_mungus.ship_in_a_bottle.util.BlockInfoListProvider;
import g_mungus.ship_in_a_bottle.util.DisplayableShipData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ShipInABottle implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final String MOD_ID = "ship-in-a-bottle";
    public static final Logger LOGGER = LoggerFactory.getLogger("ship-in-a-bottle");
    public static final Identifier SHIP_PACKET_ID = new Identifier(MOD_ID, "ship-packet");

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("Hello Fabric world!");
        ModItems.registerModItems();
        ModBlocks.registerModBlocks();


        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            server.getResourceManager().findAllResources("structures", identifier -> Objects.equals(identifier.getNamespace(), MOD_ID)).forEach((identifier, resources) -> {
                List<String> id = Arrays.stream(identifier.toString().split("/")).toList();
                String shipName = id.get(id.size() - 1).replace(".nbt", "");

                List<ServerPlayerEntity> players = new ArrayList<>();
                players.add(handler.player);
                updateClientShipData(server, shipName, players);
            });
        });
    }

    public static void updateClientShipData(MinecraftServer server, String shipName, List<ServerPlayerEntity> players) {
        ServerWorld world = server.getOverworld();

        StructureTemplateManager manager = world.getStructureTemplateManager();

        if (manager.getTemplate(new Identifier(ShipInABottle.MOD_ID, shipName)).isEmpty()) {
            LOGGER.warn("template not found for {}", shipName);
            return;
        } else {
            LOGGER.info("Syncing ship data from {}", shipName);
        }

        StructureTemplate template = manager.getTemplate(new Identifier(ShipInABottle.MOD_ID, shipName)).get();
        List<StructureTemplate.PalettedBlockInfoList> beans = ((BlockInfoListProvider) template).ship_in_a_bottle$getBlockInfoList();

        DisplayableShipData data = new DisplayableShipData(
                shipName,
                Calendar.getInstance().getTimeInMillis(),
                template.getSize().getX(),
                template.getSize().getY(),
                template.getSize().getZ()
        );

        AtomicInteger i = new AtomicInteger();

        List<String> outputs = new ArrayList<>();

        beans.forEach(it -> {
            it.getAll().forEach(structureBlockInfo -> {
                data.data.add(new DisplayableShipData.BlockInfo(structureBlockInfo.pos(), Block.getRawIdFromState(structureBlockInfo.state())));
                i.getAndIncrement();
                if (i.get() >= 256) {
                    try {
                        String ser = DisplayableShipData.serialize(data);
                        outputs.add(ser);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    data.data.clear();
                    i.set(0);
                }
            });
        });
        if (i.get() != 0) {
            try {
                String ser = DisplayableShipData.serialize(data);
                outputs.add(ser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String output : outputs) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(output);

            players.forEach(serverPlayerEntity -> {
                ServerPlayNetworking.send(serverPlayerEntity, ShipInABottle.SHIP_PACKET_ID, buf);
                LOGGER.info("Sending packet to {}", serverPlayerEntity.getEntityName());
            });
        }
    }
}