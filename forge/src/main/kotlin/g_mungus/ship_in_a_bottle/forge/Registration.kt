package g_mungus.ship_in_a_bottle.forge

import g_mungus.ship_in_a_bottle.ShipInABottle.MOD_ID
import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.block.BottleWithShipBlock
import g_mungus.ship_in_a_bottle.block.BottleWithoutShipBlock
import g_mungus.ship_in_a_bottle.block.WaterDisplayBlock
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import g_mungus.ship_in_a_bottle.forge.Blocks.BOTTLE_WITHOUT_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.forge.Blocks.BOTTLE_WITH_SHIP_BLOCK
import g_mungus.ship_in_a_bottle.forge.Blocks.WATER_DISPLAY_BLOCK
import g_mungus.ship_in_a_bottle.item.BottleWithShipItem
import g_mungus.ship_in_a_bottle.item.BottleWithoutShipItem
import g_mungus.ship_in_a_bottle.item.ShipModelItem
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object Blocks {
    private val register: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)


    val BOTTLE_WITH_SHIP_BLOCK: RegistryObject<BottleWithShipBlock> = register.register("bottle_with_ship") {
        BottleWithShipBlock(FabricBlockSettings.create().nonOpaque().hardness(0.8f))
    }

    val BOTTLE_WITHOUT_SHIP_BLOCK: RegistryObject<BottleWithoutShipBlock> =
        register.register("bottle_without_ship") {
            BottleWithoutShipBlock(
                FabricBlockSettings.create()
                    .drops(ResourceLocation(MOD_ID, "blocks/bottle_without_ship"))
                    .nonOpaque()
                    .hardness(0.8f)
            )
        }

    val WATER_DISPLAY_BLOCK: RegistryObject<WaterDisplayBlock> = register.register(
        "water_display_block"
    ) { WaterDisplayBlock() }


    fun register(eventBus: IEventBus?) {
        register.register(eventBus)
    }

    fun setup(event: FMLCommonSetupEvent) {
        ShipInABottle.BOTTLE_WITH_SHIP_BLOCK = BOTTLE_WITH_SHIP_BLOCK.get()
        ShipInABottle.BOTTLE_WITHOUT_SHIP_BLOCK = BOTTLE_WITHOUT_SHIP_BLOCK.get()
        ShipInABottle.WATER_DISPLAY_BLOCK = WATER_DISPLAY_BLOCK.get()
    }
}

object BlockEntities {
    private val register: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID)


    private val BOTTLE_WITH_SHIP_BE_TYPE = register.register(
        "bottle_with_ship_entity"
    ) {
        BlockEntityType.Builder.of({ pos, state ->
            BottleWithShipBlockEntity(
                pos,
                state,
                BottleWithShipBlockEntity.NO_SHIP
            )
        }, BOTTLE_WITH_SHIP_BLOCK.get()).build(null)
    }


    fun register(eventBus: IEventBus?) {
        register.register(eventBus)
    }

    fun setup(event: FMLCommonSetupEvent) {
        ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE = BOTTLE_WITH_SHIP_BE_TYPE.get()
    }
}

object Items {
    private val register: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)

    private val BOTTLE_WITHOUT_SHIP_ITEM = register.register(
        "bottle_without_ship"
    ) {
        BottleWithoutShipItem(BOTTLE_WITHOUT_SHIP_BLOCK.get(), Properties().stacksTo(1).rarity(Rarity.RARE))
    }

    private val BOTTLE_WITH_SHIP_ITEM = register.register(
        "bottle_with_ship"
    ) {
        BottleWithShipItem(BOTTLE_WITH_SHIP_BLOCK.get(), Properties().stacksTo(1).rarity(Rarity.RARE))
    }

    private val SHIP_MODEL_ITEM = register.register(
        "ship_model"
    ) {
        ShipModelItem()
    }


    fun register(eventBus: IEventBus?) {
        register.register(eventBus)
    }

    fun setup(event: FMLCommonSetupEvent) {
        ShipInABottle.BOTTLE_WITH_SHIP_ITEM = BOTTLE_WITH_SHIP_ITEM.get()
        ShipInABottle.BOTTLE_WITHOUT_SHIP_ITEM = BOTTLE_WITHOUT_SHIP_ITEM.get()
        ShipInABottle.SHIP_MODEL_ITEM = SHIP_MODEL_ITEM.get()
    }
}