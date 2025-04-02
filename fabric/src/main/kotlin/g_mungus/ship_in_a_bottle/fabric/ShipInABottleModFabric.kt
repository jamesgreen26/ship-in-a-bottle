package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.ShipInABottle.init
import g_mungus.ship_in_a_bottle.ShipInABottle.initClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.api.ModInitializer
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric

object ShipInABottleModFabric: ModInitializer {
    override fun onInitialize() {
        // force VS2 to load before eureka
        ValkyrienSkiesModFabric().onInitialize()

        init()
    }
}
