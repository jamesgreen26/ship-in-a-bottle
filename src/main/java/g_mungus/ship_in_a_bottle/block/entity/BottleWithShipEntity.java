package g_mungus.ship_in_a_bottle.block.entity;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.item.ModItems;
import g_mungus.ship_in_a_bottle.util.BlockInfoListProvider;
import g_mungus.ship_in_a_bottle.util.DisplayableShipData;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BottleWithShipEntity extends BlockEntity {

    private String shipName;
    public final double randomRotation;

    public String getShipName() {
        return shipName;
    }

    public BottleWithShipEntity(BlockPos pos, BlockState state) {
        this(pos, state, "sloop");
    }

    public BottleWithShipEntity(BlockPos pos, BlockState state, String shipName) {
        super(ModBlocks.BOTTLEWITHSHIPENTITY, pos, state);
        this.shipName = shipName;
        randomRotation = Math.random();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world != null && !world.isClient()) {
            try {
                StructureTemplateManager manager = ((ServerWorld) world).getStructureTemplateManager();
                StructureTemplate template = manager.getTemplate(new Identifier(ShipInABottle.MOD_ID, shipName)).get();
                List<StructureTemplate.PalettedBlockInfoList> beans = ((BlockInfoListProvider) template).ship_in_a_bottle$getBlockInfoList();


                DisplayableShipData data = new DisplayableShipData(shipName);

                AtomicInteger i  = new AtomicInteger();

                beans.forEach(it -> {
                    it.getAll().forEach(structureBlockInfo -> {
                        if (i.get() < 50) {
                            data.data.add(
                                    new DisplayableShipData.BlockInfo(structureBlockInfo.pos(),
                                            Registries.BLOCK.getId(structureBlockInfo.state().getBlock()),
                                            structureBlockInfo.state().toString())
                            );
                            i.getAndIncrement();
                        }
                    });
                });

                PacketByteBuf buf1 = PacketByteBufs.create();
                buf1.writeString(DisplayableShipData.serialize(data));

                world.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                    if (serverPlayerEntity.getServerWorld().getDimensionKey() == DimensionTypes.OVERWORLD) {
                        ServerPlayNetworking.send(serverPlayerEntity, ShipInABottle.SHIP_PACKET_ID, buf1);
                        System.out.println("Sending packet to " + serverPlayerEntity.getEntityName());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Skipping. World: " + world);
        }
    }

    public ItemStack getItemStack() {
        ItemStack itemStack = new ItemStack(ModItems.BOTTLEWITHSHIP);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("Ship", shipName);
        itemStack.setNbt(nbt);
        return itemStack;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.shipName = nbt.getString("shipName");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("shipName", this.shipName);
    }
}
