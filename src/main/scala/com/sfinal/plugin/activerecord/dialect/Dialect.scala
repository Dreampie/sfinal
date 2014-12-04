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
import java.sql.SQLException
import java.util.List
import java.util.Map
import java.util.Set
import com.sfinal.plugin.activerecord._

/**
 * Dialect.
 */
abstract class Dialect {
  def forTableBuilderDoBuild(tableName: String): String

  def forModelSave(table: Table, attrs: Map[String, AnyRef], sql: StringBuilder, paras: List[AnyRef])

  def forModelDeleteById(table: Table): String

  def forModelUpdate(table: Table, attrs: Map[String, AnyRef], modifyFlag: Set[String], pKey: String, id: AnyRef, sql: StringBuilder, paras: List[AnyRef])

  def forModelFindById(table: Table, columns: String): String

  def forDbFindById(tableName: String, primaryKey: String, columns: String): String

  def forDbDeleteById(tableName: String, primaryKey: String): String

  def forDbSave(sql: StringBuilder, paras: List[AnyRef], tableName: String, record: Record)

  def forDbUpdate(tableName: String, primaryKey: String, id: AnyRef, record: Record, sql: StringBuilder, paras: List[AnyRef])

  def forPaginate(sql: StringBuilder, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String)

  def isOracle: Boolean = {
    return false
  }

  def isTakeOverDbPaginate: Boolean = {
    return false
  }

  def takeOverDbPaginate(conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    throw new RuntimeException("You should implements this method in " + getClass.getName)
  }

  def isTakeOverModelPaginate: Boolean = {
    return false
  }

  @SuppressWarnings(Array("rawtypes")) def takeOverModelPaginate(conn: Connection, modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]], pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[_] = {
    throw new RuntimeException("You should implements this method in " + getClass.getName)
  }

  def fillStatement(pst: PreparedStatement, paras: List[AnyRef]) {
    {
      var i: Int = 0
      val size: Int = paras.size
      while (i < size) {
        {
          pst.setObject(i + 1, paras.get(i))
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

  def fillStatement(pst: PreparedStatement, paras: AnyRef*) {
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
  }

  def getDefaultPrimaryKey: String = {
    return "id"
  }
}







