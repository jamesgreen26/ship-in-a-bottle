package g_mungus.ship_in_a_bottle.client

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.ShipInABottle.BOTTLE_WITH_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.ShipInABottle.LOGGER
import g_mungus.ship_in_a_bottle.ShipInABottle.WATER_DISPLAY_BLOCK
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object ShipInABottleClient {

    @JvmStatic
    fun init() {
        LOGGER.info("client init")
        BlockRenderLayerMap.INSTANCE.putBlock(BOTTLE_WITH_SHIP_BLOCK, RenderType.translucent())
        BlockRenderLayerMap.INSTANCE.putBlock(BOTTLE_WITHOUT_SHIP_BLOCK, RenderType.translucent())
        BlockRenderLayerMap.INSTANCE.putBlock(WATER_DISPLAY_BLOCK, RenderType.translucent())

        BlockEntityRenderers.register(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE) { BottleWithShipBERenderer() }
    }
}