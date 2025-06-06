package g_mungus.ship_in_a_bottle.block

import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class BottleWithShipBlock(properties: Properties) : BaseEntityBlock(properties), SimpleWaterloggedBlock {
    private var shipName = BottleWithShipBlockEntity.NO_SHIP
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = BottleWithShipBlockEntity(pos, state, shipName)

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player) {
        val be: BlockEntity? = level.getBlockEntity(pos)
        if (be is BottleWithShipBlockEntity && !level.isClientSide && !player.isCreative) {
            popResource(level, pos, be.getItemStack())
        }
        super.playerWillDestroy(level, pos, state, player)
    }

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

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val itemStack = context.itemInHand
        shipName = itemStack.tag?.get("Ship")?.asString?: BottleWithShipBlockEntity.NO_SHIP

        return super.getStateForPlacement(context)
            ?.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.clockWise)
            ?.setValue(BlockStateProperties.WATERLOGGED, context.level.getBlockState(context.clickedPos).`is`(Blocks.WATER))
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        world: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world))
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos)
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
    }
}