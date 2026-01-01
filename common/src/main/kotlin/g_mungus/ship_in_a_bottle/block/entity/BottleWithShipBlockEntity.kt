package g_mungus.ship_in_a_bottle.block.entity

import g_mungus.ship_in_a_bottle.ShipInABottle
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import kotlin.random.Random

class BottleWithShipBlockEntity(pos: BlockPos, state: BlockState, private var shipName: String) :
    BlockEntity(ShipInABottle.BOTTLE_WITH_SHIP_BE_TYPE, pos, state) {

    val randomRotation: Double = Random.nextDouble()
    fun getShipName() =  shipName

    init {
        this.setChanged()
    }

    fun getItemStack() = ItemStack(ShipInABottle.BOTTLE_WITH_SHIP_ITEM).apply {
        tag = orCreateTag.apply{putString("Ship", shipName)}
    }

    override fun load(tag: CompoundTag) {
        super.load(tag)
        this.shipName = tag.getString("shipName")

    }

    override fun saveAdditional(tag: CompoundTag) {
        super.saveAdditional(tag)
        tag.putString("shipName", this.shipName)

    }

    override fun getUpdateTag(): CompoundTag {
        return saveWithoutMetadata()
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    companion object {
        const val NO_SHIP = ""
    }
}