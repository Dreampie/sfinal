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
import java.util.HashMap
import java.util.Map
import com.sfinal.aop._

/**
 * InterceptorBuilder
 */
object InterceptorBuilder {
  private final val NULL_INTERCEPTOR_ARRAY: Array[Interceptor] = new Array[Interceptor](0)
}

class InterceptorBuilder {
  @SuppressWarnings(Array("unchecked")) private[core] def addToInterceptorsMap(defaultInters: Array[Interceptor]) {
    for (inter <- defaultInters) intersMap.put(inter.getClass.asInstanceOf[Class[Interceptor]], inter)
  }

  /**
   * Build interceptors of Controller
   */
  private[core] def buildControllerInterceptors(controllerClass: Class[_ <: Controller]): Array[Interceptor] = {
    val before: Before = controllerClass.getAnnotation(classOf[Before])
    return if (before != null) createInterceptors(before) else NULL_INTERCEPTOR_ARRAY
  }

  /**
   * Build interceptors of Method
   */
  private[core] def buildMethodInterceptors(method: Method): Array[Interceptor] = {
    val before: Before = method.getAnnotation(classOf[Before])
    return if (before != null) createInterceptors(before) else NULL_INTERCEPTOR_ARRAY
  }

  /**
   * Build interceptors of Action
   */
  private[core] def buildActionInterceptors(defaultInters: Array[Interceptor], controllerInters: Array[Interceptor], controllerClass: Class[_ <: Controller], methodInters: Array[Interceptor], method: Method): Array[Interceptor] = {
    val controllerClearType: ClearLayer = getControllerClearType(controllerClass)
    if (controllerClearType != null) {
      defaultInters = NULL_INTERCEPTOR_ARRAY
    }
    val methodClearType: ClearLayer = getMethodClearType(method)
    if (methodClearType != null) {
      controllerInters = NULL_INTERCEPTOR_ARRAY
      if (methodClearType eq ClearLayer.ALL) {
        defaultInters = NULL_INTERCEPTOR_ARRAY
      }
    }
    val size: Int = defaultInters.length + controllerInters.length + methodInters.length
    val result: Array[Interceptor] = (if (size == 0) NULL_INTERCEPTOR_ARRAY else new Array[Interceptor](size))
    val index: Int = 0
    {
      var i: Int = 0
      while (i < defaultInters.length) {
        {
          result(({
            index += 1; index - 1
          })) = defaultInters(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < controllerInters.length) {
        {
          result(({
            index += 1; index - 1
          })) = controllerInters(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    {
      var i: Int = 0
      while (i < methodInters.length) {
        {
          result(({
            index += 1; index - 1
          })) = methodInters(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
    return result
  }

  private def getMethodClearType(method: Method): ClearLayer = {
    val clearInterceptor: ClearInterceptor = method.getAnnotation(classOf[ClearInterceptor])
    return if (clearInterceptor != null) clearInterceptor.value else null
  }

  private def getControllerClearType(controllerClass: Class[_ <: Controller]): ClearLayer = {
    val clearInterceptor: ClearInterceptor = controllerClass.getAnnotation(classOf[ClearInterceptor])
    return if (clearInterceptor != null) clearInterceptor.value else null
  }

  /**
   * Create interceptors with Annotation of Before. Singleton version.
   */
  private def createInterceptors(beforeAnnotation: Before): Array[Interceptor] = {
    var result: Array[Interceptor] = null
    @SuppressWarnings(Array("unchecked")) val interceptorClasses: Array[Class[Interceptor]] = beforeAnnotation.value.asInstanceOf[Array[Class[Interceptor]]]
    if (interceptorClasses != null && interceptorClasses.length > 0) {
      result = new Array[Interceptor](interceptorClasses.length)
      {
        var i: Int = 0
        while (i < result.length) {
          {
            result(i) = intersMap.get(interceptorClasses(i))
            if (result(i) != null) continue //todo: continue is not supported
            try {
              result(i) = interceptorClasses(i).newInstance.asInstanceOf[Interceptor]
              intersMap.put(interceptorClasses(i), result(i))
            }
            catch {
              case e: Exception => {
                throw new RuntimeException(e)
              }
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    return result
  }

  private var intersMap: Map[Class[Interceptor], Interceptor] = new HashMap[Class[Interceptor], Interceptor]
}





