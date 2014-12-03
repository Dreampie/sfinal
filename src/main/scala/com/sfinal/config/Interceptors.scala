package com.sfinal.config

import com.sfinal.aop.Interceptor

/**
 * Created by ice on 14-12-3.
 */
final class Interceptors {
  private val interceptorList: List[Interceptor] = Nil

  def add(globalInterceptor: Interceptor): Interceptors = {
    if (globalInterceptor != null) globalInterceptor :: this.interceptorList
    return this
  }

  def getInterceptorArray: Array[Interceptor] = {
    val result: Array[Interceptor] = interceptorList.toArray
    return if (result == null) new Array[Interceptor](0) else result
  }
}
