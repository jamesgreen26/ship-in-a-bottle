package g_mungus.ship_in_a_bottle.item;

import g_mungus.ship_in_a_bottle.util.StructurePlacer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BiomeTags;
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
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.List;

public class BottleWithShip extends Item {
    private final String shipName;


    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(Text.of("Ship: " + shipName));
    }


    public BottleWithShip(Settings settings) {
        super(settings);
        shipName = "HMS Placeholder";
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
                if (blockState.getBlock() == Blocks.WATER && world.getBiome(blockPos).isIn(BiomeTags.IS_OCEAN)) {

                    if (!world.isClient) {
                        world.playSound(
                                null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                                blockPos, // The position of where the sound will come from
                                SoundEvents.ITEM_BUCKET_FILL, // The sound that will play, in this case, the sound the anvil plays when it lands.
                                SoundCategory.BLOCKS, // This determines which of the volume sliders affect this sound
                                1f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                                1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
                        );



                        StructurePlacer placer = new StructurePlacer((ServerWorld) world, new Identifier("ship-in-a-bottle", "sloop"), blockPos);

                        Vec3i size = placer.getSize();

                        Direction facing = user.getHorizontalFacing();
                        BlockRotation rotation;
                        BlockPos placerOffset;

                        if (facing == Direction.NORTH) {
                            rotation = BlockRotation.NONE;
                            placerOffset = new BlockPos((size.getX() / -2), 10, (size.getZ() / -2) - 5);
                        } else if (facing == Direction.EAST) {
                            rotation = BlockRotation.CLOCKWISE_90;
                            placerOffset = new BlockPos((size.getZ() / 2) + 5, 10, (size.getX() / -2));
                        } else if (facing == Direction.SOUTH) {
                            rotation = BlockRotation.CLOCKWISE_180;
                            placerOffset = new BlockPos((size.getX() / 2), 10, (size.getZ() / 2) + 5);
                        } else {
                            rotation = BlockRotation.COUNTERCLOCKWISE_90;
                            placerOffset = new BlockPos((size.getZ() / -2) - 5, 10, (size.getX() / 2));
                        }



                        placer.setOffset(placerOffset);
                        placer.setRotation(rotation);

                        placer.loadStructure();
                    }
                    return TypedActionResult.success(new ItemStack(ModItems.BOTTLEWITHOUTSHIP), world.isClient());
                } else if (world.isClient()){
                    MinecraftClient mc = MinecraftClient.getInstance();
                    mc.inGameHud.setOverlayMessage(Text.of("She needs the sea."), false);
                }
            }
        }
        return TypedActionResult.fail(itemStack);
    }

}
