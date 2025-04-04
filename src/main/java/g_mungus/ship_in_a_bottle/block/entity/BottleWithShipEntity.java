package g_mungus.ship_in_a_bottle.block.entity;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

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
        markDirty();
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

    @Override
    @Nullable
    public Object getRenderData() {
        return this.shipName;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
