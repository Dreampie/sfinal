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
import java.sql.SQLException
import java.util.List
import java.util.Map

import com.sfinal.plugin.activerecord.{DbKit, Db}

/**
 * Cross Package Invoking pattern for package activerecord.
 */
object CPI {
  /**
   * Return the attributes map of the model
   * @param model the model extends from class Model
   * @return the attributes map of the model
   */
  @SuppressWarnings(Array("unchecked", "rawtypes")) final def getAttrs(model: Model[_ <: Model[_ <: Model[_ <: Model[_]]]]): Map[String, AnyRef] = {
    return model.getAttrs
  }

  def query(conn: Connection, sql: String, paras: AnyRef*): List[T] = {
    return Db.query(DbKit.config, conn, sql, paras)
  }

  def query(configName: String, conn: Connection, sql: String, paras: AnyRef*): List[T] = {
    return Db.query(DbKit.getConfig(configName), conn, sql, paras)
  }

  /**
   * Return the columns map of the record
   * @param record the Record object
   * @return the columns map of the record
	public static final Map<String, Object> getColumns(Record record) {
		return record.getColumns();
	} */
  def find(conn: Connection, sql: String, paras: AnyRef*): List[Record] = {
    return Db.find(DbKit.config, conn, sql, paras)
  }

  def find(configName: String, conn: Connection, sql: String, paras: AnyRef*): List[Record] = {
    return Db.find(DbKit.getConfig(configName), conn, sql, paras)
  }

  def paginate(conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    return Db.paginate(DbKit.config, conn, pageNumber, pageSize, select, sqlExceptSelect, paras)
  }

  def paginate(configName: String, conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    return Db.paginate(DbKit.getConfig(configName), conn, pageNumber, pageSize, select, sqlExceptSelect, paras)
  }

  def update(conn: Connection, sql: String, paras: AnyRef*): Int = {
    return Db.update(DbKit.config, conn, sql, paras)
  }

  def update(configName: String, conn: Connection, sql: String, paras: AnyRef*): Int = {
    return Db.update(DbKit.getConfig(configName), conn, sql, paras)
  }
}



