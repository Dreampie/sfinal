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

import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.HashMap
import java.util.Map

/**
 * DbKit
 */
@SuppressWarnings(Array("rawtypes")) final object DbKit {
  /**
   * Add Config object
   * @param config the Config contains DataSource, Dialect and so on
   */
  def addConfig(config: Config) {
    if (config == null) throw new IllegalArgumentException("Config can not be null")
    if (configNameToConfig.containsKey(config.getName)) throw new IllegalArgumentException("Config already exists: " + config.getName)
    configNameToConfig.put(config.getName, config)
    if (MAIN_CONFIG_NAME == config.getName) DbKit.config = config
    if (DbKit.config == null) DbKit.config = config
  }

  private[activerecord] def addModelToConfigMapping(modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]], config: Config) {
    modelToConfig.put(modelClass, config)
  }

  def getConfig: Config = {
    return config
  }

  def getConfig(configName: String): Config = {
    return configNameToConfig.get(configName)
  }

  def getConfig(modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]]): Config = {
    return modelToConfig.get(modelClass)
  }

  private[activerecord] final def closeQuietly(rs: ResultSet, st: Statement) {
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
  }

  private[activerecord] final def closeQuietly(st: Statement) {
    if (st != null) {
      try {
        st.close
      }
      catch {
        case e: SQLException => {
        }
      }
    }
  }

  def replaceFormatSqlOrderBy(sql: String): String = {
    sql = sql.replaceAll("(\\s)+", " ")
    val index: Int = sql.toLowerCase.lastIndexOf("order by")
    if (index > sql.toLowerCase.lastIndexOf(")")) {
      val sql1: String = sql.substring(0, index)
      var sql2: String = sql.substring(index)
      sql2 = sql2.replaceAll("[oO][rR][dD][eE][rR] [bB][yY] [\u4e00-\u9fa5a-zA-Z0-9_.]+((\\s)+(([dD][eE][sS][cC])|([aA][sS][cC])))?(( )*,( )*[\u4e00-\u9fa5a-zA-Z0-9_.]+(( )+(([dD][eE][sS][cC])|([aA][sS][cC])))?)*", "")
      return sql1 + sql2
    }
    return sql
  }

  /**
   * The main Config object for system
   */
  private[activerecord] var config: Config = null
  /**
   * For Model.getAttrsMap()/getModifyFlag() and Record.getColumnsMap()
   * while the ActiveRecordPlugin not start or the Exception throws of HashSessionManager.restorSession(..) by Jetty
   */
  private[activerecord] var brokenConfig: Config = new Config
  private var modelToConfig: Map[Class[_ <: Model[_ <: Model[_]]], Config] = new HashMap[Class[_ <: Model[_ <: Model[_]]], Config]
  private var configNameToConfig: Map[String, Config] = new HashMap[String, Config]
  private[activerecord] final val NULL_PARA_ARRAY: Array[AnyRef] = new Array[AnyRef](0)
  final val MAIN_CONFIG_NAME: String = "main"
}

@SuppressWarnings(Array("rawtypes")) final class DbKit {
  private def this() {
    this()
  }
}






