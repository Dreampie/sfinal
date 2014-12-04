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
package com.sfinal.plugin.druid

import java.sql.SQLException
import java.util.ArrayList
import java.util.List
import javax.sql.DataSource
import com.alibaba.druid.filter.Filter
import com.alibaba.druid.pool.DruidDataSource
import com.sfinal.kit.StrKit
import com.sfinal.plugin.IPlugin
import com.sfinal.plugin.activerecord.IDataSourceProvider

/**
 * DruidPlugin.
 */
class DruidPlugin extends IPlugin with IDataSourceProvider {
  def this(url: String, username: String, password: String) {
    this()
    this.url = url
    this.username = username
    this.password = password
  }

  def this(url: String, username: String, password: String, driverClass: String) {
    this()
    this.url = url
    this.username = username
    this.password = password
    this.driverClass = driverClass
  }

  def this(url: String, username: String, password: String, driverClass: String, filters: String) {
    this()
    this.url = url
    this.username = username
    this.password = password
    this.driverClass = driverClass
    this.filters = filters
  }

  /**
   * 设置过滤器，如果要开启监控统计需要使用此方法或在构造方法中进行设置
   * <p>
   * 监控统计："stat"
   * 防SQL注入："wall"
   * 组合使用： "stat,wall"
   * </p>
   */
  def setFilters(filters: String): DruidPlugin = {
    this.filters = filters
    return this
  }

  def addFilter(filter: Filter): DruidPlugin = {
    if (filterList == null) filterList = new ArrayList[Filter]
    filterList.add(filter)
    return this
  }

  def start: Boolean = {
    ds = new DruidDataSource
    ds.setUrl(url)
    ds.setUsername(username)
    ds.setPassword(password)
    if (driverClass != null) ds.setDriverClassName(driverClass)
    ds.setInitialSize(initialSize)
    ds.setMinIdle(minIdle)
    ds.setMaxActive(maxActive)
    ds.setMaxWait(maxWait)
    ds.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis)
    ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis)
    ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis)
    ds.setValidationQuery(validationQuery)
    ds.setTestWhileIdle(testWhileIdle)
    ds.setTestOnBorrow(testOnBorrow)
    ds.setTestOnReturn(testOnReturn)
    ds.setRemoveAbandoned(removeAbandoned)
    ds.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis)
    ds.setLogAbandoned(logAbandoned)
    ds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize)
    if (StrKit.notBlank(filters)) try {
      ds.setFilters(filters)
    }
    catch {
      case e: SQLException => {
        throw new RuntimeException(e)
      }
    }
    addFilterList(ds)
    return true
  }

  private def addFilterList(ds: DruidDataSource) {
    if (filterList != null) {
      val targetList: List[Filter] = ds.getProxyFilters
      import scala.collection.JavaConversions._
      for (add <- filterList) {
        var found: Boolean = false
        import scala.collection.JavaConversions._
        for (target <- targetList) {
          if (add.getClass == target.getClass) {
            found = true
            break //todo: break is not supported
          }
        }
        if (!found) targetList.add(add)
      }
    }
  }

  def stop: Boolean = {
    if (ds != null) ds.close
    return true
  }

  def getDataSource: DataSource = {
    return ds
  }

  def set(initialSize: Int, minIdle: Int, maxActive: Int): DruidPlugin = {
    this.initialSize = initialSize
    this.minIdle = minIdle
    this.maxActive = maxActive
    return this
  }

  def setDriverClass(driverClass: String): DruidPlugin = {
    this.driverClass = driverClass
    return this
  }

  def setInitialSize(initialSize: Int): DruidPlugin = {
    this.initialSize = initialSize
    return this
  }

  def setMinIdle(minIdle: Int): DruidPlugin = {
    this.minIdle = minIdle
    return this
  }

  def setMaxActive(maxActive: Int): DruidPlugin = {
    this.maxActive = maxActive
    return this
  }

  def setMaxWait(maxWait: Long): DruidPlugin = {
    this.maxWait = maxWait
    return this
  }

  def setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis: Long): DruidPlugin = {
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis
    return this
  }

  def setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis: Long): DruidPlugin = {
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis
    return this
  }

  /**
   * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
   * Oracle - "select 1 from dual"
   * DB2 - "select 1 from sysibm.sysdummy1"
   * mysql - "select 1"
   */
  def setValidationQuery(validationQuery: String): DruidPlugin = {
    this.validationQuery = validationQuery
    return this
  }

  def setTestWhileIdle(testWhileIdle: Boolean): DruidPlugin = {
    this.testWhileIdle = testWhileIdle
    return this
  }

  def setTestOnBorrow(testOnBorrow: Boolean): DruidPlugin = {
    this.testOnBorrow = testOnBorrow
    return this
  }

  def setTestOnReturn(testOnReturn: Boolean): DruidPlugin = {
    this.testOnReturn = testOnReturn
    return this
  }

  def setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize: Int): DruidPlugin = {
    this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize
    return this
  }

  final def setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis: Long) {
    this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis
  }

  final def setRemoveAbandoned(removeAbandoned: Boolean) {
    this.removeAbandoned = removeAbandoned
  }

  final def setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis: Long) {
    this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis
  }

  final def setLogAbandoned(logAbandoned: Boolean) {
    this.logAbandoned = logAbandoned
  }

  private var url: String = null
  private var username: String = null
  private var password: String = null
  private var driverClass: String = null
  private var initialSize: Int = 10
  private var minIdle: Int = 10
  private var maxActive: Int = 100
  private var maxWait: Long = DruidDataSource.DEFAULT_MAX_WAIT
  private var timeBetweenEvictionRunsMillis: Long = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS
  private var minEvictableIdleTimeMillis: Long = DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS
  private var timeBetweenConnectErrorMillis: Long = DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS
  /**
   * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
   * Oracle - "select 1 from dual"
   * DB2 - "select 1 from sysibm.sysdummy1"
   * mysql - "select 1"
   */
  private var validationQuery: String = "select 1"
  private var testWhileIdle: Boolean = true
  private var testOnBorrow: Boolean = false
  private var testOnReturn: Boolean = false
  private var removeAbandoned: Boolean = false
  private var removeAbandonedTimeoutMillis: Long = 300 * 1000
  private var logAbandoned: Boolean = false
  private var maxPoolPreparedStatementPerConnectionSize: Int = -1
  private var filters: String = null
  private var filterList: List[Filter] = null
  private var ds: DruidDataSource = null
}

