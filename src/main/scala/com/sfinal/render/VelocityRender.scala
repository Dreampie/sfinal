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
import java.util.Map.Entry
import java.util.Iterator
import java.util.Properties
import java.util.Set
import javax.servlet.ServletContext
import org.apache.velocity.Template
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import org.apache.velocity.app.Velocity
import org.apache.velocity.exception.ParseErrorException
import org.apache.velocity.exception.ResourceNotFoundException

/**
 * VelocityRender.
 */
object VelocityRender {
  private[render] def init(servletContext: ServletContext) {
    val webPath: String = servletContext.getRealPath("/")
    properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, webPath)
    properties.setProperty(Velocity.ENCODING_DEFAULT, encoding)
    properties.setProperty(Velocity.INPUT_ENCODING, encoding)
    properties.setProperty(Velocity.OUTPUT_ENCODING, encoding)
  }

  def setProperties(properties: Properties) {
    val set: Set[Map.Entry[AnyRef, AnyRef]] = properties.entrySet
    {
      val it: Iterator[Map.Entry[AnyRef, AnyRef]] = set.iterator
      while (it.hasNext) {
        val e: Map.Entry[AnyRef, AnyRef] = it.next
        VelocityRender.properties.put(e.getKey, e.getValue)
      }
    }
  }

  private final val encoding: String = getEncoding
  private final val contentType: String = "text/html; charset=" + encoding
  private final val properties: Properties = new Properties
  private var notInit: Boolean = true
}

class VelocityRender extends Render {
  def this(view: String) {
    this()
    this.view = view
  }

  def render {
    if (notInit) {
      Velocity.init(properties)
      notInit = false
    }
    var writer: PrintWriter = null
    try {
      val context: VelocityContext = new VelocityContext
      {
        val attrs: Enumeration[String] = request.getAttributeNames
        while (attrs.hasMoreElements) {
          val attrName: String = attrs.nextElement
          context.put(attrName, request.getAttribute(attrName))
        }
      }
      val template: Template = Velocity.getTemplate(view)
      response.setContentType(contentType)
      writer = response.getWriter
      template.merge(context, writer)
      writer.flush
    }
    catch {
      case e: ResourceNotFoundException => {
        throw new RenderException("Example : error : cannot find template " + view, e)
      }
      case e: ParseErrorException => {
        throw new RenderException("Example : Syntax error in template " + view + ":" + e, e)
      }
      case e: Exception => {
        throw new RenderException(e)
      }
    }
    finally {
      if (writer != null) writer.close
    }
  }
}







