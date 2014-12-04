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

import java.io.IOException
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.config.Constants
import com.sfinal.config.JFinalConfig
import com.sfinal.handler.Handler
import com.sfinal.handler.Handler
import com.sfinal.log.Logger
import com.sfinal.log.Logger

/**
 * JFinal framework filter
 */
final object JFinalFilter {
  private[core] def initLogger {
    log = Logger.getLogger(classOf[JFinalFilter])
  }

  private final val jfinal: JFinal = JFinal.me
  private var log: Logger = null
}

final class JFinalFilter extends Filter {
  def init(filterConfig: FilterConfig) {
    createJFinalConfig(filterConfig.getInitParameter("configClass"))
    if (jfinal.init(jfinalConfig, filterConfig.getServletContext) == false) throw new RuntimeException("JFinal init error!")
    handler = jfinal.getHandler
    constants = Config.getConstants
    encoding = constants.getEncoding
    jfinalConfig.afterJFinalStart
    val contextPath: String = filterConfig.getServletContext.getContextPath
    contextPathLength = (if (contextPath == null || ("/" == contextPath)) 0 else contextPath.length)
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val request: HttpServletRequest = req.asInstanceOf[HttpServletRequest]
    val response: HttpServletResponse = res.asInstanceOf[HttpServletResponse]
    request.setCharacterEncoding(encoding)
    var target: String = request.getRequestURI
    if (contextPathLength != 0) target = target.substring(contextPathLength)
    val isHandled: Array[Boolean] = Array(false)
    try {
      handler.handle(target, request, response, isHandled)
    }
    catch {
      case e: Exception => {
        if (log.isErrorEnabled) {
          val qs: String = request.getQueryString
          log.error(if (qs == null) target else target + "?" + qs, e)
        }
      }
    }
    if (isHandled(0) == false) chain.doFilter(request, response)
  }

  def destroy {
    jfinalConfig.beforeJFinalStop
    jfinal.stopPlugins
  }

  private def createJFinalConfig(configClass: String) {
    if (configClass == null) throw new RuntimeException("Please set configClass parameter of JFinalFilter in web.xml")
    var temp: AnyRef = null
    try {
      temp = Class.forName(configClass).newInstance
    }
    catch {
      case e: Exception => {
        throw new RuntimeException("Can not create instance of class: " + configClass, e)
      }
    }
    if (temp.isInstanceOf[JFinalConfig]) jfinalConfig = temp.asInstanceOf[JFinalConfig]
    else throw new RuntimeException("Can not create instance of class: " + configClass + ". Please check the config in web.xml")
  }

  private var handler: Handler = null
  private var encoding: String = null
  private var jfinalConfig: JFinalConfig = null
  private var constants: Constants = null
  private var contextPathLength: Int = 0
}

