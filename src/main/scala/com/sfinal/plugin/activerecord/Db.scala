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

import com.sfinal.plugin.activerecord.{Record, DbPro}

/**
 * Db. Powerful database query and update tool box.
 */
@SuppressWarnings(Array("rawtypes")) object Db {
  private[activerecord] def init {
    dbPro = DbPro.use
  }

  def use(configName: String): DbPro = {
    return DbPro.use(configName)
  }

  private[activerecord] def query(config: Config, conn: Connection, sql: String, paras: AnyRef*): List[T] = {
    return dbPro.query(config, conn, sql, paras)
  }

  /**
   * @see #query(String, String, Object...)
   */
  def query(sql: String, paras: AnyRef*): List[T] = {
    return dbPro.query(sql, paras)
  }

  /**
   * @see #query(String, Object...)
   * @param sql an SQL statement
   */
  def query(sql: String): List[T] = {
    return dbPro.query(sql)
  }

  /**
   * Execute sql query and return the first result. I recommend add "limit 1" in your sql.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return Object[] if your sql has select more than one column,
   *         and it return Object if your sql has select only one column.
   */
  def queryFirst(sql: String, paras: AnyRef*): T = {
    return dbPro.queryFirst(sql, paras)
  }

  /**
   * @see #queryFirst(String, Object...)
   * @param sql an SQL statement
   */
  def queryFirst(sql: String): T = {
    return dbPro.queryFirst(sql)
  }

  /**
   * Execute sql query just return one column.
   * @param <T> the type of the column that in your sql's select statement
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return List<T>
   */
  def queryColumn(sql: String, paras: AnyRef*): T = {
    return dbPro.queryColumn(sql, paras)
  }

  def queryColumn(sql: String): T = {
    return dbPro.queryColumn(sql)
  }

  def queryStr(sql: String, paras: AnyRef*): String = {
    return dbPro.queryStr(sql, paras)
  }

  def queryStr(sql: String): String = {
    return dbPro.queryStr(sql)
  }

  def queryInt(sql: String, paras: AnyRef*): Integer = {
    return dbPro.queryInt(sql, paras)
  }

  def queryInt(sql: String): Integer = {
    return dbPro.queryInt(sql)
  }

  def queryLong(sql: String, paras: AnyRef*): Long = {
    return dbPro.queryLong(sql, paras)
  }

  def queryLong(sql: String): Long = {
    return dbPro.queryLong(sql)
  }

  def queryDouble(sql: String, paras: AnyRef*): Double = {
    return dbPro.queryDouble(sql, paras)
  }

  def queryDouble(sql: String): Double = {
    return dbPro.queryDouble(sql)
  }

  def queryFloat(sql: String, paras: AnyRef*): Float = {
    return dbPro.queryFloat(sql, paras)
  }

  def queryFloat(sql: String): Float = {
    return dbPro.queryFloat(sql)
  }

  def queryBigDecimal(sql: String, paras: AnyRef*): BigDecimal = {
    return dbPro.queryBigDecimal(sql, paras)
  }

  def queryBigDecimal(sql: String): BigDecimal = {
    return dbPro.queryBigDecimal(sql)
  }

  def queryBytes(sql: String, paras: AnyRef*): Array[Byte] = {
    return dbPro.queryBytes(sql, paras)
  }

  def queryBytes(sql: String): Array[Byte] = {
    return dbPro.queryBytes(sql)
  }

  def queryDate(sql: String, paras: AnyRef*): Date = {
    return dbPro.queryDate(sql, paras)
  }

  def queryDate(sql: String): Date = {
    return dbPro.queryDate(sql)
  }

  def queryTime(sql: String, paras: AnyRef*): Time = {
    return dbPro.queryTime(sql, paras)
  }

  def queryTime(sql: String): Time = {
    return dbPro.queryTime(sql)
  }

  def queryTimestamp(sql: String, paras: AnyRef*): Timestamp = {
    return dbPro.queryTimestamp(sql, paras)
  }

  def queryTimestamp(sql: String): Timestamp = {
    return dbPro.queryTimestamp(sql)
  }

  def queryBoolean(sql: String, paras: AnyRef*): Boolean = {
    return dbPro.queryBoolean(sql, paras)
  }

  def queryBoolean(sql: String): Boolean = {
    return dbPro.queryBoolean(sql)
  }

  def queryNumber(sql: String, paras: AnyRef*): Number = {
    return dbPro.queryNumber(sql, paras)
  }

  def queryNumber(sql: String): Number = {
    return dbPro.queryNumber(sql)
  }

  /**
   * Execute sql update
   */
  private[activerecord] def update(config: Config, conn: Connection, sql: String, paras: AnyRef*): Int = {
    return dbPro.update(config, conn, sql, paras)
  }

  /**
   * Execute update, insert or delete sql statement.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
   *         or <code>DELETE</code> statements, or 0 for SQL statements
   *         that return nothing
   */
  def update(sql: String, paras: AnyRef*): Int = {
    return dbPro.update(sql, paras)
  }

  /**
   * @see #update(String, Object...)
   * @param sql an SQL statement
   */
  def update(sql: String): Int = {
    return dbPro.update(sql)
  }

  private[activerecord] def find(config: Config, conn: Connection, sql: String, paras: AnyRef*): List[Record] = {
    return dbPro.find(config, conn, sql, paras)
  }

  /**
   * @see #find(String, String, Object...)
   */
  def find(sql: String, paras: AnyRef*): List[Record] = {
    return dbPro.find(sql, paras)
  }

  /**
   * @see #find(String, String, Object...)
   * @param sql the sql statement
   */
  def find(sql: String): List[Record] = {
    return dbPro.find(sql)
  }

  /**
   * Find first record. I recommend add "limit 1" in your sql.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return the Record object
   */
  def findFirst(sql: String, paras: AnyRef*): Record = {
    return dbPro.findFirst(sql, paras)
  }

  /**
   * @see #findFirst(String, Object...)
   * @param sql an SQL statement
   */
  def findFirst(sql: String): Record = {
    return dbPro.findFirst(sql)
  }

  /**
   * Find record by id.
   * Example: Record user = Db.findById("user", 15);
   * @param tableName the table name of the table
   * @param idValue the id value of the record
   */
  def findById(tableName: String, idValue: AnyRef): Record = {
    return dbPro.findById(tableName, idValue)
  }

  /**
   * Find record by id. Fetch the specific columns only.
   * Example: Record user = Db.findById("user", 15, "name, age");
   * @param tableName the table name of the table
   * @param idValue the id value of the record
   * @param columns the specific columns separate with comma character ==> ","
   */
  def findById(tableName: String, idValue: Number, columns: String): Record = {
    return dbPro.findById(tableName, idValue, columns)
  }

  /**
   * Find record by id.
   * Example: Record user = Db.findById("user", "user_id", 15);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param idValue the id value of the record
   */
  def findById(tableName: String, primaryKey: String, idValue: Number): Record = {
    return dbPro.findById(tableName, primaryKey, idValue)
  }

  /**
   * Find record by id. Fetch the specific columns only.
   * Example: Record user = Db.findById("user", "user_id", 15, "name, age");
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param idValue the id value of the record
   * @param columns the specific columns separate with comma character ==> ","
   */
  def findById(tableName: String, primaryKey: String, idValue: AnyRef, columns: String): Record = {
    return dbPro.findById(tableName, primaryKey, idValue, columns)
  }

  /**
   * Delete record by id.
   * Example: boolean succeed = Db.deleteById("user", 15);
   * @param tableName the table name of the table
   * @param id the id value of the record
   * @return true if delete succeed otherwise false
   */
  def deleteById(tableName: String, id: AnyRef): Boolean = {
    return dbPro.deleteById(tableName, id)
  }

  /**
   * Delete record by id.
   * Example: boolean succeed = Db.deleteById("user", "user_id", 15);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param id the id value of the record
   * @return true if delete succeed otherwise false
   */
  def deleteById(tableName: String, primaryKey: String, id: AnyRef): Boolean = {
    return dbPro.deleteById(tableName, primaryKey, id)
  }

  /**
   * Delete record.
   * Example: boolean succeed = Db.delete("user", "id", user);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param record the record
   * @return true if delete succeed otherwise false
   */
  def delete(tableName: String, primaryKey: String, record: Record): Boolean = {
    return dbPro.delete(tableName, primaryKey, record)
  }

  /**
   * Example: boolean succeed = Db.delete("user", user);
   * @see #delete(String, String, Record)
   */
  def delete(tableName: String, record: Record): Boolean = {
    return dbPro.delete(tableName, record)
  }

  private[activerecord] def paginate(config: Config, conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    return dbPro.paginate(config, conn, pageNumber, pageSize, select, sqlExceptSelect, paras)
  }

  /**
   * @see #paginate(String, int, int, String, String, Object...)
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    return dbPro.paginate(pageNumber, pageSize, select, sqlExceptSelect, paras)
  }

  /**
   * @see #paginate(String, int, int, String, String, Object...)
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[Record] = {
    return dbPro.paginate(pageNumber, pageSize, select, sqlExceptSelect)
  }

  private[activerecord] def save(config: Config, conn: Connection, tableName: String, primaryKey: String, record: Record): Boolean = {
    return dbPro.save(config, conn, tableName, primaryKey, record)
  }

  /**
   * Save record.
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param record the record will be saved
   * @param true if save succeed otherwise false
   */
  def save(tableName: String, primaryKey: String, record: Record): Boolean = {
    return dbPro.save(tableName, primaryKey, record)
  }

  /**
   * @see #save(String, String, Record)
   */
  def save(tableName: String, record: Record): Boolean = {
    return dbPro.save(tableName, record)
  }

  private[activerecord] def update(config: Config, conn: Connection, tableName: String, primaryKey: String, record: Record): Boolean = {
    return dbPro.update(config, conn, tableName, primaryKey, record)
  }

  /**
   * Update Record.
   * @param tableName the table name of the Record save to
   * @param primaryKey the primary key of the table
   * @param record the Record object
   * @param true if update succeed otherwise false
   */
  def update(tableName: String, primaryKey: String, record: Record): Boolean = {
    return dbPro.update(tableName, primaryKey, record)
  }

  /**
   * Update Record. The primary key of the table is: "id".
   * @see #update(String, String, Record)
   */
  def update(tableName: String, record: Record): Boolean = {
    return dbPro.update(tableName, record)
  }

  /**
   * @see #execute(String, ICallback)
   */
  def execute(callback: ICallback): AnyRef = {
    return dbPro.execute(callback)
  }

  /**
   * Execute callback. It is useful when all the API can not satisfy your requirement.
   * @param config the Config object
   * @param callback the ICallback interface
   */
  private[activerecord] def execute(config: Config, callback: ICallback): AnyRef = {
    return dbPro.execute(config, callback)
  }

  /**
   * Execute transaction.
   * @param config the Config object
   * @param transactionLevel the transaction level
   * @param atom the atom operation
   * @return true if transaction executing succeed otherwise false
   */
  private[activerecord] def tx(config: Config, transactionLevel: Int, atom: IAtom): Boolean = {
    return dbPro.tx(config, transactionLevel, atom)
  }

  def tx(transactionLevel: Int, atom: IAtom): Boolean = {
    return dbPro.tx(transactionLevel, atom)
  }

  /**
   * Execute transaction with default transaction level.
   * @see #tx(int, IAtom)
   */
  def tx(atom: IAtom): Boolean = {
    return dbPro.tx(atom)
  }

  /**
   * Find Record by cache.
   * @see #find(String, Object...)
   * @param cacheName the cache name
   * @param key the key used to get date from cache
   * @return the list of Record
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String, paras: AnyRef*): List[Record] = {
    return dbPro.findByCache(cacheName, key, sql, paras)
  }

  /**
   * @see #findByCache(String, Object, String, Object...)
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String): List[Record] = {
    return dbPro.findByCache(cacheName, key, sql)
  }

  /**
   * Paginate by cache.
   * @see #paginate(int, int, String, String, Object...)
   * @return Page
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    return dbPro.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect, paras)
  }

  /**
   * @see #paginateByCache(String, Object, int, int, String, String, Object...)
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[Record] = {
    return dbPro.paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect)
  }

  /**
   * @see #batch(String, String, Object[][], int)
   */
  def batch(sql: String, paras: Array[Array[AnyRef]], batchSize: Int): Array[Int] = {
    return dbPro.batch(sql, paras, batchSize)
  }

  /**
   * @see #batch(String, String, String, List, int)
   */
  def batch(sql: String, columns: String, modelOrRecordList: List[_], batchSize: Int): Array[Int] = {
    return dbPro.batch(sql, columns, modelOrRecordList, batchSize)
  }

  /**
   * @see #batch(String, List, int)
   */
  def batch(sqlList: List[String], batchSize: Int): Array[Int] = {
    return dbPro.batch(sqlList, batchSize)
  }

  private var dbPro: DbPro = null
}





