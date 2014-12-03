package com.sfinal.core

import java.lang.reflect.{Method, InvocationTargetException}

import com.sfinal.aop.Interceptor

/**
 * Created by ice on 14-12-3.
 */
class ActionInvocation protected {
  var controller: Controller = null
  private var inters: Array[Interceptor] = null
  private var action: Action = null
  private var index: Int = 0
  private val NULL_ARGS: Array[Nothing] = new Array[Nothing](0)

  def this(action: Action, controller: Controller) {
    this()
    this.controller = controller
    this.inters = action.interceptors
    this.action = action
  }

  /**
   * Invoke the action.
   */
  def invoke {
    if (index < inters.length)
      inters({
        index += 1;
        index - 1
      }).intercept(this)
    else if ( {
      index += 1;
      index - 1
    } == inters.length)
      try {
        action.method.invoke(controller, NULL_ARGS)
      } catch {
        case e: InvocationTargetException => {
          val cause: Throwable = e.getTargetException
          if (cause.isInstanceOf[RuntimeException]) throw cause.asInstanceOf[RuntimeException]
          throw new RuntimeException(e)
        }
        case e: RuntimeException => {
          throw e
        }
        case e: Exception => {
          throw new Nothing(e)
        }
      }
  }


  /**
   * Return the action key.
   * actionKey = controllerKey + methodName
   */
  def actionKey: String = {
    action.actionKey
  }

  /**
   * Return the controller key.
   */
  def controllerKey: String = {
    action.controllerKey
  }

  /**
   * Return the method of this action.
   * <p>
   * You can getMethod.getAnnotations() to get annotation on action method to do more things
   */
  def method: Method = {
    action.method
  }

  /**
   * Return the method name of this action's method.
   */
  def methodName: String = {
    action.methodName
  }

  /**
   * Return view path of this controller.
   */
  def viewPath: String = {
    action.viewPath
  }
}
