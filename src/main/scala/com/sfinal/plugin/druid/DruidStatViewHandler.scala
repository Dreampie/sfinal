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
package com.sfinal.plugin.druid

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.alibaba.druid.support.http.StatViewServlet
import com.sfinal.handler.Handler
import com.sfinal.kit.HandlerKit
import com.sfinal.kit.StrKit
import com.sfinal.plugin.druid.IDruidStatViewAuth

/**
 * 替代 StatViewServlet
 */
class DruidStatViewHandler extends Handler {
  def this(visitPath: String) {
    this()
    `this`(visitPath, new IDruidStatViewAuth {
      def isPermitted(request: HttpServletRequest): Boolean = {
        return true
      }
    })
  }

  def this(visitPath: String, druidStatViewAuth: IDruidStatViewAuth) {
    this()
    if (StrKit.isBlank(visitPath)) throw new IllegalArgumentException("visitPath can not be blank")
    if (druidStatViewAuth == null) throw new IllegalArgumentException("druidStatViewAuth can not be null")
    visitPath = visitPath.trim
    if (!visitPath.startsWith("/")) visitPath = "/" + visitPath
    this.visitPath = visitPath
    this.auth = druidStatViewAuth
  }

  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    if (target.startsWith(visitPath)) {
      isHandled(0) = true
      if ((target == visitPath) && !target.endsWith("/index.html")) {
        HandlerKit.redirect(target += "/index.html", request, response, isHandled)
        return
      }
      try {
        servlet.service(request, response)
      }
      catch {
        case e: Exception => {
          throw new RuntimeException(e)
        }
      }
    }
    else {
      nextHandler.handle(target, request, response, isHandled)
    }
  }

  private var auth: IDruidStatViewAuth = null
  private var visitPath: String = "/druid"
  private var servlet: StatViewServlet = new DruidStatViewHandler#JFinalStatViewServlet

  private[druid] object JFinalStatViewServlet {
    private final val serialVersionUID: Long = 2898674199964021798L
  }

  private[druid] class JFinalStatViewServlet extends StatViewServlet {
    override def isPermittedRequest(request: HttpServletRequest): Boolean = {
      return auth.isPermitted(request)
    }

    override def service(request: HttpServletRequest, response: HttpServletResponse) {
      var contextPath: String = request.getContextPath
      val requestURI: String = request.getRequestURI
      response.setCharacterEncoding("utf-8")
      if (contextPath == null) {
        contextPath = ""
      }
      val index: Int = contextPath.length + visitPath.length
      val uri: String = requestURI.substring(0, index)
      var path: String = requestURI.substring(index)
      if (!isPermittedRequest(request)) {
        path = "/nopermit.html"
        returnResourceFile(path, uri, response)
        return
      }
      if ("/submitLogin" == path) {
        val usernameParam: String = request.getParameter(PARAM_NAME_USERNAME)
        val passwordParam: String = request.getParameter(PARAM_NAME_PASSWORD)
        if ((username == usernameParam) && (password == passwordParam)) {
          request.getSession.setAttribute(SESSION_USER_KEY, username)
          response.getWriter.print("success")
        }
        else {
          response.getWriter.print("error")
        }
        return
      }
      if (isRequireAuth && !ContainsUser(request) && !(("/login.html" == path) || path.startsWith("/css") || path.startsWith("/js") || path.startsWith("/img"))) {
        if (contextPath == null || (contextPath == "") || (contextPath == "/")) {
          response.sendRedirect("/druid/login.html")
        }
        else {
          if ("" == path) {
            response.sendRedirect("druid/login.html")
          }
          else {
            response.sendRedirect("login.html")
          }
        }
        return
      }
      if ("" == path) {
        if (contextPath == null || (contextPath == "") || (contextPath == "/")) {
          response.sendRedirect("/druid/index.html")
        }
        else {
          response.sendRedirect("druid/index.html")
        }
        return
      }
      if ("/" == path) {
        response.sendRedirect("index.html")
        return
      }
      if (path.indexOf(".json") >= 0) {
        var fullUrl: String = path
        if (request.getQueryString != null && request.getQueryString.length > 0) {
          fullUrl += "?" + request.getQueryString
        }
        response.getWriter.print(process(fullUrl))
        return
      }
      returnResourceFile(path, uri, response)
    }
  }

}





