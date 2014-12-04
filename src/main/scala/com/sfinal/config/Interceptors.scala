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
package com.sfinal.config

import com.sfinal.aop.Interceptor

/**
 * The interceptors applied to all actions.
 */
final class Interceptors {
  private val interceptorList: List[Interceptor] = Nil

  def add(globalInterceptor: Interceptor): Interceptors = {
    if (globalInterceptor != null) globalInterceptor :: this.interceptorList
    this
  }

  def getInterceptorArray: Array[Interceptor] = {
    val result: Array[Interceptor] = interceptorList.toArray[Interceptor]
    if (result == null) new Array[Interceptor](0) else result
  }
}

