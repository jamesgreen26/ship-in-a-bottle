package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.ShipInABottle.init
import net.fabricmc.api.ModInitializer
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric

object ShipInABottleModFabric: ModInitializer {
    override fun onInitialize() {
        // force VS2 to load before eureka
        ValkyrienSkiesModFabric().onInitialize()
        registerBlocks()
        registerItems()

        init()
    }
}
