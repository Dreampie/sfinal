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
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.Set
import javax.sql.DataSource
import com.sfinal.kit.StrKit
import com.sfinal.plugin.activerecord.{SqlReporter, IContainerFactory}
import com.sfinal.plugin.activerecord.cache.EhCache
import com.sfinal.plugin.activerecord.cache.ICache
import com.sfinal.plugin.activerecord.dialect.Dialect
import com.sfinal.plugin.activerecord.dialect.MysqlDialect

class Config {
  /**
   * For DbKit.brokenConfig = new Config();
   */
  private[activerecord] def this() {
    this()
  }

  /**
   * Constructor with DataSource
   * @param dataSource the dataSource, can not be null
   */
  def this(name: String, dataSource: DataSource) {
    this()
    if (StrKit.isBlank(name)) throw new IllegalArgumentException("Config name can not be blank")
    if (dataSource == null) throw new IllegalArgumentException("DataSource can not be null")
    this.name = name.trim
    this.dataSource = dataSource
  }

  /**
   * Constructor with DataSource and Dialect
   * @param dataSource the dataSource, can not be null
   * @param dialect the dialect, can not be null
   */
  def this(name: String, dataSource: DataSource, dialect: Dialect) {
    this()
    if (StrKit.isBlank(name)) throw new IllegalArgumentException("Config name can not be blank")
    if (dataSource == null) throw new IllegalArgumentException("DataSource can not be null")
    if (dialect == null) throw new IllegalArgumentException("Dialect can not be null")
    this.name = name.trim
    this.dataSource = dataSource
    this.dialect = dialect
  }

  /**
   * Constructor with full parameters
   * @param dataSource the dataSource, can not be null
   * @param dialect the dialect, set null with default value: new MysqlDialect()
   * @param showSql the showSql,set null with default value: false
   * @param devMode the devMode, set null with default value: false
   * @param transactionLevel the transaction level, set null with default value: Connection.TRANSACTION_READ_COMMITTED
   * @param containerFactory the containerFactory, set null with default value: new IContainerFactory(){......}
   * @param cache the cache, set null with default value: new EhCache()
   */
  def this(name: String, dataSource: DataSource, dialect: Dialect, showSql: Boolean, devMode: Boolean, transactionLevel: Integer, containerFactory: IContainerFactory, cache: ICache) {
    this()
    if (StrKit.isBlank(name)) throw new IllegalArgumentException("Config name can not be blank")
    if (dataSource == null) throw new IllegalArgumentException("DataSource can not be null")
    this.name = name.trim
    this.dataSource = dataSource
    if (dialect != null) this.dialect = dialect
    if (showSql != null) this.showSql = showSql
    if (devMode != null) this.devMode = devMode
    if (transactionLevel != null) this.transactionLevel = transactionLevel
    if (containerFactory != null) this.containerFactory = containerFactory
    if (cache != null) this.cache = cache
  }

  def getName: String = {
    return name
  }

  def getDialect: Dialect = {
    return dialect
  }

  def getCache: ICache = {
    return cache
  }

  def getTransactionLevel: Int = {
    return transactionLevel
  }

  def getDataSource: DataSource = {
    return dataSource
  }

  def getContainerFactory: IContainerFactory = {
    return containerFactory
  }

  def isShowSql: Boolean = {
    return showSql
  }

  def isDevMode: Boolean = {
    return devMode
  }

  /**
   * Support transaction with Transaction interceptor
   */
  final def setThreadLocalConnection(connection: Connection) {
    threadLocal.set(connection)
  }

  final def removeThreadLocalConnection {
    threadLocal.remove
  }

  /**
   * Get Connection. Support transaction if Connection in ThreadLocal
   */
  final def getConnection: Connection = {
    val conn: Connection = threadLocal.get
    if (conn != null) return conn
    return if (showSql) new SqlReporter(dataSource.getConnection).getConnection else dataSource.getConnection
  }

  /**
   * Helps to implement nested transaction.
   * Tx.intercept(...) and Db.tx(...) need this method to detected if it in nested transaction.
   */
  final def getThreadLocalConnection: Connection = {
    return threadLocal.get
  }

  /**
   * Close ResultSet、Statement、Connection
   * ThreadLocal support declare transaction.
   */
  final def close(rs: ResultSet, st: Statement, conn: Connection) {
    if (rs != null) {
      try {
        rs.close
      }
      catch {
        case e: SQLException => {
        }
      }
    }
    if (st != null) {
      try {
        st.close
      }
      catch {
        case e: SQLException => {
        }
      }
    }
    if (threadLocal.get == null) {
      if (conn != null) {
        try {
          conn.close
        }
        catch {
          case e: SQLException => {
            throw new ActiveRecordException(e)
          }
        }
      }
    }
  }

  final def close(st: Statement, conn: Connection) {
    if (st != null) {
      try {
        st.close
      }
      catch {
        case e: SQLException => {
        }
      }
    }
    if (threadLocal.get == null) {
      if (conn != null) {
        try {
          conn.close
        }
        catch {
          case e: SQLException => {
            throw new ActiveRecordException(e)
          }
        }
      }
    }
  }

  final def close(conn: Connection) {
    if (threadLocal.get == null) if (conn != null) try {
      conn.close
    }
    catch {
      case e: SQLException => {
        throw new ActiveRecordException(e)
      }
    }
  }

  private[activerecord] var name: String = null
  private final val threadLocal: ThreadLocal[Connection] = new ThreadLocal[Connection]
  private[activerecord] var dataSource: DataSource = null
  private[activerecord] var transactionLevel: Int = Connection.TRANSACTION_READ_COMMITTED
  private[activerecord] var cache: ICache = new EhCache
  private[activerecord] var showSql: Boolean = false
  private[activerecord] var devMode: Boolean = false
  private[activerecord] var dialect: Dialect = new MysqlDialect
  private[activerecord] var containerFactory: IContainerFactory = new IContainerFactory {
    def getAttrsMap: Map[String, AnyRef] = {
      return new HashMap[String, AnyRef]
    }

    def getColumnsMap: Map[String, AnyRef] = {
      return new HashMap[String, AnyRef]
    }

    def getModifyFlagSet: Set[String] = {
      return new HashSet[String]
    }
  }
}




