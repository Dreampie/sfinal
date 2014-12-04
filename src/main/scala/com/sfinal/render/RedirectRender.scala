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

import java.io.IOException
import com.sfinal.core.JFinal
import com.sfinal.render.{RenderException, Render}

/**
 * RedirectRender with status: 302 Found.
 */
object RedirectRender {
  private[render] def getContxtPath: String = {
    val cp: String = JFinal.me.getContextPath
    return if ((("" == cp) || ("/" == cp))) null else cp
  }

  private final val contextPath: String = getContxtPath
}

class RedirectRender extends Render {
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
      if (queryString != null) if (url.indexOf("?") == -1) url = url + "?" + queryString
      else url = url + "&" + queryString
    }
    try {
      response.sendRedirect(url)
    }
    catch {
      case e: IOException => {
        throw new RenderException(e)
      }
    }
  }

  private var url: String = null
  private var withQueryString: Boolean = false
}


