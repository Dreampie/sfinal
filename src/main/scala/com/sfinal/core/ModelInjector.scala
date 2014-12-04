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
package com.sfinal.core

import java.lang.reflect.Method
import java.util.Map
import java.util.Map.Entry
import javax.servlet.http.HttpServletRequest
import com.sfinal.core.TypeConverter
import com.sfinal.kit.StrKit
import com.sfinal.kit.StrKit
import com.sfinal.plugin.activerecord.ActiveRecordException
import com.sfinal.plugin.activerecord.ActiveRecordException
import com.sfinal.plugin.activerecord.Model
import com.sfinal.plugin.activerecord.Model
import com.sfinal.plugin.activerecord.Table
import com.sfinal.plugin.activerecord.Table
import com.sfinal.plugin.activerecord.TableMapping
import com.sfinal.plugin.activerecord.TableMapping

/**
 * ModelInjector
 */
final object ModelInjector {
  @SuppressWarnings(Array("unchecked")) def inject(modelClass: Class[_], request: HttpServletRequest, skipConvertError: Boolean): T = {
    val modelName: String = modelClass.getSimpleName
    return inject(modelClass, StrKit.firstCharToLowerCase(modelName), request, skipConvertError).asInstanceOf[T]
  }

  @SuppressWarnings(Array("rawtypes", "unchecked")) final def inject(modelClass: Class[_], modelName: String, request: HttpServletRequest, skipConvertError: Boolean): T = {
    var model: AnyRef = null
    try {
      model = modelClass.newInstance
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    if (model.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]) injectActiveRecordModel(model.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]], modelName, request, skipConvertError)
    else injectCommonModel(model, modelName, request, modelClass, skipConvertError)
    return model.asInstanceOf[T]
  }

  private final def injectCommonModel(model: AnyRef, modelName: String, request: HttpServletRequest, modelClass: Class[_], skipConvertError: Boolean) {
    val methods: Array[Method] = modelClass.getMethods
    for (method <- methods) {
      val methodName: String = method.getName
      if (methodName.startsWith("set") == false) continue //todo: continue is not supported
      val types: Array[Class[_]] = method.getParameterTypes
      if (types.length != 1) continue //todo: continue is not supported
      val attrName: String = methodName.substring(3)
      val value: String = request.getParameter(modelName + "." + StrKit.firstCharToLowerCase(attrName))
      if (value != null) {
        try {
          method.invoke(model, TypeConverter.convert(types(0), value))
        }
        catch {
          case e: Exception => {
            if (skipConvertError == false) throw new RuntimeException(e)
          }
        }
      }
    }
  }

  @SuppressWarnings(Array("rawtypes")) private final def injectActiveRecordModel(model: Model[_], modelName: String, request: HttpServletRequest, skipConvertError: Boolean) {
    val table: Table = TableMapping.me.getTable(model.getClass)
    val modelNameAndDot: String = modelName + "."
    val parasMap: Map[String, Array[String]] = request.getParameterMap
    import scala.collection.JavaConversions._
    for (e <- parasMap.entrySet) {
      val paraKey: String = e.getKey
      if (paraKey.startsWith(modelNameAndDot)) {
        val paraName: String = paraKey.substring(modelNameAndDot.length)
        val colType: Class[_] = table.getColumnType(paraName)
        if (colType == null) throw new ActiveRecordException("The model attribute " + paraKey + " is not exists.")
        val paraValue: Array[String] = e.getValue
        try {
          val value: AnyRef = if (paraValue(0) != null) TypeConverter.convert(colType, paraValue(0)) else null
          model.set(paraName, value)
        }
        catch {
          case ex: Exception => {
            if (skipConvertError == false) throw new RuntimeException("Can not convert parameter: " + modelNameAndDot + paraName, ex)
          }
        }
      }
    }
  }
}



