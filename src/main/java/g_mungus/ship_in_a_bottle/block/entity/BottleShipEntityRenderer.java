package g_mungus.ship_in_a_bottle.block.entity;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;

public class BottleShipEntityRenderer implements BlockEntityRenderer<BottleWithShipEntity> {

    public BottleShipEntityRenderer(BlockEntityRendererFactory.Context context){}

    @Override
    public void render(BottleWithShipEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        BlockState state = entity.getCachedState();
        Direction direction = state.get(FurnaceBlock.FACING);
        double rotation = Math.toRadians(direction.asRotation());

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


        //matrices.multiply(new Quaternionf(0, 0, 1, 0));
        matrices.multiply(new Quaternionf(Math.cos(Math.PI/-4), 0, Math.sin(Math.PI/-4), 0));
        matrices.multiply(new Quaternionf(Math.cos(rotation/-2), 0, Math.sin(rotation/-2), 0));

        itemRenderer.renderItem(new ItemStack(ModItems.SHIPMODEL), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 1);


        matrices.pop();
    }
}
