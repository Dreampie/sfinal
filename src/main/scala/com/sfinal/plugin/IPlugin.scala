package com.sfinal.plugin

/**
 * Created by ice on 14-12-3.
 */
trait IPlugin {
  def start: Boolean

  def stop: Boolean
}
