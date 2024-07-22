package g_mungus.ship_in_a_bottle.block;

import g_mungus.ship_in_a_bottle.ShipInABottle;
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Attachment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import static g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID;

public class ModBlocks {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public static final Block SHIPASSEMBLER = registerBlock("ship_assembler", new ShipAssembler(AbstractBlock.Settings.create()));
    public static final Block BOTTLEWITHOUTSHIP = registerBlock("bottle_without_ship", new BottleWithoutShip(FabricBlockSettings.create().drops(new Identifier(MOD_ID, "blocks/bottle_without_ship")).nonOpaque().hardness(0.8f)));
    public static final Block BOTTLEWITHSHIP = registerBlock("bottle_with_ship", new BottleWithShip(FabricBlockSettings.create().nonOpaque().hardness(0.8f)));
    public static final Block WATERBLOCK = registerBlock("water_block_util", new WaterBlock(AbstractBlock.Settings.copy(Blocks.BLUE_STAINED_GLASS)));
    public static BlockEntityType<BottleWithShipEntity> BOTTLEWITHSHIPENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MOD_ID, "bottle_with_ship_entity"),  BlockEntityType.Builder.create(BottleWithShipEntity::new, BOTTLEWITHSHIP).build(null));


    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(MOD_ID, name), block);
    }



    public static void registerModBlocks () {
        ShipInABottle.LOGGER.info("Registering mod blocks for " + MOD_ID);
    }
}
