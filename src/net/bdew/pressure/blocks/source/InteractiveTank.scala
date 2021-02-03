package net.bdew.pressure.blocks.source

import cofh.core.util.helpers.FluidHelper
import net.bdew.lib.block.BaseBlock
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.minecraftforge.fluids.capability.CapabilityFluidHandler

trait InteractiveTank extends BaseBlock {
  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ)
    val tile = world.getTileEntity(pos)
    if (tile == null) return false
    val heldItem = player.getHeldItem(hand)
    val handler = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
    if (FluidHelper.isFluidHandler(heldItem)) {
      FluidHelper.interactWithHandler(heldItem, handler, player, hand)
      return true
    }
    false
  }
}
