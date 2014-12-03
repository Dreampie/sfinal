package com.sfinal.aop

import com.sfinal.core.ActionInvocation

/**
 * Created by ice on 14-12-3.
 */
trait Interceptor {
  def intercept(ai: ActionInvocation)
}
