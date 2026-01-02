package g_mungus.ship_in_a_bottle.config

import org.valkyrienskies.core.internal.config.ConfigEntry

object ShipInABottleConfig {
    object Client {
        @ConfigEntry(description = "Affects how ships inside placed bottles are rendered")
        var renderQuality = RenderQuality.Fancy
    }

    object Server {
        @ConfigEntry(description = "How many experience points are required to bottle a ship")
        var bottleXpCost = 10
    }


}
enum class RenderQuality {
    Fast, Fancy
}
