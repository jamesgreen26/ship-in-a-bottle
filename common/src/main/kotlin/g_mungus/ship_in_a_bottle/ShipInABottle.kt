package g_mungus.ship_in_a_bottle

import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ShipInABottle {
    const val MOD_ID = "ship_in_a_bottle"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    lateinit var BOTTLE_WITH_SHIP_ITEM: Item
    lateinit var BOTTLE_WITHOUT_SHIP_ITEM: Item
    lateinit var SHIP_MODEL_ITEM: Item
    lateinit var BOTTLE_WITH_SHIP_BLOCK: Block
    lateinit var BOTTLE_WITHOUT_SHIP_BLOCK: Block
    lateinit var WATER_DISPLAY_BLOCK: Block
    lateinit var BOTTLE_WITH_SHIP_BE_TYPE: BlockEntityType<BottleWithShipBlockEntity>


    @JvmStatic
    fun init() {
        LOGGER.info("init {}", MOD_ID)

    }
}