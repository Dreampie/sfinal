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
import java.util.Enumeration
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.Set
import com.sfinal.kit.JsonKit
import com.sfinal.render.{RenderException, Render}

/**
 * JsonRender.
 * <p>
 * IE 不支持content type 为 application/json, 在 ajax 上传文件完成后返回 json时 IE 提示下载文件,<br>
 * 解决办法是使用： render(new JsonRender(params).forIE());
 */
object JsonRender {
  /**
   * 仅对无参 renderJson() 起作用
   */
  def addExcludedAttrs(attrs: String*) {
    if (attrs != null) for (attr <- attrs) excludedAttrs.add(attr)
  }

  def removeExcludedAttrs(attrs: String*) {
    if (attrs != null) for (attr <- attrs) excludedAttrs.remove(attr)
  }

  def clearExcludedAttrs {
    excludedAttrs.clear
  }

  def setConvertDepth(convertDepth: Int) {
    if (convertDepth < 2) throw new IllegalArgumentException("convert depth can not less than 2.")
    JsonRender.convertDepth = convertDepth
  }

  /**
   * It creates the extra attribute below while tomcat take SSL open.
   * http://git.oschina.net/jfinal/jfinal/issues/10
   */
  private final val excludedAttrs: Set[String] =
  new
  /**
   * http://zh.wikipedia.org/zh/MIME
   * 在wiki中查到: 尚未被接受为正式数据类型的subtype，可以使用x-开始的独立名称（例如application/x-gzip）
   * 所以以下可能要改成 application/x-json
   *
   * 通过使用firefox测试,struts2-json-plugin返回的是 application/json, 所以暂不改为 application/x-json
   * 1: 官方的 MIME type为application/json, 见 http://en.wikipedia.org/wiki/MIME_type
   * 2: IE 不支持 application/json, 在 ajax 上传文件完成后返回 json时 IE 提示下载文件
   */
  private final val contentType: String = "application/json; charset=" + getEncoding
  private final val contentTypeForIE: String = "text/html; charset=" + getEncoding
  private var convertDepth: Int = 8
}

class JsonRender extends Render {
  def forIE: JsonRender = {
    forIE = true
    return this
  }

  def this() {
    this()
  }

  @SuppressWarnings(Array("serial")) def this(key: String, value: AnyRef) {
    this()
    if (key == null) throw new IllegalArgumentException("The parameter key can not be null.")
    this.jsonText = JsonKit.toJson(new HashMap[String, AnyRef] {
    }, convertDepth)
  }

  def this(attrs: Array[String]) {
    this()
    if (attrs == null) throw new IllegalArgumentException("The parameter attrs can not be null.")
    this.attrs = attrs
  }

  def this(jsonText: String) {
    this()
    if (jsonText == null) throw new IllegalArgumentException("The parameter jsonString can not be null.")
    this.jsonText = jsonText
  }

  def this(`object`: AnyRef) {
    this()
    if (`object` == null) throw new IllegalArgumentException("The parameter object can not be null.")
    this.jsonText = JsonKit.toJson(`object`, convertDepth)
  }

  def render {
    if (jsonText == null) buildJsonText
    var writer: PrintWriter = null
    try {
      response.setHeader("Pragma", "no-cache")
      response.setHeader("Cache-Control", "no-cache")
      response.setDateHeader("Expires", 0)
      response.setContentType(if (forIE) contentTypeForIE else contentType)
      writer = response.getWriter
      writer.write(jsonText)
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

  @SuppressWarnings(Array("rawtypes", "unchecked")) private def buildJsonText {
    val map: Map[_, _] = new HashMap[_, _]
    if (attrs != null) {
      for (key <- attrs) map.put(key, request.getAttribute(key))
    }
    else {
      {
        val attrs: Enumeration[String] = request.getAttributeNames
        while (attrs.hasMoreElements) {
          val key: String = attrs.nextElement
          if (excludedAttrs.contains(key)) continue //todo: continue is not supported
          val value: AnyRef = request.getAttribute(key)
          map.put(key, value)
        }
      }
    }
    this.jsonText = JsonKit.toJson(map, convertDepth)
  }

  private var forIE: Boolean = false
  private var jsonText: String = null
  private var attrs: Array[String] = null
}





