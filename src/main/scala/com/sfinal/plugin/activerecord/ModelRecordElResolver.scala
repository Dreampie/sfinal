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
package com.sfinal.plugin.activerecord

import java.beans.FeatureDescriptor
import java.util.Iterator
import javax.el.ELContext
import javax.el.ELResolver
import javax.el.ELResolver
import javax.servlet.ServletContext
import javax.servlet.jsp.JspApplicationContext
import javax.servlet.jsp.JspApplicationContext
import javax.servlet.jsp.JspFactory
import javax.servlet.jsp.JspFactory

import com.sfinal.plugin.activerecord.Record

/**
 * ModelRecordElResolver
 */
@SuppressWarnings(Array("rawtypes")) object ModelRecordElResolver {
  def setWorking(isWorking: Boolean) {
    ModelRecordElResolver.isWorking = isWorking
  }

  def init(servletContext: ServletContext) {
    val jac: JspApplicationContext = JspFactory.getDefaultFactory.getJspApplicationContext(servletContext)
    if (jspApplicationContext ne jac) {
      jspApplicationContext = jac
      jspApplicationContext.addELResolver(new ModelRecordElResolver)
    }
  }

  def init {
    init(com.jfinal.core.JFinal.me.getServletContext)
  }

  private[activerecord] var jspApplicationContext: JspApplicationContext = null
  /**
   * Compatible for JspRender.setSupportActiveRecord(true);
   * Delete it in the future
   */
  private var isWorking: Boolean = true
}

@SuppressWarnings(Array("rawtypes")) class ModelRecordElResolver extends ELResolver {
  def getValue(context: ELContext, base: AnyRef, property: AnyRef): AnyRef = {
    if (isWorking == false) return null
    if (base.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]) {
      context.setPropertyResolved(true)
      if (property == null) return null
      return (base.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]).get(property.toString)
    }
    else if (base.isInstanceOf[Record]) {
      context.setPropertyResolved(true)
      if (property == null) return null
      return (base.asInstanceOf[Record]).get(property.toString)
    }
    return null
  }

  def getType(context: ELContext, base: AnyRef, property: AnyRef): Class[_] = {
    if (isWorking == false) return null
    return if ((base == null)) null else classOf[AnyRef]
  }

  def setValue(context: ELContext, base: AnyRef, property: AnyRef, value: AnyRef) {
    if (isWorking == false) return
    if (base.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]) {
      context.setPropertyResolved(true)
      if (property == null) return
      try {
        (base.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]).set(property.toString, value)
      }
      catch {
        case e: Exception => {
          (base.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]).put(property.toString, value)
        }
      }
    }
    else if (base.isInstanceOf[Record]) {
      context.setPropertyResolved(true)
      if (property == null) return
      (base.asInstanceOf[Record]).set(property.toString, value)
    }
  }

  def isReadOnly(context: ELContext, base: AnyRef, property: AnyRef): Boolean = {
    if (isWorking == false) return false
    if (base.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]] || base.isInstanceOf[Record]) {
      context.setPropertyResolved(true)
      return false
    }
    return false
  }

  def getFeatureDescriptors(context: ELContext, base: AnyRef): Iterator[FeatureDescriptor] = {
    return null
  }

  def getCommonPropertyType(context: ELContext, base: AnyRef): Class[_] = {
    if (isWorking == false) return null
    if (base.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]] || base.isInstanceOf[Record]) return classOf[String]
    return null
  }
}



