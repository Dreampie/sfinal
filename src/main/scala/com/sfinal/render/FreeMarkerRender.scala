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
import java.util.Locale
import java.util.Map
import java.util.Properties
import javax.servlet.ServletContext
import com.sfinal.render.{RenderException, Render}
import freemarker.template.Configuration
import freemarker.template.ObjectWrapper
import freemarker.template.Template
import freemarker.template.TemplateException
import freemarker.template.TemplateExceptionHandler

/**
 * FreeMarkerRender.
 */
object FreeMarkerRender {
  /**
   * freemarker can not load freemarker.properies automatically
   */
  def getConfiguration: Configuration = {
    return config
  }

  /**
   * Set freemarker's property.
   * The value of template_update_delay is 5 seconds.
   * Example: FreeMarkerRender.setProperty("template_update_delay", "1600");
   */
  def setProperty(propertyName: String, propertyValue: String) {
    try {
      FreeMarkerRender.getConfiguration.setSetting(propertyName, propertyValue)
    }
    catch {
      case e: TemplateException => {
        throw new RuntimeException(e)
      }
    }
  }

  def setProperties(properties: Properties) {
    try {
      FreeMarkerRender.getConfiguration.setSettings(properties)
    }
    catch {
      case e: TemplateException => {
        throw new RuntimeException(e)
      }
    }
  }

  private[render] def init(servletContext: ServletContext, locale: Locale, template_update_delay: Int) {
    config.setServletContextForTemplateLoading(servletContext, "/")
    if (getDevMode) {
      config.setTemplateUpdateDelay(0)
    }
    else {
      config.setTemplateUpdateDelay(template_update_delay)
    }
    config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
    config.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER)
    config.setDefaultEncoding(encoding)
    config.setOutputEncoding(encoding)
    config.setLocale(locale)
    config.setLocalizedLookup(false)
    config.setNumberFormat("#0.#####")
    config.setDateFormat("yyyy-MM-dd")
    config.setTimeFormat("HH:mm:ss")
    config.setDateTimeFormat("yyyy-MM-dd HH:mm:ss")
  }

  private final val encoding: String = getEncoding
  private final val contentType: String = "text/html; charset=" + encoding
  private final val config: Configuration = new Configuration
}

class FreeMarkerRender extends Render {
  def this(view: String) {
    this()
    this.view = view
  }

  @SuppressWarnings(Array("unchecked", "rawtypes")) def render {
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
      val template: Template = config.getTemplate(view)
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


