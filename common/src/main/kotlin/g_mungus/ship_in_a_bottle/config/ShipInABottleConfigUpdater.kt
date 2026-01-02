package g_mungus.ship_in_a_bottle.config


import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig
import org.valkyrienskies.mod.api.config.VSConfigApi
import org.valkyrienskies.mod.api.config.VSConfigApi.update
import org.valkyrienskies.mod.common.config.ConfigType
import org.valkyrienskies.mod.common.hooks.VSGameEvents

object ShipInABottleConfigUpdater {
    private val client_config = VSConfigApi.buildVSConfigModel(ShipInABottleConfig.Client)
    val CLIENT_SPEC: ForgeConfigSpec = VSConfigApi.buildForgeConfigSpec(
        configCategory = client_config.root,
        builder = ForgeConfigSpec.Builder()
    ).build()

    private val server_config = VSConfigApi.buildVSConfigModel(ShipInABottleConfig.Server)
    val SERVER_SPEC: ForgeConfigSpec = VSConfigApi.buildForgeConfigSpec(
        configCategory = server_config.root,
        builder = ForgeConfigSpec.Builder()
    ).build()

    fun update(config: ModConfig) {
        val updatedEntries = mutableSetOf<VSGameEvents.ConfigUpdateEntry>()
        client_config.update(config, ConfigType.CLIENT, updatedEntries)
        server_config.update(config, ConfigType.SERVER, updatedEntries)
    }
}
