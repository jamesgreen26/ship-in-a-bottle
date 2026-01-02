package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.ShipInABottle.init
import g_mungus.ship_in_a_bottle.config.ShipInABottleConfigUpdater
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.ship_in_a_bottle.fabric.networking.FabricNetworking
import net.fabricmc.api.ModInitializer
import net.minecraftforge.fml.config.ModConfig
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric

object ShipInABottleModFabric: ModInitializer {
    const val MOD_ID = "ship_in_a_bottle"

    override fun onInitialize() {
        // force VS2 to load before eureka
        ValkyrienSkiesModFabric().onInitialize()
        registerBlocks()
        registerItems()

        // Register config
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.CLIENT, ShipInABottleConfigUpdater.CLIENT_SPEC, "ship_in_a_bottle/client.toml")
        ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.SERVER, ShipInABottleConfigUpdater.SERVER_SPEC, "ship_in_a_bottle/server.toml")

        ModConfigEvents.loading(MOD_ID).register(ShipInABottleConfigUpdater::update)
        ModConfigEvents.reloading(MOD_ID).register(ShipInABottleConfigUpdater::update)

        // Initialize platform networking
        NetworkUtils.platformNetworking = FabricNetworking

        init()
    }
}
