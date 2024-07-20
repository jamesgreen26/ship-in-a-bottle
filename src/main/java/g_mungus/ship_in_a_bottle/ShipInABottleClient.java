package g_mungus.ship_in_a_bottle;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class ShipInABottleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BOTTLEWITHOUTSHIP, RenderLayer.getTranslucent());
    }
}
