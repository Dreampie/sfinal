package com.sfinal.plugin.activerecord

import com.jfinal.kit.StrKit
import com.jfinal.plugin.activerecord.Config
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.DbKit
import com.jfinal.plugin.activerecord.IContainerFactory
import com.jfinal.plugin.activerecord.IDataSourceProvider
import com.jfinal.plugin.activerecord.Table
import com.jfinal.plugin.activerecord.TableBuilder
import com.jfinal.plugin.activerecord.cache.ICache
import com.jfinal.plugin.activerecord.dialect.Dialect
import com.sfinal.plugin.IPlugin

/**
 * Created by ice on 14-12-3.
 * ActiveRecord plugin.
 * <br>
 * ActiveRecord plugin not support mysql type year, you can use int instead of year.
 * Mysql error message for type year when insert a record: Data truncated for column 'xxx' at row 1
 */
class ActiveRecordPlugin extends IPlugin{
  private var configName: Nothing = DbKit.MAIN_CONFIG_NAME
  private var config: Config = null
  private var dataSource: Nothing = null
  private var dataSourceProvider: IDataSourceProvider = null
  private var transactionLevel: Nothing = null
  private var cache: ICache = null
  private var showSql: Nothing = null
  private var devMode: Nothing = null
  private var dialect: Dialect = null
  private var containerFactory: IContainerFactory = null
  private var isStarted: Boolean = false
  private var tableList: Nothing = new Nothing

  def this(config: Config) {
    this()
    if (config == null) throw new Nothing("Config can not be null")
    this.config = config
  }

  def this(dataSource: Nothing) {
    this()
    this(DbKit.MAIN_CONFIG_NAME, dataSource)
  }

  def this(configName: Nothing, dataSource: Nothing) {
    this()
    `this`(configName, dataSource, Connection.TRANSACTION_READ_COMMITTED)
  }

  def this(dataSource: Nothing, transactionLevel: Int) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSource, transactionLevel)
  }

  def this(configName: Nothing, dataSource: Nothing, transactionLevel: Int) {
    this()
    if (StrKit.isBlank(configName)) throw new Nothing("configName can not be blank")
    if (dataSource == null) throw new Nothing("dataSource can not be null")
    this.configName = configName.trim
    this.dataSource = dataSource
    this.setTransactionLevel(transactionLevel)
  }

  def this(dataSourceProvider: IDataSourceProvider) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSourceProvider)
  }

  def this(configName: Nothing, dataSourceProvider: IDataSourceProvider) {
    this()
    `this`(configName, dataSourceProvider, Connection.TRANSACTION_READ_COMMITTED)
  }

  def this(dataSourceProvider: IDataSourceProvider, transactionLevel: Int) {
    this()
    `this`(DbKit.MAIN_CONFIG_NAME, dataSourceProvider, transactionLevel)
  }

  def this(configName: Nothing, dataSourceProvider: IDataSourceProvider, transactionLevel: Int) {
    this()
    if (StrKit.isBlank(configName)) throw new Nothing("configName can not be blank")
    if (dataSourceProvider == null) throw new Nothing("dataSourceProvider can not be null")
    this.configName = configName.trim
    this.dataSourceProvider = dataSourceProvider
    this.setTransactionLevel(transactionLevel)
  }

  def addMapping(tableName: Nothing, primaryKey: Nothing, modelClass: Nothing): ActiveRecordPlugin = {
    tableList.add(new Table(tableName, primaryKey, modelClass))
    return this
  }

  def addMapping(tableName: Nothing, modelClass: Nothing): ActiveRecordPlugin = {
    tableList.add(new Table(tableName, modelClass))
    return this
  }

  /**
   * Set transaction level define in java.sql.Connection
   * @param transactionLevel only be 0, 1, 2, 4, 8
   */
  def setTransactionLevel(transactionLevel: Int): ActiveRecordPlugin = {
    val t: Int = transactionLevel
    if (t != 0 && t != 1 && t != 2 && t != 4 && t != 8) throw new Nothing("The transactionLevel only be 0, 1, 2, 4, 8")
    this.transactionLevel = transactionLevel
    return this
  }

  def setCache(cache: ICache): ActiveRecordPlugin = {
    if (cache == null) throw new Nothing("cache can not be null")
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

  def getDevMode: Nothing = {
    return devMode
  }

  def setDialect(dialect: Dialect): ActiveRecordPlugin = {
    if (dialect == null) throw new Nothing("dialect can not be null")
    this.dialect = dialect
    return this
  }

  def setContainerFactory(containerFactory: IContainerFactory): ActiveRecordPlugin = {
    if (containerFactory == null) throw new Nothing("containerFactory can not be null")
    this.containerFactory = containerFactory
    return this
  }

  def start: Boolean = {
    if (isStarted) return true
    if (dataSourceProvider != null) dataSource = dataSourceProvider.getDataSource
    if (dataSource == null) throw new Nothing("ActiveRecord start error: ActiveRecordPlugin need DataSource or DataSourceProvider")
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
}
