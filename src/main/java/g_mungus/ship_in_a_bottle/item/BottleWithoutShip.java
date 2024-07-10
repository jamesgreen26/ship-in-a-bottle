package g_mungus.ship_in_a_bottle.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BottleWithoutShip extends Item {
    public BottleWithoutShip (Item.Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        ItemStack newItemStack = new ItemStack(ModItems.BOTTLEWITHSHIP);

        NbtCompound nbt = newItemStack.getOrCreateNbt();
        nbt.putString("Ship", "Blue42"); // Example custom field
        newItemStack.setNbt(nbt);

        return TypedActionResult.success(newItemStack, world.isClient());

    }
}
