package g_mungus.ship_in_a_bottle.block.entity;

import g_mungus.ship_in_a_bottle.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BottleWithShipEntity extends BlockEntity {

    public BottleWithShipEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.BOTTLEWITHSHIPENTITY, pos, state);
    }
}
