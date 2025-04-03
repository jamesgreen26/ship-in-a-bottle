package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.ShipInABottle.BOTTLE_WITH_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.ShipInABottle.WATER_DISPLAY_BLOCK
import g_mungus.ship_in_a_bottle.client.BottleWithShipBERenderer
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

@Environment(EnvType.CLIENT)
object ShipInABottleClientModFabric : ClientModInitializer {
    override fun onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(BOTTLE_WITH_SHIP_BLOCK, RenderType.cutoutMipped())
        BlockRenderLayerMap.INSTANCE.putBlock(BOTTLE_WITHOUT_SHIP_BLOCK, RenderType.cutoutMipped())
        BlockRenderLayerMap.INSTANCE.putBlock(WATER_DISPLAY_BLOCK, RenderType.translucent())

        BlockEntityRenderers.register(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE) { BottleWithShipBERenderer() }

        ShipInABottleClient.init()
    }
}