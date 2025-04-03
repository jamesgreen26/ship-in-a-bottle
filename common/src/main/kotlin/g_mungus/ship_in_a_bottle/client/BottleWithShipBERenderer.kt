package g_mungus.ship_in_a_bottle.client

import com.mojang.blaze3d.vertex.PoseStack
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Quaternionf
import kotlin.math.cos
import kotlin.math.sin

class BottleWithShipBERenderer: BlockEntityRenderer<BottleWithShipBlockEntity> {
    override fun render(
        blockEntity: BottleWithShipBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val blockRenderer = Minecraft.getInstance().blockRenderer
        val itemRenderer = Minecraft.getInstance().itemRenderer

        val level = blockEntity.level?: return

        val blockState = blockEntity.blockState
        val direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val rotation = Math.toRadians(direction.toYRot().toDouble())
        val worldTime: Float = level.gameTime.toFloat()

        poseStack.pushPose()

        if (direction === Direction.NORTH) {
            poseStack.translate(0.15, 0.031, 0.025)
        } else if (direction === Direction.SOUTH) {
            poseStack.translate(0.15, 0.031, 0.275)
        } else if (direction === Direction.WEST) {
            poseStack.translate(0.025, 0.031, 0.15)
        } else if (direction === Direction.EAST) {
            poseStack.translate(0.275, 0.031, 0.15)
        }

        val waterHeight = 3.0f / 16.0f

        poseStack.scale(0.7f, waterHeight, 0.7f)
        blockRenderer.renderSingleBlock(Blocks.BLUE_STAINED_GLASS.defaultBlockState(), poseStack, bufferSource, packedLight, packedOverlay)

        poseStack.translate(0.15, 0.3, 0.15)
        poseStack.scale(0.7f, 0.49.toFloat() / waterHeight, 0.7f)

        poseStack.translate(0.5, 0.7, 0.5)



        poseStack.mulPose(Quaternionf(cos(Math.PI / -4), 0.0, sin(Math.PI / -4), 0.0))
        poseStack.mulPose(Quaternionf(cos(rotation / -2), 0.0, sin(rotation / -2), 0.0))

        val radiansPerEntity: Double = 1.25 * Math.toRadians((worldTime * 3).toDouble()) + 2 * Math.PI * blockEntity.randomRotation
        poseStack.translate(0.0, sin(radiansPerEntity) / 48, 0.0)
        val rotationValue = (sin(Math.PI / 2 + radiansPerEntity) / 64)
        poseStack.mulPose(Quaternionf(cos(rotationValue), sin(rotationValue), 0.0, 0.0))

        itemRenderer.renderStatic(ItemStack(Items.IRON_AXE), ItemDisplayContext.GUI, packedLight, packedOverlay, poseStack, bufferSource, level, 1)
        
        poseStack.popPose()
    }
}