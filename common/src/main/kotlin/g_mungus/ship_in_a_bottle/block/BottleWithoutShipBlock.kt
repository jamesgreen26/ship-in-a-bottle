package g_mungus.ship_in_a_bottle.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BottleWithoutShipBlock(properties: Properties) : Block(properties), SimpleWaterloggedBlock {

    init {
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.WATERLOGGED)
    }

    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return when (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.WEST, Direction.EAST -> box(0.0, 0.0, 2.0, 16.0, 12.0, 14.0)
            else -> box(2.0, 0.0, 0.0, 14.0, 12.0, 16.0)
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    fun isTransparent(state: BlockState?, world: BlockGetter?, pos: BlockPos?): Boolean {
        return true
    }
}