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

import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import com.sfinal.plugin.activerecord.Record
import com.sfinal.plugin.activerecord.Table

/**
 * OracleDialect.
 */
class OracleDialect extends Dialect {
  def forTableBuilderDoBuild(tableName: String): String = {
    return "select * from " + tableName + " where rownum < 1"
  }

  def forModelSave(table: Table, attrs: Map[String, AnyRef], sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("insert into ").append(table.getName).append("(")
    val temp: StringBuilder = new StringBuilder(") values(")
    val pKey: String = table.getPrimaryKey
    var count: Int = 0
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      val colName: String = e.getKey
      if (table.hasColumnLabel(colName)) {
        if (({
          count += 1; count - 1
        }) > 0) {
          sql.append(", ")
          temp.append(", ")
        }
        sql.append(colName)
        val value: AnyRef = e.getValue
        if (value.isInstanceOf[String] && colName.equalsIgnoreCase(pKey) && (value.asInstanceOf[String]).endsWith(".nextval")) {
          temp.append(value)
        }
        else {
          temp.append("?")
          paras.add(value)
        }
      }
    }
    sql.append(temp.toString).append(")")
  }

  def forModelDeleteById(table: Table): String = {
    val pKey: String = table.getPrimaryKey
    val sql: StringBuilder = new StringBuilder(45)
    sql.append("delete from ")
    sql.append(table.getName)
    sql.append(" where ").append(pKey).append(" = ?")
    return sql.toString
  }

  def forModelUpdate(table: Table, attrs: Map[String, AnyRef], modifyFlag: Set[String], pKey: String, id: AnyRef, sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("update ").append(table.getName).append(" set ")
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      val colName: String = e.getKey
      if (!pKey.equalsIgnoreCase(colName) && modifyFlag.contains(colName) && table.hasColumnLabel(colName)) {
        if (paras.size > 0) sql.append(", ")
        sql.append(colName).append(" = ? ")
        paras.add(e.getValue)
      }
    }
    sql.append(" where ").append(pKey).append(" = ?")
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
            sql.append(columnsArray(i).trim)
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    sql.append(" from ")
    sql.append(table.getName)
    sql.append(" where ").append(table.getPrimaryKey).append(" = ?")
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
            sql.append(columnsArray(i).trim)
          }
          ({
            i += 1; i - 1
          })
        }
      }
    }
    sql.append(" from ")
    sql.append(tableName.trim)
    sql.append(" where ").append(primaryKey).append(" = ?")
    return sql.toString
  }

  def forDbDeleteById(tableName: String, primaryKey: String): String = {
    val sql: StringBuilder = new StringBuilder("delete from ")
    sql.append(tableName.trim)
    sql.append(" where ").append(primaryKey).append(" = ?")
    return sql.toString
  }

  def forDbSave(sql: StringBuilder, paras: List[AnyRef], tableName: String, record: Record) {
    sql.append("insert into ")
    sql.append(tableName.trim).append("(")
    val temp: StringBuilder = new StringBuilder
    temp.append(") values(")
    var count: Int = 0
    import scala.collection.JavaConversions._
    for (e <- record.getColumns.entrySet) {
      if (({
        count += 1; count - 1
      }) > 0) {
        sql.append(", ")
        temp.append(", ")
      }
      sql.append(e.getKey)
      val value: AnyRef = e.getValue
      if (value.isInstanceOf[String] && ((value.asInstanceOf[String]).endsWith(".nextval"))) {
        temp.append(value)
      }
      else {
        temp.append("?")
        paras.add(value)
      }
    }
    sql.append(temp.toString).append(")")
  }

  def forDbUpdate(tableName: String, primaryKey: String, id: AnyRef, record: Record, sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("update ").append(tableName.trim).append(" set ")
    import scala.collection.JavaConversions._
    for (e <- record.getColumns.entrySet) {
      val colName: String = e.getKey
      if (!primaryKey.equalsIgnoreCase(colName)) {
        if (paras.size > 0) {
          sql.append(", ")
        }
        sql.append(colName).append(" = ? ")
        paras.add(e.getValue)
      }
    }
    sql.append(" where ").append(primaryKey).append(" = ?")
    paras.add(id)
  }

  def forPaginate(sql: StringBuilder, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String) {
    val satrt: Int = (pageNumber - 1) * pageSize + 1
    val end: Int = pageNumber * pageSize
    sql.append("select * from ( select row_.*, rownum rownum_ from (  ")
    sql.append(select).append(" ").append(sqlExceptSelect)
    sql.append(" ) row_ where rownum <= ").append(end).append(") table_alias")
    sql.append(" where table_alias.rownum_ >= ").append(satrt)
  }

  override def isOracle: Boolean = {
    return true
  }

  override def fillStatement(pst: PreparedStatement, paras: List[AnyRef]) {
    {
      var i: Int = 0
      val size: Int = paras.size
      while (i < size) {
        {
          val value: AnyRef = paras.get(i)
          if (value.isInstanceOf[Date]) pst.setDate(i + 1, value.asInstanceOf[Date])
          else pst.setObject(i + 1, value)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  override def fillStatement(pst: PreparedStatement, paras: AnyRef*) {
    {
      var i: Int = 0
      while (i < paras.length) {
        {
          val value: AnyRef = paras(i)
          if (value.isInstanceOf[Date]) pst.setDate(i + 1, value.asInstanceOf[Date])
          else if (value.isInstanceOf[Timestamp]) pst.setTimestamp(i + 1, value.asInstanceOf[Timestamp])
          else pst.setObject(i + 1, value)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  override def getDefaultPrimaryKey: String = {
    return "ID"
  }
}

