package g_mungus.ship_in_a_bottle.item;

import g_mungus.ship_in_a_bottle.util.StructurePlacer;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.LightType;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;
import org.valkyrienskies.mod.common.assembly.ShipAssemblyKt;

import java.util.List;
import java.util.Objects;

public class BottleWithShip extends Item {


    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        String shipName;
        if (itemStack.hasNbt() && itemStack.getNbt().contains("Ship")) {
            shipName = itemStack.getNbt().getString("Ship");
        } else {
            shipName = "Sloop";
        }
        tooltip.add(Text.of("Ship: " + shipName));
    }


    public BottleWithShip(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (blockHitResult.getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        } else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            Direction direction = blockHitResult.getSide();
            BlockPos blockPos2 = blockPos.offset(direction);
            if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos2, direction, itemStack)) {
                BlockState blockState;


                blockState = world.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.WATER) {

                    int yLevel = blockPos.getY();
                    while (world.getLightLevel(LightType.SKY, blockPos.withY(yLevel)) < 15 && yLevel < world.getHeight()) {
                        ++yLevel;
                    }
                    if (yLevel == world.getHeight()) {
                        yLevel = blockPos.getY();
                    }

                    blockPos = new BlockPos(blockPos.withY(yLevel));

                    if (!world.isClient) {
                        world.playSound(
                                null,
                                blockPos,
                                SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON,
                                SoundCategory.PLAYERS,
                                1f,
                                1f
                        );

                        String shipName;
                        if (itemStack.hasNbt() && Objects.requireNonNull(itemStack.getNbt()).contains("Ship")) {
                            shipName = itemStack.getNbt().getString("Ship");
                        } else {
                            shipName = "sloop";
                        }

                        StructurePlacer placer = new StructurePlacer((ServerWorld) world, new Identifier("ship-in-a-bottle", shipName), blockPos);

                        Vec3i size = placer.getSize();

                        Direction facing = user.getHorizontalFacing();
                        BlockRotation rotation;
                        BlockPos placerOffset;


                        if (facing == Direction.NORTH) {
                            rotation = BlockRotation.NONE;
                            placerOffset = new BlockPos((size.getX() / -2), 10, (size.getZ() / -2) - (size.getZ() / 2));
                        } else if (facing == Direction.EAST) {
                            rotation = BlockRotation.CLOCKWISE_90;
                            placerOffset = new BlockPos((size.getZ() / 2) + (size.getZ() / 2), 10, (size.getX() / -2));
                        } else if (facing == Direction.SOUTH) {
                            rotation = BlockRotation.CLOCKWISE_180;
                            placerOffset = new BlockPos((size.getX() / 2), 10, (size.getZ() / 2) + (size.getZ() / 2));
                        } else {
                            rotation = BlockRotation.COUNTERCLOCKWISE_90;
                            placerOffset = new BlockPos((size.getZ() / -2) - (size.getZ() / 2), 10, (size.getX() / 2));
                        }


                        placer.setOffset(placerOffset);
                        placer.setRotation(rotation);

                        if (placer.loadStructure()) {
                            BlockPos pos = blockPos.add(placerOffset);
                            ServerWorld serverWorld = (ServerWorld) world;


                            DenseBlockPosSet set = new DenseBlockPosSet();


                            for (int x = 0; x < size.getX(); ++x) {
                                for (int y = 0; y < size.getY(); ++y) {
                                    for (int z = 0; z < size.getZ(); ++z) {
                                        BlockPos goober = new BlockPos(x, y, z).rotate(rotation);
                                        BlockPos adjusted = blockPos.add(placerOffset).add(goober);
                                        set.add(adjusted.getX(), adjusted.getY(), adjusted.getZ());


                                    }
                                }
                            }


                            Intrinsics.checkNotNull(pos);
                            if (!set.isEmpty()) {
                                ServerShip ship = ShipAssemblyKt.createNewShipWithBlocks(pos.add(size.getX() / 2, size.getY() / 2, size.getZ() / 2), set, serverWorld);
                                ship.setSlug(shipName);
                            }


                            //Objects.requireNonNull(ShipAssembler.INSTANCE.collectBlocks((ServerWorld) world, pos, a -> !a.isAir() && !a.isOf(Blocks.WATER) && !a.isOf(Blocks.KELP) && !a.isOf(Blocks.KELP_PLANT) && !EurekaConfig.SERVER.getBlockBlacklist().contains(Registries.BLOCK.getKey(a.getBlock()).toString()))).setSlug(shipName);

                        }


                    }

                    return TypedActionResult.success(new ItemStack(ModItems.BOTTLEWITHOUTSHIP), world.isClient());
                } else if (world.isClient()) {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    mc.inGameHud.setOverlayMessage(Text.of("She needs the sea."), false);
                }
            }
        }
        return TypedActionResult.fail(itemStack);
    }

}
