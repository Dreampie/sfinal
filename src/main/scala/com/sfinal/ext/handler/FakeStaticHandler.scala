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
package com.sfinal.ext.handler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.handler.Handler
import com.sfinal.handler.Handler
import com.sfinal.kit.HandlerKit
import com.sfinal.kit.HandlerKit
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit

/**
 * FakeStaticHandler.
 */
class FakeStaticHandler extends Handler {
  def this() {
    this()
    viewPostfix = ".html"
  }

  def this(viewPostfix: String) {
    this()
    if (StrKit.isBlank(viewPostfix)) throw new IllegalArgumentException("viewPostfix can not be blank.")
    this.viewPostfix = viewPostfix
  }

  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    if ("/" == target) {
      nextHandler.handle(target, request, response, isHandled)
      return
    }
    if (target.indexOf('.') == -1) {
      HandlerKit.renderError404(request, response, isHandled)
      return
    }
    val index: Int = target.lastIndexOf(viewPostfix)
    if (index != -1) target = target.substring(0, index)
    nextHandler.handle(target, request, response, isHandled)
  }

  private var viewPostfix: String = null
}

