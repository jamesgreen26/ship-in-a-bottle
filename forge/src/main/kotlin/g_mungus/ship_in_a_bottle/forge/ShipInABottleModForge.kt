package g_mungus.ship_in_a_bottle.forge

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ShipInABottle.MOD_ID)
class ShipInABottleModForge {
    init {
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }
        ShipInABottle.init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        ShipInABottleClient.init()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
