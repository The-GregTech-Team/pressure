/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks

import cofh.core.util.RayTracer
import cofh.core.util.helpers.{ServerHelper, WrenchHelper}
import net.bdew.lib.block.HasTE
import net.bdew.lib.rotate.{BaseRotatableBlock, Properties}
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World

trait BlockFilterableRotatable extends BlockFilterable with BaseRotatableBlock {
  this: HasTE[_ <: TileFilterable] =>
  override def onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean ={
    val traceResult = RayTracer.retrace(player)
    if (traceResult == null) return false

    val tile = world.getTileEntity(pos)
    if (tile == null || tile.isInvalid) return false

    if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
      if (ServerHelper.isServerWorld(world)) rotateBlock(world, pos, state, side)
      WrenchHelper.usedWrench(player, traceResult)
      return true
    }

    super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ)
  }

  def rotateBlock(world: World, pos: BlockPos, state: IBlockState, side: EnumFacing): Unit = {
    val facing = state.getValue(Properties.FACING)
    setFacing(world, pos, facing.rotateAround(side.getAxis))
  }
}
