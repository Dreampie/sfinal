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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Enumeration
import javax.servlet.http.HttpServletRequest
import com.sfinal.aop.Interceptor

/**
 * ActionReporter
 */
final object ActionReporter {
  /**
   * Report action before action invoking when the common request coming
   */
  private[core] final def reportCommonRequest(controller: Controller, action: Action): Boolean = {
    val content_type: String = controller.getRequest.getContentType
    if (content_type == null || content_type.toLowerCase.indexOf("multipart") == -1) {
      doReport(controller, action)
      return false
    }
    return true
  }

  /**
   * Report action after action invoking when the multipart request coming
   */
  private[core] final def reportMultipartRequest(controller: Controller, action: Action) {
    doReport(controller, action)
  }

  private final def doReport(controller: Controller, action: Action) {
    val sb: StringBuilder = new StringBuilder("\nJFinal action report -------- ").append(sdf.format(new Date)).append(" ------------------------------\n")
    val cc: Class[_ <: Controller] = action.getControllerClass
    sb.append("Controller  : ").append(cc.getName).append(".(").append(cc.getSimpleName).append(".java:1)")
    sb.append("\nMethod      : ").append(action.getMethodName).append("\n")
    val urlParas: String = controller.getPara
    if (urlParas != null) {
      sb.append("UrlPara     : ").append(urlParas).append("\n")
    }
    val inters: Array[Interceptor] = action.getInterceptors
    if (inters.length > 0) {
      sb.append("Interceptor : ")
      {
        var i: Int = 0
        while (i < inters.length) {
          {
            if (i > 0) sb.append("\n              ")
            val inter: Interceptor = inters(i)
            val ic: Class[_ <: Interceptor] = inter.getClass
            sb.append(ic.getName).append(".(").append(ic.getSimpleName).append(".java:1)")
          }
          ({
            i += 1; i - 1
          })
        }
      }
      sb.append("\n")
    }
    val request: HttpServletRequest = controller.getRequest
    val e: Enumeration[String] = request.getParameterNames
    if (e.hasMoreElements) {
      sb.append("Parameter   : ")
      while (e.hasMoreElements) {
        val name: String = e.nextElement
        val values: Array[String] = request.getParameterValues(name)
        if (values.length == 1) {
          sb.append(name).append("=").append(values(0))
        }
        else {
          sb.append(name).append("[]={")
          {
            var i: Int = 0
            while (i < values.length) {
              {
                if (i > 0) sb.append(",")
                sb.append(values(i))
              }
              ({
                i += 1; i - 1
              })
            }
          }
          sb.append("}")
        }
        sb.append("  ")
      }
      sb.append("\n")
    }
    sb.append("--------------------------------------------------------------------------------\n")
    System.out.print(sb.toString)
  }

  private final val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
}


