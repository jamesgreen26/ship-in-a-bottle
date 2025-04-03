package g_mungus.ship_in_a_bottle.structure

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import kotlin.jvm.optionals.getOrNull

class StructurePlacer(
    private val level: WorldGenLevel,
    private val templateName: ResourceLocation,
    private val blockPos: BlockPos
) {
    var rotation = Rotation.NONE
    var ignoreEntities = true
    var offset = BlockPos(0, 0, 0)

    private val template: StructureTemplate? = level.server?.structureManager?.get(templateName)?.getOrNull()

    val size get() = template?.size?: Vec3i(0, 0, 0)

    fun place(): Boolean {
        template?.let { temp ->
            val structurePlacementData = StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(rotation)
                .setIgnoreEntities(ignoreEntities)

            val pos: BlockPos = blockPos.offset(offset)

            temp.placeInWorld(level, pos, pos, structurePlacementData, RandomSource.create(level.seed), 2)

            level.server?.structureManager?.remove(templateName)
            return true
        }?: return false
    }
}