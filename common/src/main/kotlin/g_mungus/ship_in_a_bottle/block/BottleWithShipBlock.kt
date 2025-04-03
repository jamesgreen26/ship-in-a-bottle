package g_mungus.ship_in_a_bottle.block

import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BottleWithShipBlock(properties: Properties) : BaseEntityBlock(properties), SimpleWaterloggedBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BottleWithShipBlockEntity(pos, state)

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

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)
            ?.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection)
            ?.setValue(BlockStateProperties.WATERLOGGED, context.level.getBlockState(context.clickedPos).`is`(Blocks.WATER))
    }
}