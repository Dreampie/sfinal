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
import java.io.PrintWriter

import com.sfinal.render.{RenderException, Render}

/**
 * HtmlRender.
 */
object HtmlRender {
  private final val contentType: String = "text/html; charset=" + getEncoding
}

class HtmlRender extends Render {
  def this(text: String) {
    this()
    this.text = text
  }

  def render {
    var writer: PrintWriter = null
    try {
      response.setHeader("Pragma", "no-cache")
      response.setHeader("Cache-Control", "no-cache")
      response.setDateHeader("Expires", 0)
      response.setContentType(contentType)
      writer = response.getWriter
      writer.write(text)
      writer.flush
    }
    catch {
      case e: IOException => {
        throw new RenderException(e)
      }
    }
    finally {
      if (writer != null) writer.close
    }
  }

  private var text: String = null
}





