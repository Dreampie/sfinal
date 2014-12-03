package com.sfinal.config

import com.sfinal.plugin.IPlugin

/**
 * Created by ice on 14-12-3.
 */
final class Plugins {
  val pluginList: List[IPlugin] = Nil

  def add(plugin: IPlugin): Plugins = {
    if (plugin != null) plugin :: this.pluginList
    return this
  }

}
