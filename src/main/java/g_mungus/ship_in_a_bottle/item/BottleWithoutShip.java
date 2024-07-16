package g_mungus.ship_in_a_bottle.item;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID;

public class BottleWithoutShip extends Item {
    public BottleWithoutShip(Item.Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
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
                String shipName = ship.getSlug();
                nbt.putString("Ship", shipName);
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

                    Integer[] maxShipCoords = {blockPos.getX(), blockPos.getY(), blockPos.getZ()};
                    Integer[] minShipCoords = {blockPos.getX(), blockPos.getY(), blockPos.getZ()};


                    ServerWorld serverWorld = (ServerWorld) world;
                    List<BlockPos> toBreak = new LinkedList<>();

                    for (int i = minWorldPos.x; i <= maxWorldPos.x; i++) {
                        for (int j = minWorldPos.y; j <= maxWorldPos.y; j++) {
                            for (int k = minWorldPos.z; k <= maxWorldPos.z; k++) {

                                BlockPos pos = new BlockPos(i, j, k);

                                if (world.getBlockState(pos).getBlock() != Blocks.AIR) {
                                    toBreak.add(pos);


                                }

                            }
                        }
                    }

                    for (BlockPos pos : toBreak) {

                        Integer[] currentpos = {pos.getX(), pos.getY(), pos.getZ()};

                        for (int i = 0; i < 3; i++) {
                            if (maxShipCoords[i] < currentpos[i]) {
                                maxShipCoords[i] = currentpos[i];
                            }
                            if (minShipCoords[i] > currentpos[i]) {
                                minShipCoords[i] = currentpos[i];
                            }
                        }
                    }

                    Vec3i shipSize = new Vec3i((maxShipCoords[0] - minShipCoords[0] + 1), (maxShipCoords[1] - minShipCoords[1] + 1), (maxShipCoords[2] - minShipCoords[2] + 1));

                    System.out.println("Ship width: " + shipSize.getX());
                    System.out.println("Ship height: " + shipSize.getY());
                    System.out.println("Ship length: " + shipSize.getZ());

                    //

                    BlockPos structureStart = new BlockPos(minShipCoords[0], minShipCoords[1], minShipCoords[2]);

                    StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();

                    StructureTemplate structureTemplate;
                    Identifier structureID;
                    try {
                        structureID = new Identifier(MOD_ID, Objects.requireNonNullElse(shipName, "placeholder"));
                        structureTemplate = structureTemplateManager.getTemplateOrBlank(structureID);

                        structureTemplate.saveFromWorld(world, structureStart, shipSize, true, Blocks.AIR);
                        structureTemplateManager.saveTemplate(structureID);
                    } catch (InvalidIdentifierException var8) {
                        return TypedActionResult.pass(itemStack);
                    }

                    //

                    for (BlockPos pos : toBreak) {
                        if (world.getBlockState(pos).hasBlockEntity()) {
                            BlockEntity blockEntity = world.getBlockEntity(pos);
                            if (blockEntity != null && blockEntity.getCachedState() != null) {
                                world.removeBlockEntity(pos);
                            }
                        }

                        world.setBlockState(pos, Blocks.AIR.getDefaultState(), 0, 0);

                        if (Math.random() > 0.7) {
                            serverWorld.spawnParticles(ParticleTypes.END_ROD, pos.getX(), pos.getY(), pos.getZ(), 1, 0, 0, 0, 0);
                        }
                    }
                    world.playSound(
                            null,
                            blockPos,
                            SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK,
                            SoundCategory.PLAYERS,
                            1f,
                            1f
                    );

                }
                return TypedActionResult.success(newItemStack, world.isClient());
            } else {
                return TypedActionResult.pass(itemStack);

            }
        }
    }
}
