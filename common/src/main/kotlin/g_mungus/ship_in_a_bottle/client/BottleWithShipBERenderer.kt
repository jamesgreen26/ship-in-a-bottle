package g_mungus.ship_in_a_bottle.client

import com.mojang.blaze3d.vertex.PoseStack
import g_mungus.ship_in_a_bottle.ShipInABottle
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipBlockEntity
import g_mungus.ship_in_a_bottle.config.RenderQuality
import g_mungus.ship_in_a_bottle.config.ShipInABottleConfig
import g_mungus.ship_in_a_bottle.networking.DisplayableShipData
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.core.Direction
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Quaternionf
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

class BottleWithShipBERenderer : BlockEntityRenderer<BottleWithShipBlockEntity> {
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

        val level = blockEntity.level ?: return

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
        blockRenderer.renderSingleBlock(
            ShipInABottle.WATER_DISPLAY_BLOCK.defaultBlockState(),
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay
        )

        poseStack.translate(0.15, 0.3, 0.15)
        poseStack.scale(0.7f, 0.49.toFloat() / waterHeight, 0.7f)

        poseStack.translate(0.5, 0.7, 0.5)



        poseStack.mulPose(Quaternionf(cos(Math.PI / -4), 0.0, sin(Math.PI / -4), 0.0))
        poseStack.mulPose(Quaternionf(cos(rotation / -2), 0.0, sin(rotation / -2), 0.0))

        val radiansPerEntity: Double =
            1.25 * Math.toRadians((worldTime * 3).toDouble()) + 2 * Math.PI * blockEntity.randomRotation
        poseStack.translate(0.0, sin(radiansPerEntity) / 48, 0.0)
        val rotationValue = (sin(Math.PI / 2 + radiansPerEntity) / 64)
        poseStack.mulPose(Quaternionf(cos(rotationValue), sin(rotationValue), 0.0, 0.0))


        val data = ShipInABottleClient.shipDisplayData[blockEntity.getShipName()]

        if (blockEntity.getShipName().isBlank() || data == null || ShipInABottleConfig.Client.renderQuality == RenderQuality.Fast) {
            renderSimpleShip(poseStack, itemRenderer, packedLight, packedOverlay, bufferSource, level)
        } else {
            renderAuthenticShip(data, poseStack, blockRenderer, bufferSource, packedLight, packedOverlay)
        }

        poseStack.popPose()
    }

    private fun renderAuthenticShip(
        data: DisplayableShipData,
        poseStack: PoseStack,
        blockRenderer: BlockRenderDispatcher,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        if (data.sizeX >= data.sizeZ) {
            poseStack.mulPose(Quaternionf(1f, 0f, 0f, 0f))
        } else {
            poseStack.mulPose(Quaternionf(sqrt(2.0) / 2, 0.0, sqrt(2.0) / 2, 0.0))
        }

        val largestLength =
            max(data.sizeX.toDouble(), max(data.sizeY.toDouble(), data.sizeZ.toDouble())).toInt()
        poseStack.scale(
            (1.0 / largestLength).toFloat(),
            (1.0 / largestLength).toFloat(),
            (1.0 / largestLength).toFloat()
        )


        poseStack.translate(data.sizeX / -2.0, data.sizeY / -2.0, data.sizeZ / -2.0)
        poseStack.translate(0.0, data.sizeY / -12.0, 0.0)

        for (entry in data.data) {
            poseStack.translate(entry.x.toDouble(), entry.y.toDouble(), entry.z.toDouble())
            blockRenderer.renderSingleBlock(
                Block.stateById(entry.stateId),
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
            )
            poseStack.translate(-entry.x.toDouble(), -entry.y.toDouble(), -entry.z.toDouble())
        }
    }

    private fun renderSimpleShip(
        poseStack: PoseStack,
        itemRenderer: ItemRenderer,
        packedLight: Int,
        packedOverlay: Int,
        bufferSource: MultiBufferSource,
        level: Level
    ) {
        poseStack.mulPose(Quaternionf(1f, 0f, 0f, 0f))
        itemRenderer.renderStatic(
            ItemStack(ShipInABottle.SHIP_MODEL_ITEM),
            ItemDisplayContext.GUI,
            packedLight,
            packedOverlay,
            poseStack,
            bufferSource,
            level,
            1
        )
    }
}