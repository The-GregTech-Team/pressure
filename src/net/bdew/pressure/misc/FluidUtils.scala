/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.misc

import net.minecraftforge.fluids.FluidStack

object FluidUtils {
  def copyStackWithAmount(fs: FluidStack, amount: Int): FluidStack = {
    val tmp = fs.copy()
    fs.amount = amount
    fs
  }
}
