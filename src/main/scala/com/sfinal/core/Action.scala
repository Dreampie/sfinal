package com.sfinal.core

import java.lang.reflect.Method

import com.sfinal.aop.Interceptor

/**
 * Created by ice on 14-12-3.
 */
class Action(
              var controllerClass: Class[Controller] = null,
              var controllerKey: String = null,
              var actionKey: String = null,
              var method: Method = null,
              var methodName: String = null,
              var interceptors: Array[Interceptor] = null,
              var viewPath: String = null
              )
