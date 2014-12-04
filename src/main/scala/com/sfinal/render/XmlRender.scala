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

import java.io.PrintWriter
import java.util.Enumeration
import java.util.HashMap
import java.util.Map
import freemarker.template.Template

/**
 * XmlRender use FreeMarker
 */
object XmlRender {
  private final val contentType: String = "text/xml; charset=" + getEncoding
}

class XmlRender extends FreeMarkerRender {
  def this(view: String) {
    this()
    `super`(view)
  }

  @SuppressWarnings(Array("unchecked", "rawtypes")) override def render {
    response.setContentType(contentType)
    val root: Map[_, _] = new HashMap[_, _]
    {
      val attrs: Enumeration[String] = request.getAttributeNames
      while (attrs.hasMoreElements) {
        val attrName: String = attrs.nextElement
        root.put(attrName, request.getAttribute(attrName))
      }
    }
    var writer: PrintWriter = null
    try {
      val template: Template = getConfiguration.getTemplate(view)
      writer = response.getWriter
      template.process(root, writer)
    }
    catch {
      case e: Exception => {
        throw new RenderException(e)
      }
    }
    finally {
      if (writer != null) writer.close
    }
  }
}