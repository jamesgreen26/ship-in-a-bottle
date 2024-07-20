package g_mungus.ship_in_a_bottle.block;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID;

public class ModBlocks {
    public static final Block SHIPASSEMBLER = registerBlock("ship_assembler", new ShipAssembler(AbstractBlock.Settings.create()));
    public static final Block BOTTLEWITHOUTSHIP = registerBlock("bottle_without_ship", new BottleWithoutShip(FabricBlockSettings.create().drops(new Identifier(MOD_ID, "blocks/bottle_without_ship")).nonOpaque().hardness(0.8f)));


    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
    }

    public static void registerModBlocks () {
        ShipInABottle.LOGGER.info("Registering mod blocks for " + MOD_ID);
    }
}
