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

import java.util.ArrayList
import java.util.Enumeration
import java.util.HashMap
import java.util.List
import java.util.Map
import javax.servlet.http.HttpServletRequest
import com.sfinal.plugin.activerecord.CPI
import com.sfinal.plugin.activerecord.Model
import com.sfinal.plugin.activerecord.ModelRecordElResolver
import com.sfinal.plugin.activerecord.Page
import com.sfinal.plugin.activerecord.Record
import com.sfinal.render.{RenderException, Render}

/**
 * JspRender.
 */
@SuppressWarnings(Array("rawtypes", "unchecked")) object JspRender {
  @Deprecated def setSupportActiveRecord(supportActiveRecord: Boolean) {
    JspRender.isSupportActiveRecord = supportActiveRecord
    ModelRecordElResolver.setWorking(if (JspRender.isSupportActiveRecord) false else true)
  }

  private var isSupportActiveRecord: Boolean = false
  private var DEPTH: Int = 8
}

@SuppressWarnings(Array("rawtypes", "unchecked")) class JspRender extends Render {
  def this(view: String) {
    this()
    this.view = view
  }

  def render {
    try {
      if (isSupportActiveRecord) supportActiveRecord(request)
      request.getRequestDispatcher(view).forward(request, response)
    }
    catch {
      case e: Exception => {
        throw new RenderException(e)
      }
    }
  }

  private def supportActiveRecord(request: HttpServletRequest) {
    {
      val attrs: Enumeration[String] = request.getAttributeNames
      while (attrs.hasMoreElements) {
        val key: String = attrs.nextElement
        val value: AnyRef = request.getAttribute(key)
        request.setAttribute(key, handleObject(value, DEPTH))
      }
    }
  }

  private def handleObject(value: AnyRef, depth: Int): AnyRef = {
    if (value == null || (({
      depth -= 1; depth + 1
    })) <= 0) return value
    if (value.isInstanceOf[List[_]]) return handleList(value.asInstanceOf[List[_]], depth)
    else if (value.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]) return handleMap(CPI.getAttrs(value.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]), depth)
    else if (value.isInstanceOf[Record]) return handleMap((value.asInstanceOf[Record]).getColumns, depth)
    else if (value.isInstanceOf[Map[_, _]]) return handleMap(value.asInstanceOf[Map[_, _]], depth)
    else if (value.isInstanceOf[Page[_]]) return handlePage(value.asInstanceOf[Page[_]], depth)
    else if (value.isInstanceOf[Array[AnyRef]]) return handleArray(value.asInstanceOf[Array[AnyRef]], depth)
    else return value
  }

  private def handleMap(map: Map[_, _], depth: Int): Map[_, _] = {
    if (map == null || map.size == 0) return map
    val result: Map[AnyRef, AnyRef] = map
    import scala.collection.JavaConversions._
    for (e <- result.entrySet) {
      val key: AnyRef = e.getKey
      var value: AnyRef = e.getValue
      value = handleObject(value, depth)
      result.put(key, value)
    }
    return result
  }

  private def handleList(list: List[_], depth: Int): List[_] = {
    if (list == null || list.size == 0) return list
    val result: List[_] = new ArrayList[_](list.size)
    import scala.collection.JavaConversions._
    for (value <- list) result.add(handleObject(value, depth))
    return result
  }

  private def handlePage(page: Page[_], depth: Int): AnyRef = {
    val result: Map[String, AnyRef] = new HashMap[String, AnyRef]
    result.put("list", handleList(page.getList, depth))
    result.put("pageNumber", page.getPageNumber)
    result.put("pageSize", page.getPageSize)
    result.put("totalPage", page.getTotalPage)
    result.put("totalRow", page.getTotalRow)
    return result
  }

  private def handleArray(array: Array[AnyRef], depth: Int): List[_] = {
    if (array == null || array.length == 0) return new ArrayList[_](0)
    val result: List[_] = new ArrayList[_](array.length)
    {
      var i: Int = 0
      while (i < array.length) {
        result.add(handleObject(array(i), depth))
        ({
          i += 1; i - 1
        })
      }
    }
    return result
  }
}

/*
	private void handleGetterMethod(Map<String, Object> result, Method[] methods) {
		for (Method method : methods) {
			String methodName = method.getName();
			if (methodName.startsWith("get") && method.getParameterTypes().length == 0) {
				throw new RuntimeException("Not finished!");
			}
		}
	}
*/




