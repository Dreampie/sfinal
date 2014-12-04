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
package com.sfinal.plugin.ehcache

import java.io.Serializable
import com.sfinal.plugin.ehcache.RenderType
import com.sfinal.render.FreeMarkerRender
import com.sfinal.render.FreeMarkerRender
import com.sfinal.render.JspRender
import com.sfinal.render.JspRender
import com.sfinal.render.Render
import com.sfinal.render.Render
import com.sfinal.render.VelocityRender
import com.sfinal.render.VelocityRender
import com.sfinal.render.XmlRender
import com.sfinal.render.XmlRender

/**
 * RenderInfo.
 */
object RenderInfo {
  private final val serialVersionUID: Long = -7299875545092102194L
}

class RenderInfo extends Serializable {
  def this(render: Render) {
    this()
    if (render == null) throw new IllegalArgumentException("Render can not be null.")
    view = render.getView
    if (render.isInstanceOf[FreeMarkerRender]) renderType = RenderType.FREE_MARKER_RENDER
    else if (render.isInstanceOf[JspRender]) renderType = RenderType.JSP_RENDER
    else if (render.isInstanceOf[VelocityRender]) renderType = RenderType.VELOCITY_RENDER
    else if (render.isInstanceOf[XmlRender]) renderType = RenderType.XML_RENDER
    else throw new IllegalArgumentException("CacheInterceptor can not support the render of the type : " + render.getClass.getName)
  }

  def createRender: Render = {
    if (renderType eq RenderType.FREE_MARKER_RENDER) return new FreeMarkerRender(view)
    else if (renderType eq RenderType.JSP_RENDER) return new JspRender(view)
    else if (renderType eq RenderType.VELOCITY_RENDER) return new VelocityRender(view)
    else if (renderType eq RenderType.XML_RENDER) return new XmlRender(view)
    throw new IllegalArgumentException("CacheInterceptor can not support the renderType of the value : " + renderType)
  }

  private var view: String = null
  private var renderType: Integer = null
}

