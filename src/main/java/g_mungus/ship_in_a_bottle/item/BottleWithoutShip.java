package g_mungus.ship_in_a_bottle.item;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.World;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.apigame.constraints.VSConstraintKt;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.physics_api.voxel.LodBlockBoundingBox;
import org.valkyrienskies.physics_api.voxel.LodBlockBoundingBoxKt;

import java.util.Collection;

public class BottleWithoutShip extends Item {
    public BottleWithoutShip(Item.Settings settings) {
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
            if (VSGameUtilsKt.isBlockInShipyard(world, blockPos)) {
                Ship ship = VSGameUtilsKt.getShipManagingPos(world, blockPos);
                ItemStack newItemStack = new ItemStack(ModItems.BOTTLEWITHSHIP);

                NbtCompound nbt = newItemStack.getOrCreateNbt();
                assert ship != null;
                nbt.putString("Ship", ship.getSlug());
                newItemStack.setNbt(nbt);


                IShipActiveChunksSet activeChunks = ship.getActiveChunksSet();
                Vector3i minWorldPos = new Vector3i();
                Vector3i maxWorldPos = new Vector3i();

                int minY = world.getBottomY();
                int maxY = world.getTopY();
                maxY = (maxY / 16) * 16 - 1;

                LevelYRange yRange = new LevelYRange(minY, maxY);

                activeChunks.getMinMaxWorldPos(minWorldPos, maxWorldPos, yRange);

                int width = maxWorldPos.x - minWorldPos.x + 1;
                int height = maxWorldPos.y - minWorldPos.y + 1;
                int length = maxWorldPos.z - minWorldPos.z + 1;

                System.out.println("Width: " + width);
                System.out.println("Height: " + height);
                System.out.println("Length: " + length);

                if (!world.isClient()) {
                    for (int i = minWorldPos.x; i <= maxWorldPos.x; i++) {
                        for (int j = minWorldPos.y; j <= maxWorldPos.y; j++) {
                            for (int k = minWorldPos.z; k <= maxWorldPos.z; k++) {
//                                world.setBlockState(new BlockPos(i,j,k), Blocks.AIR.getDefaultState());
                                BlockPos toBreak = new BlockPos(i, j, k);
                                BlockState toBreakState = world.getBlockState(toBreak);
                                if (toBreakState.hasBlockEntity()) {
                                    BlockEntity blockEntity = world.getBlockEntity(toBreak);

                                    // Check if the block entity is not null and has NBT data
                                    if (blockEntity != null && blockEntity.getCachedState() != null) {
                                        // Get the existing NBT tag compound
                                        world.removeBlockEntity(toBreak);
                                    }
                                }
                                if (toBreakState.getBlock() != Blocks.AIR) {
                                    ServerWorld serverWorld = (ServerWorld) world;
                                    if (Math.random() > 0.7) {
                                        serverWorld.spawnParticles(ParticleTypes.END_ROD, toBreak.getX(), toBreak.getY(), toBreak.getZ(), 1, 0, 0, 0, 0);
                                    }
                                }
                                world.setBlockState(toBreak, Blocks.AIR.getDefaultState(), 0, 0);
                            }
                        }
                    }
                    world.playSound(
                            null, // Player - if non-null, will play sound for every nearby player *except* the specified player
                            blockPos, // The position of where the sound will come from
                            SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, // The sound that will play, in this case, the sound the anvil plays when it lands.
                            SoundCategory.PLAYERS, // This determines which of the volume sliders affect this sound
                            1f, //Volume multiplier, 1 is normal, 0.5 is half volume, etc
                            1f // Pitch multiplier, 1 is normal, 0.5 is half pitch, etc
                    );
                    user.giveItemStack(newItemStack);
                }
                itemStack.decrement(1);
                return TypedActionResult.success(itemStack, world.isClient());
            } else {
                return TypedActionResult.pass(itemStack);

            }
        }
    }
}
