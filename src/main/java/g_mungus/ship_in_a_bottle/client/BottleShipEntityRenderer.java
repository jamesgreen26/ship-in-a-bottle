package g_mungus.ship_in_a_bottle.client;

import com.mojang.brigadier.StringReader;
import g_mungus.ship_in_a_bottle.block.ModBlocks;
import g_mungus.ship_in_a_bottle.block.entity.BottleWithShipEntity;
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
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Quaternionf;

import java.util.Objects;

import static java.lang.Math.*;
import static net.minecraft.command.argument.BlockArgumentParser.INVALID_BLOCK_ID_EXCEPTION;


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
        matrices.multiply(new Quaternionf(1, 0, 0, 0));


        //itemRenderer.renderItem(new ItemStack(ModItems.SHIPMODEL), ModelTransformationMode.GUI, light, overlay, matrices, vertexConsumers, MinecraftClient.getInstance().world, 1);


        try {
            StringReader reader = new StringReader(ShipInABottleClient.shipDisplayData.get(((BottleWithShipEntity) entity).getShipName()).get(0).id);
            Identifier id = Identifier.fromCommandInput(reader);
            Block block = (Block)((RegistryEntry.Reference<?>) Registries.BLOCK.getReadOnlyWrapper().getOptional(RegistryKey.of(RegistryKeys.BLOCK, id)).orElseThrow(() -> INVALID_BLOCK_ID_EXCEPTION.createWithContext(reader, id.toString()))).value();
            blockRenderManager.renderBlockAsEntity(block.getDefaultState(),  matrices, vertexConsumers, light, overlay);
        } catch (Exception e) {}

        matrices.pop();
    }
}
