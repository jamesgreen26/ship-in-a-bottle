package g_mungus.ship_in_a_bottle.forge

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.client.BottleWithShipBERenderer
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(ShipInABottle.MOD_ID)
class ShipInABottleModForge {
    init {
        Blocks.register(MOD_BUS)
        Items.register(MOD_BUS)
        BlockEntities.register(MOD_BUS)


        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }

        MOD_BUS.addListener { event: FMLCommonSetupEvent ->
            Blocks.setup(event)
            Items.setup(event)
            BlockEntities.setup(event)
        }

        ShipInABottle.init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        ItemBlockRenderTypes.setRenderLayer(ShipInABottle.BOTTLE_WITH_SHIP_BLOCK, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK, RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(ShipInABottle.WATER_DISPLAY_BLOCK, RenderType.translucent());

        BlockEntityRenderers.register(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE) { BottleWithShipBERenderer() }

        ShipInABottleClient.init()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
