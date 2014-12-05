/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sfinal.core

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.config.Constants
import com.sfinal.core._
import com.sfinal.handler.Handler
import com.sfinal.handler.Handler
import com.sfinal.log.Logger
import com.sfinal.render.Render
import com.sfinal.render.Render
import com.sfinal.render.RenderException
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory

/**
 * ActionHandler
 */
object ActionHandler {
  private val renderFactory: RenderFactory = RenderFactory.me
  private val log: Logger = Logger.logger(classOf[ActionHandler])
}

class ActionHandler extends Handler {
  private val renderFactory: RenderFactory = ActionHandler.renderFactory
  private val log: Logger = ActionHandler.log
  private var devMode: Boolean = false
  private var actionMapping: ActionMapping = null

  def this(actionMapping: ActionMapping, constants: Constants) {
    this()
    this.actionMapping = actionMapping
    this.devMode = constants.getDevMode
  }

  /**
   * handle
   * 1: Action action = actionMapping.getAction(target)
   * 2: new ActionInvocation(...).invoke()
   * 3: render(...)
   */
  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    if (target.indexOf('.') != -1) {
      return
    }
    isHandled(0) = true
    val urlPara: Array[String] = Array(null)
    val action: Action = actionMapping.getAction(target, urlPara)
    if (action == null) {
      if (ActionHandler.log.isWarnEnabled) {
        val qs: String = request.getQueryString
        ActionHandler.log.warn("404 Action Not Found: " + (if (qs == null) target else target + "?" + qs))
      }
      renderFactory.getErrorRender(404).setContext(request, response).render
      return
    }
    try {
      val controller: Controller = action.getControllerClass.newInstance
      controller.init(request, response, urlPara(0))
      if (devMode) {
        val isMultipartRequest: Boolean = ActionReporter.reportCommonRequest(controller, action)
        new ActionInvocation(action, controller).invoke
        if (isMultipartRequest) ActionReporter.reportMultipartRequest(controller, action)
      }
      else {
        new ActionInvocation(action, controller).invoke
      }
      var render: Render = controller.getRender
      render match {
        case actionRender: ActionRender =>
          val actionUrl: String = actionRender.getActionUrl
          if (target == actionUrl) throw new RuntimeException("The forward action url is the same as before.")
          else handle(actionUrl, request, response, isHandled)
          return
        case _ =>
      }
      if (render == null) render = renderFactory.getDefaultRender(action.getViewPath + action.getMethodName)
      render.setContext(request, response, action.getViewPath).render
    }
    catch {
      case e: RenderException => {
        if (log.isErrorEnabled) {
          val qs: String = request.getQueryString
          log.error(if (qs == null) target else target + "?" + qs, e)
        }
      }
      case e: ActionException => {
        val errorCode: Int = e.getErrorCode
        if (errorCode == 404 && log.isWarnEnabled) {
          val qs: String = request.getQueryString
          log.warn("404 Not Found: " + (if (qs == null) target else target + "?" + qs))
        }
        else if (errorCode == 401 && log.isWarnEnabled) {
          val qs: String = request.getQueryString
          log.warn("401 Unauthorized: " + (if (qs == null) target else target + "?" + qs))
        }
        else if (errorCode == 403 && log.isWarnEnabled) {
          val qs: String = request.getQueryString
          log.warn("403 Forbidden: " + (if (qs == null) target else target + "?" + qs))
        }
        else if (log.isErrorEnabled) {
          val qs: String = request.getQueryString
          log.error(if (qs == null) target else target + "?" + qs, e)
        }
        e.getErrorRender.setContext(request, response).render
      }
      case t: Throwable => {
        if (log.isErrorEnabled) {
          val qs: String = request.getQueryString
          log.error(if (qs == null) target else target + "?" + qs, t)
        }
        renderFactory.getErrorRender(500).setContext(request, response).render
      }
    }
  }

}






