package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
object ShipInABottleClientModFabric : ClientModInitializer {
    override fun onInitializeClient() {
        ShipInABottleClient.init()
    }
}