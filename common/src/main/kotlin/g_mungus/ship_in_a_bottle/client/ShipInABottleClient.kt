package g_mungus.ship_in_a_bottle.client

import g_mungus.ship_in_a_bottle.ShipInABottle.LOGGER
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.ship_in_a_bottle.networking.DisplayableShipData
import java.util.concurrent.ConcurrentHashMap

object ShipInABottleClient {
    val shipDisplayData: ConcurrentHashMap<String, DisplayableShipData> = ConcurrentHashMap()


    @JvmStatic
    fun init() {
        LOGGER.info("client init")

        NetworkUtils.initializeClient()
    }
}