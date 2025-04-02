package g_mungus.ship_in_a_bottle.mixin;

import g_mungus.ship_in_a_bottle.util.BlockInfoListProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StructureTemplate.class)
public class StructureTemplateMixin implements BlockInfoListProvider {

	@Final
	@Shadow
	private List<StructureTemplate.Palette> palettes;

	@Override
	public List<StructureTemplate.Palette> ship_in_a_bottle$getBlockInfoList() {
		return palettes;
	}
}