package com.sfinal.handler

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.jfinal.handler.Handler

/**
 * Created by wangrenhui on 14/12/3.
 */
trait Handler {
  protected var nextHandler: Handler = null

  /**
   * Handle target
   * @param target url target of this web http request
   * @param request HttpServletRequest of this http request
   * @param response HttpServletRequest of this http request
   * @param isHandled JFinalFilter will invoke doFilter() method if isHandled[0] == false,
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
    var result: Handler = actionHandler {
      var i: Int = handlerList.size - 1
      while (i >= 0) {
        {
          val temp: Handler = handlerList.get(i)
          temp.nextHandler = result
          result = temp
        }
        ({
          i -= 1;
          i + 1
        })
      }
    }
    return result
  }
}

class HandlerFactory {
}
