package g_mungus.ship_in_a_bottle.item

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.vlib.v2.api.VLibAPI
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class BottleWithShipItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents.add(
            Component.literal(
                "Ship: ${stack.orCreateTag.getString("Ship")}"
            )
        )
    }

    override fun place(context: BlockPlaceContext): InteractionResult {
        return InteractionResult.PASS
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        val blockHitResult: BlockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY)
        val blockPos = blockHitResult.blockPos

        if (blockHitResult.type == HitResult.Type.BLOCK) {
            return if (level.getBlockState(blockPos).`is`(Blocks.WATER) && !player.isShiftKeyDown) {
                tryPlaceShip(level, blockPos, player, itemStack)
            } else {
                tryPlaceBlock(level, player, usedHand, itemStack)
            }
        } else {
            explainUsage(level)
            return InteractionResultHolder.pass(itemStack)
        }
    }

    private fun tryPlaceShip(
        level: Level,
        blockPos: BlockPos,
        player: Player,
        itemStack: ItemStack
    ): InteractionResultHolder<ItemStack> {
        val result = ItemStack(ShipInABottle.BOTTLE_WITHOUT_SHIP_ITEM)
        runEffects(level, blockPos)

        itemStack.orCreateTag.getString("Ship").takeUnless { it.isBlank() }?.let { name ->
            if (level is ServerLevel) {
                val template = level.structureManager.get(ResourceLocation(ShipInABottle.MOD_ID, name)).getOrNull()
                    ?: return InteractionResultHolder.fail(result)

                val x = template.size.x
                val y = template.size.y
                val z = template.size.z
                val direction = player.direction

                val placePos = blockPos.offset(direction.normal.multiply(min(x, z) / 2)).offset(0, y/2, 0)

                val ship = VLibAPI.placeTemplateAsShip(template, level, placePos, false)
                ship?.slug = name
            }
            return InteractionResultHolder.success(result)
        }
        return InteractionResultHolder.fail(result)
    }

    private fun tryPlaceBlock(
        level: Level,
        player: Player,
        usedHand: InteractionHand,
        itemStack: ItemStack
    ): InteractionResultHolder<ItemStack> {
        val blockHitResult2 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE)
        return if (blockHitResult2.type == HitResult.Type.BLOCK) {
            super.place(BlockPlaceContext(player, usedHand, itemStack, blockHitResult2))
            InteractionResultHolder.success(itemStack)
        } else {
            InteractionResultHolder.pass(itemStack)
        }
    }

    private fun explainUsage(level: Level) {
        if (level.isClientSide()) {
            val mc = Minecraft.getInstance()
            mc.gui.setOverlayMessage(Component.literal("She needs the sea."), false)
        }
    }

    private fun runEffects(level: Level, blockPos: BlockPos) {
        level.playSound(
            null,
            blockPos,
            SoundEvents.EVOKER_PREPARE_SUMMON,
            SoundSource.PLAYERS,
            1f,
            1f
        )
    }
}