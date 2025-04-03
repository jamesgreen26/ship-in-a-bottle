package g_mungus.ship_in_a_bottle.block.entity

import g_mungus.ship_in_a_bottle.ShipInABottle
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.random.Random

class BottleWithShipBlockEntity(pos: BlockPos, state: BlockState, private var shipName: String) :
    BlockEntity(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE, pos, state) {

        /** Use the primary constructor instead when possible **/
    constructor(pos: BlockPos, state: BlockState) : this(pos, state, "")

    val randomRotation: Double = Random.nextDouble()
}