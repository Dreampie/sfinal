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

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException
import java.sql.Types
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.Map.Entry
import java.util.Set
import com.sfinal.plugin.activerecord._
import com.sfinal.plugin.activerecord.dialect.Dialect

/**
 * AnsiSqlDialect. Try to use ANSI SQL dialect with ActiveRecordPlugin.
 * <p>
 * A clever person solves a problem. A wise person avoids it.
 */
class AnsiSqlDialect extends Dialect {
  def forTableBuilderDoBuild(tableName: String): String = {
    return "select * from " + tableName + " where 1 = 2"
  }

  def forModelSave(table: Table, attrs: Map[String, AnyRef], sql: StringBuilder, paras: List[AnyRef]) {
    sql.append("insert into ").append(table.getName).append("(")
    val temp: StringBuilder = new StringBuilder(") values(")
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      val colName: String = e.getKey
      if (table.hasColumnLabel(colName)) {
        if (paras.size > 0) {
          sql.append(", ")
          temp.append(", ")
        }
        sql.append(colName)
        temp.append("?")
        paras.add(e.getValue)
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
    import scala.collection.JavaConversions._
    for (e <- record.getColumns.entrySet) {
      if (paras.size > 0) {
        sql.append(", ")
        temp.append(", ")
      }
      sql.append(e.getKey)
      temp.append("?")
      paras.add(e.getValue)
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

  /**
   * SELECT * FROM subject t1 WHERE (SELECT count(*) FROM subject t2 WHERE t2.id < t1.id AND t2.key = '123') > = 10 AND (SELECT count(*) FROM subject t2 WHERE t2.id < t1.id AND t2.key = '123') < 20 AND t1.key = '123'
   */
  def forPaginate(sql: StringBuilder, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String) {
    throw new ActiveRecordException("Your should not invoke this method because takeOverDbPaginate(...) will take over it.")
  }

  override def isTakeOverDbPaginate: Boolean = {
    return true
  }

  @SuppressWarnings(Array("rawtypes")) override def takeOverDbPaginate(conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    var totalRow: Long = 0
    var totalPage: Int = 0
    val result: List[_] = CPI.query(conn, "select count(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), paras)
    val size: Int = result.size
    if (size == 1) totalRow = (result.get(0).asInstanceOf[Number]).longValue
    else if (size > 1) totalRow = result.size
    else return new Page[Record](new ArrayList[Record](0), pageNumber, pageSize, 0, 0)
    totalPage = (totalRow / pageSize).asInstanceOf[Int]
    if (totalRow % pageSize != 0) {
      totalPage += 1
    }
    val sql: StringBuilder = new StringBuilder
    sql.append(select).append(" ").append(sqlExceptSelect)
    val pst: PreparedStatement = conn.prepareStatement(sql.toString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    {
      var i: Int = 0
      while (i < paras.length) {
        {
          pst.setObject(i + 1, paras(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val rs: ResultSet = pst.executeQuery
    val offset: Int = pageSize * (pageNumber - 1)
    {
      var i: Int = 0
      while (i < offset) {
        if (!rs.next) break //todo: break is not supported
        ({
          i += 1; i - 1
        })
      }
    }
    val list: List[Record] = buildRecord(rs, pageSize)
    if (rs != null) rs.close
    if (pst != null) pst.close
    return new Page[Record](list, pageNumber, pageSize, totalPage, totalRow.asInstanceOf[Int])
  }

  private def buildRecord(rs: ResultSet, pageSize: Int): List[Record] = {
    val result: List[Record] = new ArrayList[Record]
    val rsmd: ResultSetMetaData = rs.getMetaData
    val columnCount: Int = rsmd.getColumnCount
    val labelNames: Array[String] = new Array[String](columnCount + 1)
    val types: Array[Int] = new Array[Int](columnCount + 1)
    buildLabelNamesAndTypes(rsmd, labelNames, types)
    {
      var k: Int = 0
      while (k < pageSize && rs.next) {
        {
          val record: Record = new Record
          val columns: Map[String, AnyRef] = record.getColumns
          {
            var i: Int = 1
            while (i <= columnCount) {
              {
                var value: AnyRef = null
                if (types(i) < Types.BLOB) value = rs.getObject(i)
                else if (types(i) == Types.CLOB) value = ModelBuilder.handleClob(rs.getClob(i))
                else if (types(i) == Types.NCLOB) value = ModelBuilder.handleClob(rs.getNClob(i))
                else if (types(i) == Types.BLOB) value = ModelBuilder.handleBlob(rs.getBlob(i))
                else value = rs.getObject(i)
                columns.put(labelNames(i), value)
              }
              ({
                i += 1; i - 1
              })
            }
          }
          result.add(record)
        }
        ({
          k += 1; k - 1
        })
      }
    }
    return result
  }

  private def buildLabelNamesAndTypes(rsmd: ResultSetMetaData, labelNames: Array[String], types: Array[Int]) {
    {
      var i: Int = 1
      while (i < labelNames.length) {
        {
          labelNames(i) = rsmd.getColumnLabel(i)
          types(i) = rsmd.getColumnType(i)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  override def isTakeOverModelPaginate: Boolean = {
    return true
  }

  @SuppressWarnings(Array("rawtypes", "unchecked")) override def takeOverModelPaginate(conn: Connection, modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]], pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[_ <: Model[_ <: Model[_ <: Model[_]]]] = {
    var totalRow: Long = 0
    var totalPage: Int = 0
    val result: List[_] = CPI.query(conn, "select count(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), paras)
    val size: Int = result.size
    if (size == 1) totalRow = (result.get(0).asInstanceOf[Number]).longValue
    else if (size > 1) totalRow = result.size
    else return new Page[_](new ArrayList[_](0), pageNumber, pageSize, 0, 0)
    totalPage = (totalRow / pageSize).asInstanceOf[Int]
    if (totalRow % pageSize != 0) {
      totalPage += 1
    }
    val sql: StringBuilder = new StringBuilder
    sql.append(select).append(" ").append(sqlExceptSelect)
    val pst: PreparedStatement = conn.prepareStatement(sql.toString, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    {
      var i: Int = 0
      while (i < paras.length) {
        {
          pst.setObject(i + 1, paras(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val rs: ResultSet = pst.executeQuery
    val offset: Int = pageSize * (pageNumber - 1)
    {
      var i: Int = 0
      while (i < offset) {
        if (!rs.next) break //todo: break is not supported
        ({
          i += 1; i - 1
        })
      }
    }
    val list: List[_] = buildModel(rs, modelClass, pageSize)
    if (rs != null) rs.close
    if (pst != null) pst.close
    return new Page[_](list, pageNumber, pageSize, totalPage, totalRow.asInstanceOf[Int])
  }

  @SuppressWarnings(Array("rawtypes", "unchecked")) final def buildModel(rs: ResultSet, modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]], pageSize: Int): List[T] = {
    val result: List[T] = new ArrayList[T]
    val rsmd: ResultSetMetaData = rs.getMetaData
    val columnCount: Int = rsmd.getColumnCount
    val labelNames: Array[String] = new Array[String](columnCount + 1)
    val types: Array[Int] = new Array[Int](columnCount + 1)
    buildLabelNamesAndTypes(rsmd, labelNames, types)
    {
      var k: Int = 0
      while (k < pageSize && rs.next) {
        {
          val ar: Model[_] = modelClass.newInstance
          val attrs: Map[String, AnyRef] = CPI.getAttrs(ar)
          {
            var i: Int = 1
            while (i <= columnCount) {
              {
                var value: AnyRef = null
                if (types(i) < Types.BLOB) value = rs.getObject(i)
                else if (types(i) == Types.CLOB) value = ModelBuilder.handleClob(rs.getClob(i))
                else if (types(i) == Types.NCLOB) value = ModelBuilder.handleClob(rs.getNClob(i))
                else if (types(i) == Types.BLOB) value = ModelBuilder.handleBlob(rs.getBlob(i))
                else value = rs.getObject(i)
                attrs.put(labelNames(i), value)
              }
              ({
                i += 1; i - 1
              })
            }
          }
          result.add(ar.asInstanceOf[T])
        }
        ({
          k += 1; k - 1
        })
      }
    }
    return result
  }
}

