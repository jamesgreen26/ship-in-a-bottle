package g_mungus.ship_in_a_bottle

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ShipInABottle {
    const val MOD_ID = "ship_in_a_bottle"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)


    @JvmStatic
    fun init() {
        LOGGER.info("init {}", MOD_ID)

    }

    @JvmStatic
    fun initClient() {

    }
}