package g_mungus.ship_in_a_bottle.forge

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.client.BottleWithShipBERenderer
import g_mungus.ship_in_a_bottle.client.ShipInABottleClient
import g_mungus.ship_in_a_bottle.config.ShipInABottleConfigUpdater
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.ship_in_a_bottle.forge.networking.ForgeNetworking
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS


@Mod(ShipInABottle.MOD_ID)
class ShipInABottleModForge {
    init {
        ModBlocks.register(MOD_BUS)
        Items.register(MOD_BUS)
        BlockEntities.register(MOD_BUS)

        // Register config
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ShipInABottleConfigUpdater.CLIENT_SPEC, "ship_in_a_bottle/client.toml")
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ShipInABottleConfigUpdater.SERVER_SPEC, "ship_in_a_bottle/server.toml")

        MOD_BUS.addListener(this::onConfigLoading)
        MOD_BUS.addListener(this::onConfigReloading)

        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }

        MOD_BUS.addListener { event: FMLCommonSetupEvent ->
            ModBlocks.setup(event)
            Items.setup(event)
            BlockEntities.setup(event)

            // Setup networking (must be in FMLCommonSetupEvent for proper initialization)
            ForgeNetworking.setupChannel(event)
            NetworkUtils.platformNetworking = ForgeNetworking

            // Initialize common mod after networking is setup
            ShipInABottle.init()
        }

        MOD_BUS.addListener { event: BuildCreativeModeTabContentsEvent ->
            if (event.tabKey === CreativeModeTabs.TOOLS_AND_UTILITIES) {
                event.accept(ShipInABottle.BOTTLE_WITHOUT_SHIP_ITEM)
            }
        }

        // Register ForgeNetworking event handlers on the FORGE event bus
        MinecraftForge.EVENT_BUS.register(ForgeNetworking)
    }

    private fun onConfigLoading(event: ModConfigEvent.Loading) {
        ShipInABottleConfigUpdater.update(event.config)
    }

    private fun onConfigReloading(event: ModConfigEvent.Reloading) {
        ShipInABottleConfigUpdater.update(event.config)
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
