package g_mungus.ship_in_a_bottle.client;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.block.entity.*;
import g_mungus.ship_in_a_bottle.util.DisplayableShipData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;

import java.util.Objects;

import static java.lang.Math.*;


public class BottleShipEntityRenderer implements BlockEntityRenderer<BottleWithShipEntity> {

    public BottleShipEntityRenderer(BlockEntityRendererFactory.Context context){}



    @Override
    public void render(BottleWithShipEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        BlockState state = entity.getCachedState();
        Direction direction = state.get(FurnaceBlock.FACING);
        double rotation = Math.toRadians(direction.asRotation());

        float worldTime = Objects.requireNonNull(entity.getWorld()).getTime();



        matrices.push();



        if (direction == Direction.NORTH) {
            matrices.translate(0.15, 0.031, 0.025);
        } else if (direction == Direction.SOUTH) {
            matrices.translate(0.15, 0.031, 0.275);
        } else if (direction == Direction.WEST) {
            matrices.translate(0.025, 0.031, 0.15);
        } else if (direction == Direction.EAST){
            matrices.translate(0.275, 0.031, 0.15);
        }

        float waterHeight = 3.0F / 16.0F;

        matrices.scale(0.7F, waterHeight, 0.7F);

        blockRenderManager.renderBlockAsEntity(ModBlocks.WATERBLOCK.getDefaultState(),  matrices, vertexConsumers, light, overlay);



        matrices.translate(0.15, 0.3, 0.15);
        matrices.scale(0.7F, (float) 0.49 / waterHeight, 0.7F);

        matrices.translate(0.5, 0.7, 0.5);



        matrices.multiply(new Quaternionf(cos(Math.PI/-4), 0, sin(Math.PI/-4), 0));
        matrices.multiply(new Quaternionf(cos(rotation/-2), 0, sin(rotation/-2), 0));

        double radiansPerEntity = 1.25 * toRadians(worldTime * 3) + 2*PI*entity.randomRotation;
        matrices.translate(0,sin(radiansPerEntity)/48,0);
        double rotationValue = (sin(PI/2 + radiansPerEntity) / 64);
        matrices.multiply(new Quaternionf(cos(rotationValue), sin(rotationValue), 0, 0));

        //itemRenderer.renderItem(new ItemStack(ModItems.SHIPMODEL), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 1);

        try {
            DisplayableShipData data = ShipInABottleClient.shipDisplayData.get((entity.getShipName()));
            if (data != null) {

                if (data.sizeX >= data.sizeZ) {
                    matrices.multiply(new Quaternionf(1, 0, 0, 0));
                } else {
                    matrices.multiply(new Quaternionf(sqrt(2)/2, 0f, sqrt(2)/2, 0));
                }

                int largestLength = max(data.sizeX, max(data.sizeY, data.sizeZ));
                matrices.scale((float) (1.0 / largestLength), (float) (1.0 / largestLength), (float) (1.0 / largestLength));


                matrices.translate(data.sizeX / -2.0, data.sizeY / -2.0, data.sizeZ / -2.0);
                matrices.translate(0, data.sizeY / -12.0, 0);

                for (DisplayableShipData.BlockInfo entry : data.data) {

                    matrices.translate(entry.x, entry.y, entry.z);
                    blockRenderManager.renderBlockAsEntity(Block.getStateFromRawId(entry.stateId), matrices, vertexConsumers, light, overlay);
                    matrices.translate(-entry.x, -entry.y, -entry.z);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        matrices.pop();
    }
}
