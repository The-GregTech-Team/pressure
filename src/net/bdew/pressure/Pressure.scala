/*
 * Copyright (c) bdew, 2013 - 2017
 * https://github.com/bdew/pressure
 *
 * This mod is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://bdew.net/minecraft-mod-public-license/
 */

package net.bdew.pressure

import java.io.File

import net.bdew.lib.Misc
import net.bdew.pressure.api.PressureAPI
import net.bdew.pressure.compat.enderio.EnderIOProxy
import net.bdew.pressure.compat.opencomputers.OCBlocks
import net.bdew.pressure.config._
import net.bdew.pressure.misc.PressureCreativeTabs
import net.bdew.pressure.network.NetworkHandler
import net.bdew.pressure.pressurenet.Helper
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.Logger

@Mod(modid = Pressure.modId, version = "PRESSURE_VER", name = "Pressure Pipes", dependencies = "required-after:bdlib; required-after:cofhcore", modLanguage = "scala", acceptedMinecraftVersions = "[1.12,1.12.2]")
object Pressure {
  var log: Logger = null
  var instance = this

  final val modId = "pressure"
  final val channel = "bdew.pressure"

  var configDir: File = null

  def logInfo(msg: String, args: Any*) = log.info(msg.format(args: _*))
  def logWarn(msg: String, args: Any*) = log.warn(msg.format(args: _*))
  def logError(msg: String, args: Any*) = log.error(msg.format(args: _*))
  def logWarnException(msg: String, t: Throwable, args: Any*) = log.warn(msg.format(args: _*), t)
  def logErrorException(msg: String, t: Throwable, args: Any*) = log.error(msg.format(args: _*), t)

  @EventHandler
  def preInit(event: FMLPreInitializationEvent) {
    log = event.getModLog
    PressureAPI.HELPER = Helper
    configDir = new File(event.getModConfigurationDirectory, "PressurePipes")
    TuningLoader.loadConfigFiles()
    Items.load()
    Blocks.load()
    Machines.load()
    if (event.getSide == Side.CLIENT) PressureClient.preInit()
  }

  @EventHandler
  def init(event: FMLInitializationEvent) {
    if (event.getSide.isClient) Config.load(new File(configDir, "client.config"))
    NetworkRegistry.INSTANCE.registerGuiHandler(this, Config.guiHandler)
    TuningLoader.loadDelayed()
    FMLInterModComms.sendMessage("waila", "register", "net.bdew.pressure.waila.WailaHandler.loadCallback")
    if (Misc.haveModVersion("opencomputers"))
      OCBlocks.init()
    NetworkHandler.init()
  }

  @EventHandler
  def postInit(event: FMLPostInitializationEvent) {
    PressureCreativeTabs.init()
    if (Misc.haveModVersion("EnderIO"))
      EnderIOProxy.init()
  }
}
