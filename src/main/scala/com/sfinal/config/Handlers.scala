package com.sfinal.config

import com.sfinal.handler.Handler

/**
 * Created by ice on 14-12-3.
 */
final class Handlers {
  private val handlerList: List[Handler] = List[Handler]()

  def add(handler: Handler): Handlers = {
    if (handler != null) handler :: handlerList
    return this
  }

  def getHandlerList: List[Handler] = {
    return handlerList
  }


}
