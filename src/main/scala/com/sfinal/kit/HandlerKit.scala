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
package com.sfinal.kit

import java.io.IOException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.render.RenderException
import com.sfinal.render.RenderException
import com.sfinal.render.RenderFactory
import com.sfinal.render.RenderFactory

/**
 * HandlerKit.
 */
object HandlerKit {
  def renderError404(view: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    isHandled(0) = true
    response.setStatus(HttpServletResponse.SC_NOT_FOUND)
    RenderFactory.me.getRender(view).setContext(request, response).render
  }

  def renderError404(request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    isHandled(0) = true
    RenderFactory.me.getErrorRender(404).setContext(request, response).render
  }

  def redirect301(url: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    isHandled(0) = true
    val queryString: String = request.getQueryString
    if (queryString != null) url += "?" + queryString
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY)
    response.setHeader("Location", url)
    response.setHeader("Connection", "close")
  }

  def redirect(url: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    isHandled(0) = true
    val queryString: String = request.getQueryString
    if (queryString != null) url = url + "?" + queryString
    try {
      response.sendRedirect(url)
    }
    catch {
      case e: IOException => {
        throw new RenderException(e)
      }
    }
  }
}

class HandlerKit {
  private def this() {
    this()
  }
}

