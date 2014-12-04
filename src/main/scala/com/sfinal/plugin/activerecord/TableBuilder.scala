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
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
import java.util.List

import com.sfinal.plugin.activerecord.TableMapping

/**
 * TableBuilder build the mapping of model between class and table.
 */
object TableBuilder {
  private[activerecord] def build(tableList: List[Table], config: Config): Boolean = {
    var temp: Table = null
    var conn: Connection = null
    try {
      conn = config.dataSource.getConnection
      val tableMapping: TableMapping = TableMapping.me
      import scala.collection.JavaConversions._
      for (table <- tableList) {
        temp = table
        doBuild(table, conn, config)
        tableMapping.putTable(table)
        DbKit.addModelToConfigMapping(table.getModelClass, config)
      }
      return true
    }
    catch {
      case e: Exception => {
        if (temp != null) System.err.println("Can not create Table object, maybe the table " + temp.getName + " is not exists.")
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  @SuppressWarnings(Array("unchecked")) private def doBuild(table: Table, conn: Connection, config: Config) {
    table.setColumnTypeMap(config.containerFactory.getAttrsMap)
    if (table.getPrimaryKey == null) table.setPrimaryKey(config.dialect.getDefaultPrimaryKey)
    val sql: String = config.dialect.forTableBuilderDoBuild(table.getName)
    val stm: Statement = conn.createStatement
    val rs: ResultSet = stm.executeQuery(sql)
    val rsmd: ResultSetMetaData = rs.getMetaData
    {
      var i: Int = 1
      while (i <= rsmd.getColumnCount) {
        {
          val colName: String = rsmd.getColumnName(i)
          val colClassName: String = rsmd.getColumnClassName(i)
          if ("java.lang.String" == colClassName) {
            table.setColumnType(colName, classOf[String])
          }
          else if ("java.lang.Integer" == colClassName) {
            table.setColumnType(colName, classOf[Integer])
          }
          else if ("java.lang.Long" == colClassName) {
            table.setColumnType(colName, classOf[Long])
          }
          else if ("java.sql.Date" == colClassName) {
            table.setColumnType(colName, classOf[Date])
          }
          else if ("java.lang.Double" == colClassName) {
            table.setColumnType(colName, classOf[Double])
          }
          else if ("java.lang.Float" == colClassName) {
            table.setColumnType(colName, classOf[Float])
          }
          else if ("java.lang.Boolean" == colClassName) {
            table.setColumnType(colName, classOf[Boolean])
          }
          else if ("java.sql.Time" == colClassName) {
            table.setColumnType(colName, classOf[Time])
          }
          else if ("java.sql.Timestamp" == colClassName) {
            table.setColumnType(colName, classOf[Timestamp])
          }
          else if ("java.math.BigDecimal" == colClassName) {
            table.setColumnType(colName, classOf[BigDecimal])
          }
          else if ("[B" == colClassName) {
            table.setColumnType(colName, classOf[Array[Byte]])
          }
          else {
            val `type`: Int = rsmd.getColumnType(i)
            if (`type` == Types.BLOB) {
              table.setColumnType(colName, classOf[Array[Byte]])
            }
            else if (`type` == Types.CLOB || `type` == Types.NCLOB) {
              table.setColumnType(colName, classOf[String])
            }
            else {
              table.setColumnType(colName, classOf[String])
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    rs.close
    stm.close
  }
}


