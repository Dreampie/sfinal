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
package com.sfinal.kit

import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.HashMap
import java.util.Iterator
import java.util.List
import java.util.Map
import com.sfinal.kit.StrKit
import com.sfinal.plugin.activerecord.Model
import com.sfinal.plugin.activerecord.Model
import com.sfinal.plugin.activerecord.Record
import com.sfinal.plugin.activerecord.Record

/**
 * Convert object to json string.
 *
 * Json     			java
 * string			java.lang.String
 * number			java.lang.Number
 * true|false		java.lang.Boolean
 * null				null
 * array			java.util.List
 * object			java.util.Map
 */
@SuppressWarnings(Array("rawtypes", "unchecked")) object JsonKit {
  def setConvertDepth(convertDepth: Int) {
    if (convertDepth < 2) throw new IllegalArgumentException("convert depth can not less than 2.")
    JsonKit.convertDepth = convertDepth
  }

  def setTimestampPattern(timestampPattern: String) {
    if (timestampPattern == null || ("" == timestampPattern.trim)) throw new IllegalArgumentException("timestampPattern can not be blank.")
    JsonKit.timestampPattern = timestampPattern
  }

  def setDatePattern(datePattern: String) {
    if (datePattern == null || ("" == datePattern.trim)) throw new IllegalArgumentException("datePattern can not be blank.")
    JsonKit.datePattern = datePattern
  }

  private def mapToJson(map: Map[_, _], depth: Int): String = {
    if (map == null) return "null"
    val sb: StringBuilder = new StringBuilder
    var first: Boolean = true
    val iter: Iterator[_] = map.entrySet.iterator
    sb.append('{')
    while (iter.hasNext) {
      if (first) first = false
      else sb.append(',')
      val entry: Map.Entry[_, _] = iter.next.asInstanceOf[Map.Entry[_, _]]
      toKeyValue(String.valueOf(entry.getKey), entry.getValue, sb, depth)
    }
    sb.append('}')
    return sb.toString
  }

  private def toKeyValue(key: String, value: AnyRef, sb: StringBuilder, depth: Int): String = {
    sb.append('\"')
    if (key == null) sb.append("null")
    else escape(key, sb)
    sb.append('\"').append(':')
    sb.append(toJson(value, depth))
    return sb.toString
  }

  private def listToJson(list: List[_], depth: Int): String = {
    if (list == null) return "null"
    var first: Boolean = true
    val sb: StringBuilder = new StringBuilder
    val iter: Iterator[_] = list.iterator
    sb.append('[')
    while (iter.hasNext) {
      if (first) first = false
      else sb.append(',')
      val value: AnyRef = iter.next
      if (value == null) {
        sb.append("null")
        continue //todo: continue is not supported
      }
      sb.append(toJson(value, depth))
    }
    sb.append(']')
    return sb.toString
  }

  /**
   * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
   */
  private def escape(s: String): String = {
    if (s == null) return null
    val sb: StringBuilder = new StringBuilder
    escape(s, sb)
    return sb.toString
  }

  private def escape(s: String, sb: StringBuilder) {
    {
      var i: Int = 0
      while (i < s.length) {
        {
          val ch: Char = s.charAt(i)
          ch match {
            case '"' =>
              sb.append("\\\"")
              break //todo: break is not supported
            case '\\' =>
              sb.append("\\\\")
              break //todo: break is not supported
            case '\b' =>
              sb.append("\\b")
              break //todo: break is not supported
            case '\f' =>
              sb.append("\\f")
              break //todo: break is not supported
            case '\n' =>
              sb.append("\\n")
              break //todo: break is not supported
            case '\r' =>
              sb.append("\\r")
              break //todo: break is not supported
            case '\t' =>
              sb.append("\\t")
              break //todo: break is not supported
            case '/' =>
              sb.append("\\/")
              break //todo: break is not supported
            case _ =>
              if ((ch >= '\u0000' && ch <= '\u001F') || (ch >= '\u007F' && ch <= '\u009F') || (ch >= '\u2000' && ch <= '\u20FF')) {
                val str: String = Integer.toHexString(ch)
                sb.append("\\u")
                {
                  var k: Int = 0
                  while (k < 4 - str.length) {
                    {
                      sb.append('0')
                    }
                    ({
                      k += 1; k - 1
                    })
                  }
                }
                sb.append(str.toUpperCase)
              }
              else {
                sb.append(ch)
              }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def toJson(value: AnyRef): String = {
    return toJson(value, convertDepth)
  }

  def toJson(value: AnyRef, depth: Int): String = {
    if (value == null || (({
      depth -= 1; depth + 1
    })) < 0) return "null"
    if (value.isInstanceOf[String]) return "\"" + escape(value.asInstanceOf[String]) + "\""
    if (value.isInstanceOf[Double]) {
      if ((value.asInstanceOf[Double]).isInfinite || (value.asInstanceOf[Double]).isNaN) return "null"
      else return value.toString
    }
    if (value.isInstanceOf[Float]) {
      if ((value.asInstanceOf[Float]).isInfinite || (value.asInstanceOf[Float]).isNaN) return "null"
      else return value.toString
    }
    if (value.isInstanceOf[Number]) return value.toString
    if (value.isInstanceOf[Boolean]) return value.toString
    if (value.isInstanceOf[Date]) {
      if (value.isInstanceOf[Timestamp]) return "\"" + new SimpleDateFormat(timestampPattern).format(value) + "\""
      if (value.isInstanceOf[Time]) return "\"" + value.toString + "\""
      return "\"" + new SimpleDateFormat(datePattern).format(value) + "\""
    }
    if (value.isInstanceOf[Map[_, _]]) {
      return mapToJson(value.asInstanceOf[Map[_, _]], depth)
    }
    if (value.isInstanceOf[List[_]]) {
      return listToJson(value.asInstanceOf[List[_]], depth)
    }
    val result: String = otherToJson(value, depth)
    if (result != null) return result
    return "\"" + escape(value.toString) + "\""
  }

  private def otherToJson(value: AnyRef, depth: Int): String = {
    if (value.isInstanceOf[Character]) {
      return "\"" + escape(value.toString) + "\""
    }
    if (value.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]) {
      val map: Map[_, _] = com.jfinal.plugin.activerecord.CPI.getAttrs(value.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]])
      return mapToJson(map, depth)
    }
    if (value.isInstanceOf[Record]) {
      val map: Map[_, _] = (value.asInstanceOf[Record]).getColumns
      return mapToJson(map, depth)
    }
    if (value.isInstanceOf[Array[AnyRef]]) {
      val arr: Array[AnyRef] = value.asInstanceOf[Array[AnyRef]]
      val list: List[_] = new ArrayList[_](arr.length)
      {
        var i: Int = 0
        while (i < arr.length) {
          list.add(arr(i))
          ({
            i += 1; i - 1
          })
        }
      }
      return listToJson(list, depth)
    }
    if (value.isInstanceOf[Enum[_ <: Enum[E]]]) {
      return "\"" + (value.asInstanceOf[Enum[_ <: Enum[E]]]).toString + "\""
    }
    return beanToJson(value, depth)
  }

  private def beanToJson(model: AnyRef, depth: Int): String = {
    val map: Map[_, _] = new HashMap[_, _]
    val methods: Array[Method] = model.getClass.getMethods
    for (m <- methods) {
      val methodName: String = m.getName
      val indexOfGet: Int = methodName.indexOf("get")
      if (indexOfGet == 0 && methodName.length > 3) {
        val attrName: String = methodName.substring(3)
        if (!(attrName == "Class")) {
          val types: Array[Class[_]] = m.getParameterTypes
          if (types.length == 0) {
            try {
              val value: AnyRef = m.invoke(model)
              map.put(StrKit.firstCharToLowerCase(attrName), value)
            }
            catch {
              case e: Exception => {
                throw new RuntimeException(e.getMessage, e)
              }
            }
          }
        }
      }
      else {
        val indexOfIs: Int = methodName.indexOf("is")
        if (indexOfIs == 0 && methodName.length > 2) {
          val attrName: String = methodName.substring(2)
          val types: Array[Class[_]] = m.getParameterTypes
          if (types.length == 0) {
            try {
              val value: AnyRef = m.invoke(model)
              map.put(StrKit.firstCharToLowerCase(attrName), value)
            }
            catch {
              case e: Exception => {
                throw new RuntimeException(e.getMessage, e)
              }
            }
          }
        }
      }
    }
    return mapToJson(map, depth)
  }

  private var convertDepth: Int = 8
  private var timestampPattern: String = "yyyy-MM-dd HH:mm:ss"
  private var datePattern: String = "yyyy-MM-dd"
}







