package g_mungus.ship_in_a_bottle.item

import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.config.ShipInABottleConfig
import g_mungus.ship_in_a_bottle.networking.NetworkUtils
import g_mungus.vlib.v2.api.VLibAPI
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.random.Random

class BottleWithoutShipItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun place(context: BlockPlaceContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        val ship = level.getShipManagingPos(pos)


        if (ship != null && context.player?.isShiftKeyDown != true) {
            val player = context.player ?: return InteractionResult.PASS
            if (player.experienceLevel >= ShipInABottleConfig.Server.bottleXpCost || player.isCreative) {
                if (level is ServerLevel && ship is ServerShip) {
                    runEffects(ship.shipAABB, level, pos)

                    VLibAPI.saveShipToTemplate(
                        ship,
                        ResourceLocation(
                            ShipInABottle.MOD_ID,
                            ship.slug ?: level.random.nextInt().toString()
                        ), level
                    )
                    VLibAPI.discardShip(ship, level)

                    NetworkUtils.updateClientShipData(
                        level.server,
                        ship.slug.toString(),
                        level.server.playerList.players
                    )
                }
                context.player?.setItemInHand(
                    context.hand,
                    ItemStack(ShipInABottle.BOTTLE_WITH_SHIP_ITEM).apply {
                        tag = CompoundTag().apply { putString("Ship", ship.slug.toString()) }
                    }
                )
                if (!context.level.isClientSide && ShipInABottleConfig.Server.bottleXpCost > 0) {
                    if (!player.isCreative) player.giveExperiencePoints(
                        -1 * (xpCostByLevel.getOrNull(
                            ShipInABottleConfig.Server.bottleXpCost
                        ) ?: xpCostByLevel.last())
                    )
                }
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

    val xpCostByLevel = listOf(
        0, 7, 16, 27, 40, 55, 72, 91, 112, 135, 160, 187, 216, 247, 280, 315, 352, 394, 441, 493, 550, 612, 679, 751, 828, 910, 997, 1089, 1186, 1288, 1395
    )
}