/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure.blocks.router.gui

import net.bdew.lib.gui.SlotClickable
import net.bdew.pressure.blocks.router.data.RouterSideMode
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{ClickType, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing

class SlotMode(container: ContainerRouter, index: Int, x: Int, y: Int) extends Slot(container.inventory, index, x, y) with SlotClickable {
  val dir = EnumFacing.byIndex(index)

  override def onClick(clickType: ClickType, button: Int, player: EntityPlayer): ItemStack = {
    val stack = player.inventory.getItemStack
    if (!container.te.getWorld.isRemote && clickType == ClickType.PICKUP) {
      if (button == 0)
        container.te.sideModes.set(dir, RouterSideMode.order(container.te.sideModes.get(dir)))
      else if (button == 1)
        container.te.sideModes.set(dir, RouterSideMode.DISABLED)
    }
    stack
  }
}
