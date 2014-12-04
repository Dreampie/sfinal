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

import java.sql.Connection
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation

/**
 * One Connection Per Thread for one request.<br>
 * warning: can not use this interceptor with transaction feature like Tx, Db.tx(...)
 */
class OneConnectionPerThread extends Interceptor {
  def intercept(invocation: ActionInvocation) {
    var conn: Connection = null
    try {
      conn = DbKit.config.getConnection
      DbKit.config.setThreadLocalConnection(conn)
      invocation.invoke
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
    finally {
      DbKit.config.removeThreadLocalConnection
      DbKit.config.close(conn)
    }
  }
}

