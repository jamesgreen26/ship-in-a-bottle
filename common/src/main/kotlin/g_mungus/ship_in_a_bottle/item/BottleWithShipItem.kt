package g_mungus.ship_in_a_bottle.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

class BottleWithShipItem(block: Block, properties: Properties): BlockItem(block, properties) {

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        tooltipComponents.add(Component.literal("Ship: ${
            stack.orCreateTag.getString("Ship")?: "Sloop"
        }"))
    }
}