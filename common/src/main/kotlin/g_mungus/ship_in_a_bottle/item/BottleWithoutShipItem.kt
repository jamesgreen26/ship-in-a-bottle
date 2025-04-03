package g_mungus.ship_in_a_bottle.item

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.vlib.api.VLibGameUtils
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import org.joml.primitives.AABBic
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.random.Random

class BottleWithoutShipItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun place(context: BlockPlaceContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        val ship = level.getShipManagingPos(pos)


        if (ship != null && context.player?.isShiftKeyDown != true) {
            val player = context.player?: return InteractionResult.PASS
            if (player.experienceLevel >= 30 || player.isCreative) {
                if (level is ServerLevel) {
                    runEffects(ship.shipAABB, level, pos)

                    VLibGameUtils.saveShipToTemplate(ShipInABottle.MOD_ID, level, ship.id, false, true)
                    NetworkUtils.updateClientShipData(level.server, ship.slug.toString(), level.server.playerList.players)
                }
                context.player?.setItemInHand(
                    context.hand,
                    ItemStack(ShipInABottle.BOTTLE_WITH_SHIP_ITEM).apply {
                        tag = CompoundTag().apply { putString("Ship", ship.slug.toString()) }
                    }
                )
                if (!player.isCreative) player.giveExperienceLevels(-30)
                context.player?.swing(context.hand)
                return InteractionResult.SUCCESS
            } else {
                if (context.level.isClientSide) {
                    Minecraft.getInstance().gui.setOverlayMessage(Component.literal("Not enough levels."), false)
                }
                return InteractionResult.FAIL
            }
        } else {
            return super.place(context)
        }
    }



    private fun runEffects(
        box: AABBic?,
        level: ServerLevel,
        pos: BlockPos
    ) {
        box?.let {
            for (x in it.minX()..it.maxX()) {
                for (y in it.minY()..it.maxY()) {
                    for (z in it.minZ()..it.maxZ()) {
                        if (!level.getBlockState(BlockPos(x, y, z)).isAir && Random.nextDouble() > 0.7) {
                            level.sendParticles(ParticleTypes.END_ROD, x + 0.5, y + 0.5, z + 0.5, 1, 0.0, 0.0, 0.0, 0.0)
                        }
                    }
                }
            }
        }

        level.playSound(
            null,
            pos,
            SoundEvents.EVOKER_PREPARE_ATTACK,
            SoundSource.PLAYERS,
            1f,
            1f
        )
    }
}