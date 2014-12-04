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

import javax.servlet.http.HttpServletResponse

import com.sfinal.render.{Render, RedirectRender}

/**
 * Redirect301Render.
 */
object Redirect301Render {
  private final val contextPath: String = RedirectRender.getContxtPath
}

class Redirect301Render extends Render {
  def this(url: String) {
    this()
    this.url = url
    this.withQueryString = false
  }

  def this(url: String, withQueryString: Boolean) {
    this()
    this.url = url
    this.withQueryString = withQueryString
  }

  def render {
    if (contextPath != null && url.indexOf("://") == -1) url = contextPath + url
    if (withQueryString) {
      val queryString: String = request.getQueryString
      if (queryString != null) url = url + "?" + queryString
    }
    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY)
    response.setHeader("Location", url)
    response.setHeader("Connection", "close")
  }

  private var url: String = null
  private var withQueryString: Boolean = false
}

