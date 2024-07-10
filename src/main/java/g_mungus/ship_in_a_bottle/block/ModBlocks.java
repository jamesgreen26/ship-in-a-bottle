package g_mungus.ship_in_a_bottle.block;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.item.BottleWithShip;
import g_mungus.ship_in_a_bottle.item.BottleWithoutShip;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModBlocks {
    public static final Block SHIPASSEMBLER = registerBlock("ship_assembler", new ShipAssembler(AbstractBlock.Settings.create()));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(ShipInABottle.MOD_ID, name), block);
    }

    public static void registerModItems () {
        ShipInABottle.LOGGER.info("Registering mod blocks for " + ShipInABottle.MOD_ID);
    }
}
