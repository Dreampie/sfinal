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
package com.sfinal.render

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.core.Const

/**
 * Render.
 */
object Render {
  private[render] final def init(encoding: String, devMode: Boolean) {
    Render.encoding = encoding
    Render.devMode = devMode
  }

  final def getEncoding: String = {
    return encoding
  }

  final def getDevMode: Boolean = {
    return devMode
  }

  private var encoding: String = Const.DEFAULT_ENCODING
  private var devMode: Boolean = Const.DEFAULT_DEV_MODE
}

abstract class Render {
  final def setContext(request: HttpServletRequest, response: HttpServletResponse): Render = {
    this.request = request
    this.response = response
    return this
  }

  final def setContext(request: HttpServletRequest, response: HttpServletResponse, viewPath: String): Render = {
    this.request = request
    this.response = response
    if (view != null && !view.startsWith("/")) view = viewPath + view
    return this
  }

  def getView: String = {
    return view
  }

  def setView(view: String) {
    this.view = view
  }

  /**
   * Render to client
   */
  def render

  protected var view: String = null
  protected var request: HttpServletRequest = null
  protected var response: HttpServletResponse = null
}

