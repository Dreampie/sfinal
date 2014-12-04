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
package com.sfinal.plugin.activerecord.dialect

import java.util.List
import java.util.Map
import java.util.Set
import java.util.Map.Entry
import com.sfinal.plugin.activerecord.Record
import com.sfinal.plugin.activerecord.Table

/**
 * MysqlDialect.
 */
class MysqlDialect extends Dialect {
  def forTableBuilderDoBuild(tableName: String): String = {
    return "select * from `" + tableName + "` where 1 = 2"
  }

  def forModelSave(table: Table, attrs: Map[String, AnyRef], sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("insert into `").append(table.getName).append("`(")
    val temp: StringBuilder = new StringBuilder(") values(")
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      val colName: String = e.getKey
      if (table.hasColumnLabel(colName)) {
        if (paras.size > 0) {
          sql.append(", ")
          temp.append(", ")
        }
        sql.append("`").append(colName).append("`")
        temp.append("?")
        paras.add(e.getValue)
      }
    }
    sql.append(temp.toString).append(")")
  }

  def forModelDeleteById(table: Table): String = {
    val primaryKey: String = table.getPrimaryKey
    val sql: StringBuilder = new StringBuilder(45)
    sql.append("delete from `")
    sql.append(table.getName)
    sql.append("` where `").append(primaryKey).append("` = ?")
    return sql.toString
  }

  def forModelUpdate(table: Table, attrs: Map[String, AnyRef], modifyFlag: Set[String], primaryKey: String, id: AnyRef, sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("update `").append(table.getName).append("` set ")
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      val colName: String = e.getKey
      if (!primaryKey.equalsIgnoreCase(colName) && modifyFlag.contains(colName) && table.hasColumnLabel(colName)) {
        if (paras.size > 0) sql.append(", ")
        sql.append("`").append(colName).append("` = ? ")
        paras.add(e.getValue)
      }
    }
    sql.append(" where `").append(primaryKey).append("` = ?")
    paras.add(id)
  }

  def forModelFindById(table: Table, columns: String): String = {
    val sql: StringBuilder = new StringBuilder("select ")
    if (columns.trim == "*") {
      sql.append(columns)
    }
    else {
      val columnsArray: Array[String] = columns.split(",")
      {
        var i: Int = 0
        while (i < columnsArray.length) {
          {
            if (i > 0) sql.append(", ")
            sql.append("`").append(columnsArray(i).trim).append("`")
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    sql.append(" from `")
    sql.append(table.getName)
    sql.append("` where `").append(table.getPrimaryKey).append("` = ?")
    return sql.toString
  }

  def forDbFindById(tableName: String, primaryKey: String, columns: String): String = {
    val sql: StringBuilder = new StringBuilder("select ")
    if (columns.trim == "*") {
      sql.append(columns)
    }
    else {
      val columnsArray: Array[String] = columns.split(",")
      {
        var i: Int = 0
        while (i < columnsArray.length) {
          {
            if (i > 0) sql.append(", ")
            sql.append("`").append(columnsArray(i).trim).append("`")
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    sql.append(" from `")
    sql.append(tableName.trim)
    sql.append("` where `").append(primaryKey).append("` = ?")
    return sql.toString
  }

  def forDbDeleteById(tableName: String, primaryKey: String): String = {
    val sql: StringBuilder = new StringBuilder("delete from `")
    sql.append(tableName.trim)
    sql.append("` where `").append(primaryKey).append("` = ?")
    return sql.toString
  }

  def forDbSave(sql: StringBuilder, paras: List[AnyRef], tableName: String, record: Record) {
    sql.append("insert into `")
    sql.append(tableName.trim).append("`(")
    val temp: StringBuilder = new StringBuilder
    temp.append(") values(")
    import scala.collection.JavaConversions._
    for (e <- record.getColumns.entrySet) {
      if (paras.size > 0) {
        sql.append(", ")
        temp.append(", ")
      }
      sql.append("`").append(e.getKey).append("`")
      temp.append("?")
      paras.add(e.getValue)
    }
    sql.append(temp.toString).append(")")
  }

  def forDbUpdate(tableName: String, primaryKey: String, id: AnyRef, record: Record, sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("update `").append(tableName.trim).append("` set ")
    import scala.collection.JavaConversions._
    for (e <- record.getColumns.entrySet) {
      val colName: String = e.getKey
      if (!primaryKey.equalsIgnoreCase(colName)) {
        if (paras.size > 0) {
          sql.append(", ")
        }
        sql.append("`").append(colName).append("` = ? ")
        paras.add(e.getValue)
      }
    }
    sql.append(" where `").append(primaryKey).append("` = ?")
    paras.add(id)
  }

  def forPaginate(sql: StringBuilder, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String) {
    val offset: Int = pageSize * (pageNumber - 1)
    sql.append(select).append(" ")
    sql.append(sqlExceptSelect)
    sql.append(" limit ").append(offset).append(", ").append(pageSize)
  }
}

