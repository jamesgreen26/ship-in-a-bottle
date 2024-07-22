package g_mungus.ship_in_a_bottle.block;

import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID;
import static g_mungus.ship_in_a_bottle.block.ModBlocks.WATERLOGGED;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;

public class BottleWithShip extends BlockWithEntity implements Waterloggable{


    private String shipName = "sloop";

    public BottleWithShip(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FurnaceBlock.FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }


    protected static final VoxelShape SHAPE_X = Block.createCuboidShape(0.0, 0.0, 2.0, 16.0, 12.0, 14.0);
    protected static final VoxelShape SHAPE_Z = Block.createCuboidShape(2.0, 0.0, 0.0, 14.0, 12.0, 16.0);

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FurnaceBlock.FACING)) {
            default -> SHAPE_Z;
            case WEST, EAST -> SHAPE_X;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FurnaceBlock.FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        ItemStack itemStack = ctx.getStack();

        this.shipName = itemStack.getNbt() != null ? itemStack.getNbt().getString("Ship") : "sloop";

        return this.getDefaultState()
                    .with(FurnaceBlock.FACING, ctx.getHorizontalPlayerFacing())
                    .with(WATERLOGGED, ctx.getWorld().getFluidState(ctx.getBlockPos()).isOf(Fluids.WATER));

    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FurnaceBlock.FACING, rotation.rotate(state.get(FurnaceBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FurnaceBlock.FACING)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            // This is for 1.17 and below: world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BottleWithShipEntity(pos, state, shipName);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BottleWithShipEntity && !world.isClient()) {
            dropStack(world, pos, ((BottleWithShipEntity) be).getItemStack());
        }
        super.onBreak(world, pos, state, player);
    }
}
