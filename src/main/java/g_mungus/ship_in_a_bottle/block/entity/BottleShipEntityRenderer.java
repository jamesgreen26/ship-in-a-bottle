package g_mungus.ship_in_a_bottle.block.entity;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class BottleShipEntityRenderer implements BlockEntityRenderer<BottleWithShipEntity> {

    public BottleShipEntityRenderer(BlockEntityRendererFactory.Context context){}

    @Override
    public void render(BottleWithShipEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

        BlockState state = entity.getCachedState();
        Direction direction = state.get(Properties.FACING);

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

        float waterHeight = 2.0F / 16.0F;

        matrices.scale(0.7F, waterHeight, 0.7F);

        blockRenderManager.renderBlockAsEntity(ModBlocks.WATERBLOCK.getDefaultState(),  matrices, vertexConsumers, light, overlay);

        matrices.translate(0.15, 0.08, 0.15);
        matrices.scale(0.7F, (float) 0.49 / waterHeight, 0.7F);


        blockRenderManager.renderBlockAsEntity(Blocks.OAK_PLANKS.getDefaultState(),  matrices, vertexConsumers, light, overlay);


        matrices.pop();
    }
}
