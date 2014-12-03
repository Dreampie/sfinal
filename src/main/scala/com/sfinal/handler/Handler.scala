package com.sfinal.handler

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

/**
 * Created by wangrenhui on 14/12/3.
 */
trait Handler {
  var nextHandler: Handler = null

  /**
   * Handle target
   * @param target url target of this web http request
   * @param request HttpServletRequest of this http request
   * @param response HttpServletRequest of this http request
   * @param isHandled SFinalFilter will invoke doFilter() method if isHandled[0] == false,
   *                  it is usually to tell Filter should handle the static resource.
   */
  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean])
}


/**
 * HandlerFactory.
 */
object HandlerFactory {
  /**
   * Build handler chain
   */
  def getHandler(handlerList: List[Handler], actionHandler: Handler): Handler = {
    var result: Handler = actionHandler

    handlerList.reverse.map(
      handler => {
        handler.nextHandler = result
        result = handler
      })
    result
  }
}
