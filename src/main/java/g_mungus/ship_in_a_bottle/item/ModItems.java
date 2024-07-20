package g_mungus.ship_in_a_bottle.item;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import static g_mungus.ship_in_a_bottle.block.ModBlocks.SHIPASSEMBLER;


public class ModItems {
    public static final Item BOTTLEWITHOUTSHIP = registerItem("bottle_without_ship", new BottleWithoutShip(ModBlocks.BOTTLEWITHOUTSHIP, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item BOTTLEWITHSHIP = registerItem("bottle_with_ship", new BottleWithShip(new Item.Settings().maxCount(1).rarity(Rarity.RARE)));


    private static void addItemsToToolItemGroup(FabricItemGroupEntries entries) {
        entries.add(BOTTLEWITHOUTSHIP);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ShipInABottle.MOD_ID, name), item);
    }

    public static void registerModItems () {
        ShipInABottle.LOGGER.info("Registering mod items for " + ShipInABottle.MOD_ID);
        Registry.register(Registries.ITEM,new Identifier("ship-in-a-bottle","ship_assembler"),new BlockItem(SHIPASSEMBLER,new Item.Settings()));


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToToolItemGroup);
    }
}
