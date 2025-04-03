package g_mungus.ship_in_a_bottle.client

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.ShipInABottle.LOGGER
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers

object ShipInABottleClient {

    @JvmStatic
    fun init() {
        LOGGER.info("client init")
        BlockEntityRenderers.register(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE) { BottleWithShipBERenderer() }
    }
}