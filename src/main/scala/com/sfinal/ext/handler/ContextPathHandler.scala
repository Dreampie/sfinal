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
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit

/**
 * Provide a context path to view if you need.
 * <br>
 * Example:<br>
 * In JFinalFilter: handlers.add(new ContextPathHandler("CONTEXT_PATH"));<br>
 * in freemarker: <img src="${BASE_PATH}/images/logo.png" />
 */
class ContextPathHandler extends Handler {
  def this() {
    this()
    contextPathName = "CONTEXT_PATH"
  }

  def this(contextPathName: String) {
    this()
    if (StrKit.isBlank(contextPathName)) throw new IllegalArgumentException("contextPathName can not be blank.")
    this.contextPathName = contextPathName
  }

  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    request.setAttribute(contextPathName, request.getContextPath)
    nextHandler.handle(target, request, response, isHandled)
  }

  private var contextPathName: String = null
}

