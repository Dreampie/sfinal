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

import java.util.regex.Pattern
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import com.sfinal.handler.Handler
import com.sfinal.handler.Handler
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit

/**
 * Skip the excluded url request from browser.
 * The skiped url will be handled by next Filter after JFinalFilter
 * <p>
 * Example: me.add(new UrlSkipHandler(".+\\.\\w{1,4}", false));
 */
class UrlSkipHandler extends Handler {
  def this(skipedUrlRegx: String, isCaseSensitive: Boolean) {
    this()
    if (StrKit.isBlank(skipedUrlRegx)) throw new IllegalArgumentException("The para excludedUrlRegx can not be blank.")
    skipedUrlPattern = if (isCaseSensitive) Pattern.compile(skipedUrlRegx) else Pattern.compile(skipedUrlRegx, Pattern.CASE_INSENSITIVE)
  }

  def handle(target: String, request: HttpServletRequest, response: HttpServletResponse, isHandled: Array[Boolean]) {
    if (skipedUrlPattern.matcher(target).matches) return
    else nextHandler.handle(target, request, response, isHandled)
  }

  private var skipedUrlPattern: Pattern = null
}



