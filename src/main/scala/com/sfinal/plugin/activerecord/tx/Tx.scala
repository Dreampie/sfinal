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
package com.sfinal.plugin.activerecord.tx

import java.sql.Connection
import java.sql.SQLException
import com.sfinal.aop.Interceptor
import com.sfinal.core.ActionInvocation
import com.sfinal.plugin.activerecord._
import com.sfinal.plugin.activerecord.tx.TxConfig

/**
 * ActiveRecord declare transaction.
 * Example: @Before(Tx.class)
 */
object Tx {
  private[tx] def getConfigWithTxConfig(ai: ActionInvocation): Config = {
    var txConfig: TxConfig = ai.getMethod.getAnnotation(classOf[TxConfig])
    if (txConfig == null) txConfig = ai.getController.getClass.getAnnotation(classOf[TxConfig])
    if (txConfig != null) {
      val config: Config = DbKit.getConfig(txConfig.value)
      if (config == null) throw new RuntimeException("Config not found with TxConfig")
      return config
    }
    return null
  }
}

class Tx extends Interceptor {
  protected def getTransactionLevel(config: Config): Int = {
    return config.getTransactionLevel
  }

  def intercept(ai: ActionInvocation) {
    var config: Config = getConfigWithTxConfig(ai)
    if (config == null) config = DbKit.getConfig
    var conn: Connection = config.getThreadLocalConnection
    if (conn != null) {
      try {
        if (conn.getTransactionIsolation < getTransactionLevel(config)) conn.setTransactionIsolation(getTransactionLevel(config))
        ai.invoke
        return
      }
      catch {
        case e: SQLException => {
          throw new ActiveRecordException(e)
        }
      }
    }
    var autoCommit: Boolean = null
    try {
      conn = config.getConnection
      autoCommit = conn.getAutoCommit
      config.setThreadLocalConnection(conn)
      conn.setTransactionIsolation(getTransactionLevel(config))
      conn.setAutoCommit(false)
      ai.invoke
      conn.commit
    }
    catch {
      case e: NestedTransactionHelpException => {
        if (conn != null) try {
          conn.rollback
        }
        catch {
          case e1: Exception => {
            e1.printStackTrace
          }
        }
      }
      case t: Throwable => {
        if (conn != null) try {
          conn.rollback
        }
        catch {
          case e1: Exception => {
            e1.printStackTrace
          }
        }
        throw new ActiveRecordException(t)
      }
    }
    finally {
      try {
        if (conn != null) {
          if (autoCommit != null) conn.setAutoCommit(autoCommit)
          conn.close
        }
      }
      catch {
        case t: Throwable => {
          t.printStackTrace
        }
      }
      finally {
        config.removeThreadLocalConnection
      }
    }
  }
}

/**
 * Reentrance transaction, nested transaction in other words.
 * JFinal decide not to support nested transaction.
 * The code below is help to support nested transact in the future.
private void reentryTx() {
	Connection oldConn = DbKit.getThreadLocalConnection());	// Get connection from threadLocal directly
	Connection conn = null;
	try {
		conn = DbKit.getDataSource().getConnection();
		DbKit.setThreadLocalConnection(conn);
		conn.setTransactionIsolation(getTransactionLevel());	// conn.setTransactionIsolation(transactionLevel);
		conn.setAutoCommit(false);
		// here is service code
		conn.commit();
	} catch (Exception e) {
		if (conn != null)
			try {conn.rollback();} catch (SQLException e1) {e1.printStackTrace();}
		throw new ActiveRecordException(e);
	}
	finally {
		try {
			if (conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();	// can not throw exception here, otherwise the more important exception in catch block can not be throw.
		}
		finally {
			if (oldConn != null)
				DbKit.setThreadLocalConnection(oldConn);
			else
				DbKit.removeThreadLocalConnection();	// prevent memory leak
		}
	}
}*/




