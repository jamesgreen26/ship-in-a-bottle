package g_mungus.ship_in_a_bottle.client;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.util.DisplayableShipData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ShipInABottleClient implements ClientModInitializer {

    public static final ConcurrentHashMap<String, List<DisplayableShipData.BlockInfo>> shipDisplayData = new ConcurrentHashMap<>();

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOTTLEWITHOUTSHIP, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOTTLEWITHSHIP, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WATERBLOCK, RenderLayer.getTranslucent());

        BlockEntityRendererFactories.register(ModBlocks.BOTTLEWITHSHIPENTITY, BottleShipEntityRenderer::new);


        ClientPlayNetworking.registerGlobalReceiver(ShipInABottle.SHIP_PACKET_ID, (client, handler, buf, responseSender) -> {
            String bytes = buf.readString();

            client.execute(() -> {
                try {
                    DisplayableShipData data = DisplayableShipData.deserialize(bytes);
                    shipDisplayData.put(data.shipName, data.data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
