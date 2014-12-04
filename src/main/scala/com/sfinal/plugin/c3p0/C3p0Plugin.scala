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
package com.sfinal.plugin.c3p0

import java.beans.PropertyVetoException
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties
import javax.sql.DataSource
import com.sfinal.kit.StrKit
import com.sfinal.plugin.IPlugin
import com.sfinal.plugin.activerecord.IDataSourceProvider
import com.mchange.v2.c3p0.ComboPooledDataSource
import com.mchange.v2.c3p0.ComboPooledDataSource

/**
 * The c3p0 datasource plugin.
 */
class C3p0Plugin extends IPlugin with IDataSourceProvider {
  def setDriverClass(driverClass: String): C3p0Plugin = {
    if (StrKit.isBlank(driverClass)) throw new IllegalArgumentException("driverClass can not be blank.")
    this.driverClass = driverClass
    return this
  }

  def setMaxPoolSize(maxPoolSize: Int): C3p0Plugin = {
    if (maxPoolSize < 1) throw new IllegalArgumentException("maxPoolSize must more than 0.")
    this.maxPoolSize = maxPoolSize
    return this
  }

  def setMinPoolSize(minPoolSize: Int): C3p0Plugin = {
    if (minPoolSize < 1) throw new IllegalArgumentException("minPoolSize must more than 0.")
    this.minPoolSize = minPoolSize
    return this
  }

  def setInitialPoolSize(initialPoolSize: Int): C3p0Plugin = {
    if (initialPoolSize < 1) throw new IllegalArgumentException("initialPoolSize must more than 0.")
    this.initialPoolSize = initialPoolSize
    return this
  }

  def setMaxIdleTime(maxIdleTime: Int): C3p0Plugin = {
    if (maxIdleTime < 1) throw new IllegalArgumentException("maxIdleTime must more than 0.")
    this.maxIdleTime = maxIdleTime
    return this
  }

  def setAcquireIncrement(acquireIncrement: Int): C3p0Plugin = {
    if (acquireIncrement < 1) throw new IllegalArgumentException("acquireIncrement must more than 0.")
    this.acquireIncrement = acquireIncrement
    return this
  }

  def this(jdbcUrl: String, user: String, password: String) {
    this()
    this.jdbcUrl = jdbcUrl
    this.user = user
    this.password = password
  }

  def this(jdbcUrl: String, user: String, password: String, driverClass: String) {
    this()
    this.jdbcUrl = jdbcUrl
    this.user = user
    this.password = password
    this.driverClass = if (driverClass != null) driverClass else this.driverClass
  }

  def this(jdbcUrl: String, user: String, password: String, driverClass: String, maxPoolSize: Integer, minPoolSize: Integer, initialPoolSize: Integer, maxIdleTime: Integer, acquireIncrement: Integer) {
    this()
    initC3p0Properties(jdbcUrl, user, password, driverClass, maxPoolSize, minPoolSize, initialPoolSize, maxIdleTime, acquireIncrement)
  }

  private def initC3p0Properties(jdbcUrl: String, user: String, password: String, driverClass: String, maxPoolSize: Integer, minPoolSize: Integer, initialPoolSize: Integer, maxIdleTime: Integer, acquireIncrement: Integer) {
    this.jdbcUrl = jdbcUrl
    this.user = user
    this.password = password
    this.driverClass = if (driverClass != null) driverClass else this.driverClass
    this.maxPoolSize = if (maxPoolSize != null) maxPoolSize else this.maxPoolSize
    this.minPoolSize = if (minPoolSize != null) minPoolSize else this.minPoolSize
    this.initialPoolSize = if (initialPoolSize != null) initialPoolSize else this.initialPoolSize
    this.maxIdleTime = if (maxIdleTime != null) maxIdleTime else this.maxIdleTime
    this.acquireIncrement = if (acquireIncrement != null) acquireIncrement else this.acquireIncrement
  }

  def this(propertyfile: File) {
    this()
    var fis: FileInputStream = null
    try {
      fis = new FileInputStream(propertyfile)
      val ps: Properties = new Properties
      ps.load(fis)
      initC3p0Properties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"), toInt(ps.getProperty("maxPoolSize")), toInt(ps.getProperty("minPoolSize")), toInt(ps.getProperty("initialPoolSize")), toInt(ps.getProperty("maxIdleTime")), toInt(ps.getProperty("acquireIncrement")))
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    finally {
      if (fis != null) try {
        fis.close
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
  }

  def this(properties: Properties) {
    this()
    val ps: Properties = properties
    initC3p0Properties(ps.getProperty("jdbcUrl"), ps.getProperty("user"), ps.getProperty("password"), ps.getProperty("driverClass"), toInt(ps.getProperty("maxPoolSize")), toInt(ps.getProperty("minPoolSize")), toInt(ps.getProperty("initialPoolSize")), toInt(ps.getProperty("maxIdleTime")), toInt(ps.getProperty("acquireIncrement")))
  }

  def start: Boolean = {
    dataSource = new ComboPooledDataSource
    dataSource.setJdbcUrl(jdbcUrl)
    dataSource.setUser(user)
    dataSource.setPassword(password)
    try {
      dataSource.setDriverClass(driverClass)
    }
    catch {
      case e: PropertyVetoException => {
        dataSource = null
        System.err.println("C3p0Plugin start error")
        throw new RuntimeException(e)
      }
    }
    dataSource.setMaxPoolSize(maxPoolSize)
    dataSource.setMinPoolSize(minPoolSize)
    dataSource.setInitialPoolSize(initialPoolSize)
    dataSource.setMaxIdleTime(maxIdleTime)
    dataSource.setAcquireIncrement(acquireIncrement)
    return true
  }

  private def toInt(str: String): Integer = {
    return Integer.parseInt(str)
  }

  def getDataSource: DataSource = {
    return dataSource
  }

  def stop: Boolean = {
    if (dataSource != null) dataSource.close
    return true
  }

  private var jdbcUrl: String = null
  private var user: String = null
  private var password: String = null
  private var driverClass: String = "com.mysql.jdbc.Driver"
  private var maxPoolSize: Int = 100
  private var minPoolSize: Int = 10
  private var initialPoolSize: Int = 10
  private var maxIdleTime: Int = 20
  private var acquireIncrement: Int = 2
  private var dataSource: ComboPooledDataSource = null
}


