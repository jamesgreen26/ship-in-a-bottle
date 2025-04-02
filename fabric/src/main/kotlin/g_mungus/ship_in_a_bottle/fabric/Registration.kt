package g_mungus.ship_in_a_bottle.fabric

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID
import g_mungus.ship_in_a_bottle.block.BottleWithShipBlock
import g_mungus.ship_in_a_bottle.block.BottleWithoutShipBlock
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import g_mungus.ship_in_a_bottle.item.BottleWithShipItem
import g_mungus.ship_in_a_bottle.item.BottleWithoutShipItem
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour


fun registerBlocks() {
    ShipInABottle.LOGGER.info("Registering blocks")

    ShipInABottle.BOTTLE_WITH_SHIP_BLOCK = registerBlock(
        "bottle_with_ship",
        BottleWithShipBlock(FabricBlockSettings.create().nonOpaque().hardness(0.8f))
    )

    ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK = registerBlock(
        "bottle_without_ship", BottleWithoutShipBlock(

            FabricBlockSettings.create().drops(ResourceLocation(MOD_ID, "blocks/bottle_without_ship")).nonOpaque()
                .hardness(0.8f)
        )
    )

    ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.tryBuild(MOD_ID, "bottle_with_ship_entity")!!,
        BlockEntityType.Builder.of({ pos, state -> BottleWithShipBlockEntity(pos, state) }, ShipInABottle.BOTTLE_WITH_SHIP_BLOCK).build(null)
    )
}

fun registerItems() {
    ShipInABottle.LOGGER.info("Registering items")

    ShipInABottle.BOTTLE_WITHOUT_SHIP_ITEM = registerItem(
        "bottle_without_ship",
        BottleWithoutShipItem(ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK, Properties().stacksTo(1).rarity(Rarity.RARE))
    )

    ShipInABottle.BOTTLE_WITH_SHIP_ITEM = registerItem(
        "bottle_with_ship",
        BottleWithShipItem(ShipInABottle.BOTTLE_WITH_SHIP_BLOCK, Properties().stacksTo(1).rarity(Rarity.RARE))
    )

    ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
        .register(ItemGroupEvents.ModifyEntries { entries: FabricItemGroupEntries? ->
            addItemsToToolItemGroup(entries!!)
        })
}


private fun registerBlock(name: String, block: Block): Block {
    return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.tryBuild(MOD_ID, name)!!, block)
}

private fun registerItem(name: String, item: Item): Item {
    return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryBuild(MOD_ID, name)!!, item)
}

private fun addItemsToToolItemGroup(entries: FabricItemGroupEntries) {
    entries.accept(ShipInABottle.BOTTLE_WITHOUT_SHIP_ITEM)
}