/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.pressurenet

import net.bdew.pressure.Pressure
import net.bdew.pressure.api.{IPressureConnection, IPressureInject}
import net.bdew.pressure.misc.FluidUtils
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

case class PressureConnection(origin: IPressureInject, side: EnumFacing, tiles: Set[PressureOutputFace]) extends IPressureConnection {
  override def pushFluid(fluid: FluidStack, doPush: Boolean): Int = {
    if (fluid == null || fluid.getFluid == null || fluid.amount == 0 || tiles.isEmpty) return 0

    // Here we check that this connection hasn't been already visited
    // and if a loop is detected we blow up the block.

    if (Helper.recursionGuard.value.contains(this)) {
      Pressure.logInfo("Detected loop, blowing up %s (dim %d)", origin.pressureNodePos, origin.pressureNodeWorld.provider.getDimension)
      origin.pressureNodeWorld.setBlockToAir(origin.pressureNodePos)
      origin.pressureNodeWorld.createExplosion(null, origin.pressureNodePos.getX, origin.pressureNodePos.getY, origin.pressureNodePos.getZ, 1, true)
      return 0
    }

    Helper.recursionGuard.withValue(Helper.recursionGuard.value + this) {
      // Now the inner part will see this block as part of the path
      // The set is reset back to the old value automatically once execution leaves this block

      if (fluid.amount < 10) {
        // Don't try balancing small amounts
        var toPush = fluid.amount
        tiles.foreach { target =>
          toPush -= target.eject(FluidUtils.copyStackWithAmount(fluid, toPush), doPush)
          if (toPush <= 0) return fluid.amount
        }
        fluid.amount - toPush
      } else {
        val maxFill = tiles.map(target => target -> target.eject(fluid.copy(), false)).toMap
        val totalFill = maxFill.values.sum
        val mul = if (totalFill > fluid.amount) fluid.amount.toFloat / totalFill else 1
        val filled = maxFill map { case (te, amount) =>
          val toFill = (amount * mul).floor.toInt
          if (toFill > 0)
            te.eject(FluidUtils.copyStackWithAmount(fluid, toFill), doPush)
          else
            0
        }
        filled.sum
      }
    }
  }

}
