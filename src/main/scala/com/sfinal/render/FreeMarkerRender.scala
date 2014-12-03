package com.sfinal.render

import java.io.{IOException, PrintWriter}
import java.util
import java.util.{Locale, Properties}
import javax.servlet.ServletContext

import freemarker.template._

import scala.collection.immutable.HashMap

/**
 * Created by ice on 14-12-3.
 */
class FreeMarkerRender extends Render {


  def this(view: String) {
    this()
    this.view = view
  }


  /**
   * Set freemarker's property.
   * The value of template_update_delay is 5 seconds.
   * Example: FreeMarkerRender.setProperty("template_update_delay", "1600");
   */
  def property_=(propertyName: String, propertyValue: String) {
    try {
      FreeMarkerRender.configuration.setSetting(propertyName, propertyValue)
    }
    catch {
      case e: TemplateException => {
        throw new RuntimeException(e)
      }
    }
  }

  def properties_=(properties: Properties) {
    try {
      FreeMarkerRender.configuration.setSettings(properties)
    }
    catch {
      case e: TemplateException => {
        throw new RuntimeException(e)
      }
    }
  }


  def render {
    response.setContentType(FreeMarkerRender.contentType)
    var root: Map[String, Object] = HashMap[String, Object]()
    val attrs: util.Enumeration[_] = request.getAttributeNames
    while (attrs.hasMoreElements) {
      val attrName: String = attrs.nextElement.toString
      root += ((attrName, request.getAttribute(attrName)))
    }

    var writer: PrintWriter = null
    try {
      val template: Template = FreeMarkerRender.configuration.getTemplate(view)
      writer = response.getWriter
      template.process(root, writer)
    }
    catch {
      case e: IOException => {
        throw new RuntimeException(e)
      }
    } finally {
      if (writer != null) writer.close
    }
  }
}

object FreeMarkerRender {
  private val encoding: String = Render.encoding
  private val contentType: String = "text/html; charset=" + encoding
  private val config: Configuration = new Configuration


  /**
   * freemarker can not load freemarker.properies automatically
   */
  def configuration: Configuration = {
    config
  }

  def init(servletContext: ServletContext, locale: Locale, template_update_delay: Int) {
    config.setServletContextForTemplateLoading(servletContext, "/")
    if (Render.devMode) {
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
}