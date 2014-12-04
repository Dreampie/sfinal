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

import java.util.Enumeration
import java.util.HashMap
import java.util.Map
import javax.servlet.ServletContext
import javax.servlet.http.HttpSession
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.core.Controller

/**
 * SessionInViewInterceptor.
 */
class SessionInViewInterceptor extends Interceptor {
  def this() {
    this()
  }

  def this(createSession: Boolean) {
    this()
    this.createSession = createSession
  }

  @SuppressWarnings(Array("rawtypes", "unchecked")) def intercept(ai: ActionInvocation) {
    ai.invoke
    val c: Controller = ai.getController
    if (c.getRender.isInstanceOf[JsonRender]) return
    val hs: HttpSession = c.getSession(createSession)
    if (hs != null) {
      val session: Map[_, _] = new JFinalSession(hs)
      {
        val names: Enumeration[String] = hs.getAttributeNames
        while (names.hasMoreElements) {
          val name: String = names.nextElement
          session.put(name, hs.getAttribute(name))
        }
      }
      c.setAttr("session", session)
    }
  }

  private var createSession: Boolean = false
}

@SuppressWarnings(Array("rawtypes", "deprecation")) object JFinalSession {
  private final val serialVersionUID: Long = -6148316613614087335L
}

@SuppressWarnings(Array("rawtypes", "deprecation")) class JFinalSession extends HashMap with HttpSession {
  def this(session: HttpSession) {
    this()
    this.session = session
  }

  def getAttribute(key: String): AnyRef = {
    return session.getAttribute(key)
  }

  @SuppressWarnings(Array("unchecked")) def getAttributeNames: Enumeration[_] = {
    return session.getAttributeNames
  }

  def getCreationTime: Long = {
    return session.getCreationTime
  }

  def getId: String = {
    return session.getId
  }

  def getLastAccessedTime: Long = {
    return session.getLastAccessedTime
  }

  def getMaxInactiveInterval: Int = {
    return session.getMaxInactiveInterval
  }

  def getServletContext: ServletContext = {
    return session.getServletContext
  }

  def getSessionContext: HttpSessionContext = {
    return session.getSessionContext
  }

  def getValue(key: String): AnyRef = {
    return session.getValue(key)
  }

  def getValueNames: Array[String] = {
    return session.getValueNames
  }

  def invalidate {
    session.invalidate
  }

  def isNew: Boolean = {
    return session.isNew
  }

  def putValue(key: String, value: AnyRef) {
    session.putValue(key, value)
  }

  def removeAttribute(key: String) {
    session.removeAttribute(key)
  }

  def removeValue(key: String) {
    session.removeValue(key)
  }

  def setAttribute(key: String, value: AnyRef) {
    session.setAttribute(key, value)
  }

  def setMaxInactiveInterval(maxInactiveInterval: Int) {
    session.setMaxInactiveInterval(maxInactiveInterval)
  }

  private var session: HttpSession = null
}

/*
public void intercept(ActionInvocation ai) {
	ai.invoke();
	
	Controller c = ai.getController();
	HttpSession hs = c.getSession(createSession);
	if (hs != null) {
		c.setAttr("session", new JFinalSession(hs));
	}
}
*/

