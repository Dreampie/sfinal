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
package com.sfinal.ext.interceptor

import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller

/**
 * Accept POST method only.
 */
class POST extends Interceptor {
  def intercept(ai: ActionInvocation) {
    val controller: Controller = ai.getController
    if ("POST".equalsIgnoreCase(controller.getRequest.getMethod.toUpperCase)) ai.invoke
    else controller.renderError(404)
  }
}

