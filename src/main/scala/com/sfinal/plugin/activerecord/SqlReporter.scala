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
package com.sfinal.plugin.activerecord

import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.Connection
import com.sfinal.log.Logger
import com.sfinal.log.Logger

/**
 * SqlReporter.
 */
object SqlReporter {
  def setLogger(on: Boolean) {
    SqlReporter.loggerOn = on
  }

  private var loggerOn: Boolean = false
  private final val log: Logger = Logger.getLogger(classOf[SqlReporter])
}

class SqlReporter extends InvocationHandler {
  private[activerecord] def this(conn: Connection) {
    this()
    this.conn = conn
  }

  @SuppressWarnings(Array("rawtypes")) private[activerecord] def getConnection: Connection = {
    val clazz: Class[_] = conn.getClass
    return Proxy.newProxyInstance(clazz.getClassLoader, Array[Class[_]](classOf[Connection]), this).asInstanceOf[Connection]
  }

  def invoke(proxy: AnyRef, method: Method, args: Array[AnyRef]): AnyRef = {
    try {
      if (method.getName == "prepareStatement") {
        val info: String = "Sql: " + args(0)
        if (loggerOn) log.info(info)
        else System.out.println(info)
      }
      return method.invoke(conn, args)
    }
    catch {
      case e: InvocationTargetException => {
        throw e.getTargetException
      }
    }
  }

  private var conn: Connection = null
}





