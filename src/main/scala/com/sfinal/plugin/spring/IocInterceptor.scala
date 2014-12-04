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
package com.sfinal.plugin.spring

import java.lang.reflect.Field
import org.springframework.context.ApplicationContext
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller
import org.springframework.context.ApplicationContext

/**
 * IocInterceptor.
 */
object IocInterceptor {
  private[spring] var ctx: ApplicationContext = null
}

class IocInterceptor extends Interceptor {
  def intercept(ai: ActionInvocation) {
    val controller: Controller = ai.getController
    val fields: Array[Field] = controller.getClass.getDeclaredFields
    for (field <- fields) {
      var bean: AnyRef = null
      if (field.isAnnotationPresent(classOf[Inject.BY_NAME])) bean = ctx.getBean(field.getName)
      else if (field.isAnnotationPresent(classOf[Inject.BY_TYPE])) bean = ctx.getBean(field.getType)
      else continue //todo: continue is not supported
      try {
        if (bean != null) {
          field.setAccessible(true)
          field.set(controller, bean)
        }
      }
      catch {
        case e: Exception => {
          throw new RuntimeException(e)
        }
      }
    }
    ai.invoke
  }
}

