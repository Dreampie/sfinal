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
import java.util.ArrayList
import java.util.List
import javax.sql.DataSource
import com.sfinal.kit.StrKit
import com.sfinal.plugin.IPlugin
import com.sfinal.plugin.activerecord._
import com.sfinal.plugin.activerecord.cache.ICache
import com.sfinal.plugin.activerecord.dialect.Dialect

/**
 * ActiveRecord plugin.
 * <br>
 * ActiveRecord plugin not support mysql type year, you can use int instead of year. 
 * Mysql error message for type year when insert a record: Data truncated for column 'xxx' at row 1
 */
class ActiveRecordPlugin extends IPlugin {
  def this(config: Config) {
    this()
    if (config == null) throw new IllegalArgumentException("Config can not be null")
    this.config = config
  }

  def this(dataSource: DataSource) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSource)
  }

  def this(configName: String, dataSource: DataSource) {
    this()
    `this`(configName, dataSource, Connection.TRANSACTION_READ_COMMITTED)
  }

  def this(dataSource: DataSource, transactionLevel: Int) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSource, transactionLevel)
  }

  def this(configName: String, dataSource: DataSource, transactionLevel: Int) {
    this()
    if (StrKit.isBlank(configName)) throw new IllegalArgumentException("configName can not be blank")
    if (dataSource == null) throw new IllegalArgumentException("dataSource can not be null")
    this.configName = configName.trim
    this.dataSource = dataSource
    this.setTransactionLevel(transactionLevel)
  }

  def this(dataSourceProvider: IDataSourceProvider) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSourceProvider)
  }

  def this(configName: String, dataSourceProvider: IDataSourceProvider) {
    this()
    `this`(configName, dataSourceProvider, Connection.TRANSACTION_READ_COMMITTED)
  }

  def this(dataSourceProvider: IDataSourceProvider, transactionLevel: Int) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSourceProvider, transactionLevel)
  }

  def this(configName: String, dataSourceProvider: IDataSourceProvider, transactionLevel: Int) {
    this()
    if (StrKit.isBlank(configName)) throw new IllegalArgumentException("configName can not be blank")
    if (dataSourceProvider == null) throw new IllegalArgumentException("dataSourceProvider can not be null")
    this.configName = configName.trim
    this.dataSourceProvider = dataSourceProvider
    this.setTransactionLevel(transactionLevel)
  }

  def addMapping(tableName: String, primaryKey: String, modelClass: Class[_ <: Model[_]]): ActiveRecordPlugin = {
    tableList.add(new Table(tableName, primaryKey, modelClass))
    return this
  }

  def addMapping(tableName: String, modelClass: Class[_ <: Model[_]]): ActiveRecordPlugin = {
    tableList.add(new Table(tableName, modelClass))
    return this
  }

  /**
   * Set transaction level define in java.sql.Connection
   * @param transactionLevel only be 0, 1, 2, 4, 8
   */
  def setTransactionLevel(transactionLevel: Int): ActiveRecordPlugin = {
    val t: Int = transactionLevel
    if (t != 0 && t != 1 && t != 2 && t != 4 && t != 8) throw new IllegalArgumentException("The transactionLevel only be 0, 1, 2, 4, 8")
    this.transactionLevel = transactionLevel
    return this
  }

  def setCache(cache: ICache): ActiveRecordPlugin = {
    if (cache == null) throw new IllegalArgumentException("cache can not be null")
    this.cache = cache
    return this
  }

  def setShowSql(showSql: Boolean): ActiveRecordPlugin = {
    this.showSql = showSql
    return this
  }

  def setDevMode(devMode: Boolean): ActiveRecordPlugin = {
    this.devMode = devMode
    return this
  }

  def getDevMode: Boolean = {
    return devMode
  }

  def setDialect(dialect: Dialect): ActiveRecordPlugin = {
    if (dialect == null) throw new IllegalArgumentException("dialect can not be null")
    this.dialect = dialect
    return this
  }

  def setContainerFactory(containerFactory: IContainerFactory): ActiveRecordPlugin = {
    if (containerFactory == null) throw new IllegalArgumentException("containerFactory can not be null")
    this.containerFactory = containerFactory
    return this
  }

  def start: Boolean = {
    if (isStarted) return true
    if (dataSourceProvider != null) dataSource = dataSourceProvider.getDataSource
    if (dataSource == null) throw new RuntimeException("ActiveRecord start error: ActiveRecordPlugin need DataSource or DataSourceProvider")
    if (config == null) config = new Config(configName, dataSource, dialect, showSql, devMode, transactionLevel, containerFactory, cache)
    DbKit.addConfig(config)
    val succeed: Boolean = TableBuilder.build(tableList, config)
    if (succeed) {
      Db.init
      isStarted = true
    }
    return succeed
  }

  def stop: Boolean = {
    isStarted = false
    return true
  }

  private var configName: String = DbKit.MAIN_CONFIG_NAME
  private var config: Config = null
  private var dataSource: DataSource = null
  private var dataSourceProvider: IDataSourceProvider = null
  private var transactionLevel: Integer = null
  private var cache: ICache = null
  private var showSql: Boolean = null
  private var devMode: Boolean = null
  private var dialect: Dialect = null
  private var containerFactory: IContainerFactory = null
  private var isStarted: Boolean = false
  private var tableList: List[Table] = new ArrayList[Table]
}








