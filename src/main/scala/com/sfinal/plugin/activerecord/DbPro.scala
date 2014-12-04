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
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import com.sfinal.plugin.activerecord.{Model, NestedTransactionHelpException, Record, RecordBuilder}
import com.sfinal.plugin.activerecord.cache.ICache
import com.sfinal.plugin.activerecord.DbKit.NULL_PARA_ARRAY

/**
 * DbPro. Professional database query and update tool.
 */
@SuppressWarnings(Array("rawtypes", "unchecked")) object DbPro {
  def use(configName: String): DbPro = {
    var result: DbPro = map.get(configName)
    if (result == null) {
      result = new DbPro(configName)
      map.put(configName, result)
    }
    return result
  }

  def use: DbPro = {
    return use(DbKit.config.name)
  }

  private final val map: Map[String, DbPro] = new HashMap[String, DbPro]
}

@SuppressWarnings(Array("rawtypes", "unchecked")) class DbPro {
  def this() {
    this()
    if (DbKit.config == null) throw new RuntimeException("The main config is null, initialize ActiveRecordPlugin first")
    this.config = DbKit.config
  }

  def this(configName: String) {
    this()
    this.config = DbKit.getConfig(configName)
    if (this.config == null) throw new IllegalArgumentException("Config not found by configName: " + configName)
  }

  private[activerecord] def query(config: Config, conn: Connection, sql: String, paras: AnyRef*): List[T] = {
    val result: List[_] = new ArrayList[_]
    val pst: PreparedStatement = conn.prepareStatement(sql)
    config.dialect.fillStatement(pst, paras)
    val rs: ResultSet = pst.executeQuery
    val colAmount: Int = rs.getMetaData.getColumnCount
    if (colAmount > 1) {
      while (rs.next) {
        val temp: Array[AnyRef] = new Array[AnyRef](colAmount)
        {
          var i: Int = 0
          while (i < colAmount) {
            {
              temp(i) = rs.getObject(i + 1)
            }
            ({
              i += 1; i - 1
            })
          }
        }
        result.add(temp)
      }
    }
    else if (colAmount == 1) {
      while (rs.next) {
        result.add(rs.getObject(1))
      }
    }
    DbKit.closeQuietly(rs, pst)
    return result
  }

  /**
   * @see #query(String, String, Object...)
   */
  def query(sql: String, paras: AnyRef*): List[T] = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return query(config, conn, sql, paras)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * @see #query(String, Object...)
   * @param sql an SQL statement
   */
  def query(sql: String): List[T] = {
    return query(sql, NULL_PARA_ARRAY)
  }

  /**
   * Execute sql query and return the first result. I recommend add "limit 1" in your sql.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return Object[] if your sql has select more than one column,
   *         and it return Object if your sql has select only one column.
   */
  def queryFirst(sql: String, paras: AnyRef*): T = {
    val result: List[T] = query(sql, paras)
    return (if (result.size > 0) result.get(0) else null)
  }

  /**
   * @see #queryFirst(String, Object...)
   * @param sql an SQL statement
   */
  def queryFirst(sql: String): T = {
    val result: List[T] = query(sql, NULL_PARA_ARRAY)
    return (if (result.size > 0) result.get(0) else null)
  }

  /**
   * Execute sql query just return one column.
   * @param <T> the type of the column that in your sql's select statement
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return List<T>
   */
  def queryColumn(sql: String, paras: AnyRef*): T = {
    val result: List[T] = query(sql, paras)
    if (result.size > 0) {
      val temp: T = result.get(0)
      if (temp.isInstanceOf[Array[AnyRef]]) throw new ActiveRecordException("Only ONE COLUMN can be queried.")
      return temp
    }
    return null
  }

  def queryColumn(sql: String): T = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[T]
  }

  def queryStr(sql: String, paras: AnyRef*): String = {
    return queryColumn(sql, paras).asInstanceOf[String]
  }

  def queryStr(sql: String): String = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[String]
  }

  def queryInt(sql: String, paras: AnyRef*): Integer = {
    return queryColumn(sql, paras).asInstanceOf[Integer]
  }

  def queryInt(sql: String): Integer = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Integer]
  }

  def queryLong(sql: String, paras: AnyRef*): Long = {
    return queryColumn(sql, paras).asInstanceOf[Long]
  }

  def queryLong(sql: String): Long = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Long]
  }

  def queryDouble(sql: String, paras: AnyRef*): Double = {
    return queryColumn(sql, paras).asInstanceOf[Double]
  }

  def queryDouble(sql: String): Double = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Double]
  }

  def queryFloat(sql: String, paras: AnyRef*): Float = {
    return queryColumn(sql, paras).asInstanceOf[Float]
  }

  def queryFloat(sql: String): Float = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Float]
  }

  def queryBigDecimal(sql: String, paras: AnyRef*): BigDecimal = {
    return queryColumn(sql, paras).asInstanceOf[BigDecimal]
  }

  def queryBigDecimal(sql: String): BigDecimal = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[BigDecimal]
  }

  def queryBytes(sql: String, paras: AnyRef*): Array[Byte] = {
    return queryColumn(sql, paras).asInstanceOf[Array[Byte]]
  }

  def queryBytes(sql: String): Array[Byte] = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Array[Byte]]
  }

  def queryDate(sql: String, paras: AnyRef*): Date = {
    return queryColumn(sql, paras).asInstanceOf[Date]
  }

  def queryDate(sql: String): Date = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Date]
  }

  def queryTime(sql: String, paras: AnyRef*): Time = {
    return queryColumn(sql, paras).asInstanceOf[Time]
  }

  def queryTime(sql: String): Time = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Time]
  }

  def queryTimestamp(sql: String, paras: AnyRef*): Timestamp = {
    return queryColumn(sql, paras).asInstanceOf[Timestamp]
  }

  def queryTimestamp(sql: String): Timestamp = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Timestamp]
  }

  def queryBoolean(sql: String, paras: AnyRef*): Boolean = {
    return queryColumn(sql, paras).asInstanceOf[Boolean]
  }

  def queryBoolean(sql: String): Boolean = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Boolean]
  }

  def queryNumber(sql: String, paras: AnyRef*): Number = {
    return queryColumn(sql, paras).asInstanceOf[Number]
  }

  def queryNumber(sql: String): Number = {
    return queryColumn(sql, NULL_PARA_ARRAY).asInstanceOf[Number]
  }

  /**
   * Execute sql update
   */
  private[activerecord] def update(config: Config, conn: Connection, sql: String, paras: AnyRef*): Int = {
    val pst: PreparedStatement = conn.prepareStatement(sql)
    config.dialect.fillStatement(pst, paras)
    val result: Int = pst.executeUpdate
    DbKit.closeQuietly(pst)
    return result
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
    var conn: Connection = null
    try {
      conn = config.getConnection
      return update(config, conn, sql, paras)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * @see #update(String, Object...)
   * @param sql an SQL statement
   */
  def update(sql: String): Int = {
    return update(sql, NULL_PARA_ARRAY)
  }

  /**
   * Get id after insert method getGeneratedKey().
   */
  private def getGeneratedKey(pst: PreparedStatement): AnyRef = {
    val rs: ResultSet = pst.getGeneratedKeys
    var id: AnyRef = null
    if (rs.next) id = rs.getObject(1)
    rs.close
    return id
  }

  private[activerecord] def find(config: Config, conn: Connection, sql: String, paras: AnyRef*): List[Record] = {
    val pst: PreparedStatement = conn.prepareStatement(sql)
    config.dialect.fillStatement(pst, paras)
    val rs: ResultSet = pst.executeQuery
    val result: List[Record] = RecordBuilder.build(config, rs)
    DbKit.closeQuietly(rs, pst)
    return result
  }

  /**
   * @see #find(String, String, Object...)
   */
  def find(sql: String, paras: AnyRef*): List[Record] = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return find(config, conn, sql, paras)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * @see #find(String, String, Object...)
   * @param sql the sql statement
   */
  def find(sql: String): List[Record] = {
    return find(sql, NULL_PARA_ARRAY)
  }

  /**
   * Find first record. I recommend add "limit 1" in your sql.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return the Record object
   */
  def findFirst(sql: String, paras: AnyRef*): Record = {
    val result: List[Record] = find(sql, paras)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * @see #findFirst(String, Object...)
   * @param sql an SQL statement
   */
  def findFirst(sql: String): Record = {
    val result: List[Record] = find(sql, NULL_PARA_ARRAY)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * Find record by id.
   * Example: Record user = DbPro.use().findById("user", 15);
   * @param tableName the table name of the table
   * @param idValue the id value of the record
   */
  def findById(tableName: String, idValue: AnyRef): Record = {
    return findById(tableName, config.dialect.getDefaultPrimaryKey, idValue, "*")
  }

  /**
   * Find record by id. Fetch the specific columns only.
   * Example: Record user = DbPro.use().findById("user", 15, "name, age");
   * @param tableName the table name of the table
   * @param idValue the id value of the record
   * @param columns the specific columns separate with comma character ==> ","
   */
  def findById(tableName: String, idValue: Number, columns: String): Record = {
    return findById(tableName, config.dialect.getDefaultPrimaryKey, idValue, columns)
  }

  /**
   * Find record by id.
   * Example: Record user = DbPro.use().findById("user", "user_id", 15);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param idValue the id value of the record
   */
  def findById(tableName: String, primaryKey: String, idValue: Number): Record = {
    return findById(tableName, primaryKey, idValue, "*")
  }

  /**
   * Find record by id. Fetch the specific columns only.
   * Example: Record user = DbPro.use().findById("user", "user_id", 15, "name, age");
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param idValue the id value of the record
   * @param columns the specific columns separate with comma character ==> ","
   */
  def findById(tableName: String, primaryKey: String, idValue: AnyRef, columns: String): Record = {
    val sql: String = config.dialect.forDbFindById(tableName, primaryKey, columns)
    val result: List[Record] = find(sql, idValue)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * Delete record by id.
   * Example: boolean succeed = DbPro.use().deleteById("user", 15);
   * @param tableName the table name of the table
   * @param id the id value of the record
   * @return true if delete succeed otherwise false
   */
  def deleteById(tableName: String, id: AnyRef): Boolean = {
    return deleteById(tableName, config.dialect.getDefaultPrimaryKey, id)
  }

  /**
   * Delete record by id.
   * Example: boolean succeed = DbPro.use().deleteById("user", "user_id", 15);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param id the id value of the record
   * @return true if delete succeed otherwise false
   */
  def deleteById(tableName: String, primaryKey: String, id: AnyRef): Boolean = {
    if (id == null) throw new IllegalArgumentException("id can not be null")
    val sql: String = config.dialect.forDbDeleteById(tableName, primaryKey)
    return update(sql, id) >= 1
  }

  /**
   * Delete record.
   * Example: boolean succeed = DbPro.use().delete("user", "id", user);
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param record the record
   * @return true if delete succeed otherwise false
   */
  def delete(tableName: String, primaryKey: String, record: Record): Boolean = {
    return deleteById(tableName, primaryKey, record.get(primaryKey))
  }

  /**
   * Example: boolean succeed = DbPro.use().delete("user", user);
   * @see #delete(String, String, Record)
   */
  def delete(tableName: String, record: Record): Boolean = {
    val defaultPrimaryKey: String = config.dialect.getDefaultPrimaryKey
    return deleteById(tableName, defaultPrimaryKey, record.get(defaultPrimaryKey))
  }

  private[activerecord] def paginate(config: Config, conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    if (pageNumber < 1 || pageSize < 1) throw new ActiveRecordException("pageNumber and pageSize must be more than 0")
    if (config.dialect.isTakeOverDbPaginate) return config.dialect.takeOverDbPaginate(conn, pageNumber, pageSize, select, sqlExceptSelect, paras)
    var totalRow: Long = 0
    var totalPage: Int = 0
    val result: List[_] = query(config, conn, "select count(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), paras)
    val size: Int = result.size
    if (size == 1) totalRow = (result.get(0).asInstanceOf[Number]).longValue
    else if (size > 1) totalRow = result.size
    else return new Page[Record](new ArrayList[Record](0), pageNumber, pageSize, 0, 0)
    totalPage = (totalRow / pageSize).asInstanceOf[Int]
    if (totalRow % pageSize != 0) {
      totalPage += 1
    }
    val sql: StringBuilder = new StringBuilder
    config.dialect.forPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect)
    val list: List[Record] = find(config, conn, sql.toString, paras)
    return new Page[Record](list, pageNumber, pageSize, totalPage, totalRow.asInstanceOf[Int])
  }

  /**
   * @see #paginate(String, int, int, String, String, Object...)
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return paginate(config, conn, pageNumber, pageSize, select, sqlExceptSelect, paras)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * @see #paginate(String, int, int, String, String, Object...)
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[Record] = {
    return paginate(pageNumber, pageSize, select, sqlExceptSelect, NULL_PARA_ARRAY)
  }

  private[activerecord] def save(config: Config, conn: Connection, tableName: String, primaryKey: String, record: Record): Boolean = {
    val paras: List[AnyRef] = new ArrayList[AnyRef]
    val sql: StringBuilder = new StringBuilder
    config.dialect.forDbSave(sql, paras, tableName, record)
    var pst: PreparedStatement = null
    if (config.dialect.isOracle) pst = conn.prepareStatement(sql.toString, Array[String](primaryKey))
    else pst = conn.prepareStatement(sql.toString, Statement.RETURN_GENERATED_KEYS)
    config.dialect.fillStatement(pst, paras)
    val result: Int = pst.executeUpdate
    record.set(primaryKey, getGeneratedKey(pst))
    DbKit.closeQuietly(pst)
    return result >= 1
  }

  /**
   * Save record.
   * @param tableName the table name of the table
   * @param primaryKey the primary key of the table
   * @param record the record will be saved
   * @param true if save succeed otherwise false
   */
  def save(tableName: String, primaryKey: String, record: Record): Boolean = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return save(config, conn, tableName, primaryKey, record)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * @see #save(String, String, Record)
   */
  def save(tableName: String, record: Record): Boolean = {
    return save(tableName, config.dialect.getDefaultPrimaryKey, record)
  }

  private[activerecord] def update(config: Config, conn: Connection, tableName: String, primaryKey: String, record: Record): Boolean = {
    val id: AnyRef = record.get(primaryKey)
    if (id == null) throw new ActiveRecordException("You can't update model without Primary Key.")
    val sql: StringBuilder = new StringBuilder
    val paras: List[AnyRef] = new ArrayList[AnyRef]
    config.dialect.forDbUpdate(tableName, primaryKey, id, record, sql, paras)
    if (paras.size <= 1) {
      return false
    }
    return update(config, conn, sql.toString, paras.toArray) >= 1
  }

  /**
   * Update Record.
   * @param tableName the table name of the Record save to
   * @param primaryKey the primary key of the table
   * @param record the Record object
   * @param true if update succeed otherwise false
   */
  def update(tableName: String, primaryKey: String, record: Record): Boolean = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return update(config, conn, tableName, primaryKey, record)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * Update Record. The primary key of the table is: "id".
   * @see #update(String, String, Record)
   */
  def update(tableName: String, record: Record): Boolean = {
    return update(tableName, config.dialect.getDefaultPrimaryKey, record)
  }

  /**
   * @see #execute(String, ICallback)
   */
  def execute(callback: ICallback): AnyRef = {
    return execute(config, callback)
  }

  /**
   * Execute callback. It is useful when all the API can not satisfy your requirement.
   * @param config the Config object
   * @param callback the ICallback interface
   */
  private[activerecord] def execute(config: Config, callback: ICallback): AnyRef = {
    var conn: Connection = null
    try {
      conn = config.getConnection
      return callback.call(conn)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(conn)
    }
  }

  /**
   * Execute transaction.
   * @param config the Config object
   * @param transactionLevel the transaction level
   * @param atom the atom operation
   * @return true if transaction executing succeed otherwise false
   */
  private[activerecord] def tx(config: Config, transactionLevel: Int, atom: IAtom): Boolean = {
    var conn: Connection = config.getThreadLocalConnection
    if (conn != null) {
      try {
        if (conn.getTransactionIsolation < transactionLevel) conn.setTransactionIsolation(transactionLevel)
        val result: Boolean = atom.run
        if (result) return true
        throw new NestedTransactionHelpException("Notice the outer transaction that the nested transaction return false")
      }
      catch {
        case e: SQLException => {
          throw new ActiveRecordException(e)
        }
      }
    }
    var autoCommit: Boolean = null
    try {
      conn = config.getConnection
      autoCommit = conn.getAutoCommit
      config.setThreadLocalConnection(conn)
      conn.setTransactionIsolation(transactionLevel)
      conn.setAutoCommit(false)
      val result: Boolean = atom.run
      if (result) conn.commit
      else conn.rollback
      return result
    }
    catch {
      case e: NestedTransactionHelpException => {
        if (conn != null) try {
          conn.rollback
        }
        catch {
          case e1: Exception => {
            e1.printStackTrace
          }
        }
        return false
      }
      case t: Throwable => {
        if (conn != null) try {
          conn.rollback
        }
        catch {
          case e1: Exception => {
            e1.printStackTrace
          }
        }
        throw if (t.isInstanceOf[RuntimeException]) t.asInstanceOf[RuntimeException] else new ActiveRecordException(t)
      }
    }
    finally {
      try {
        if (conn != null) {
          if (autoCommit != null) conn.setAutoCommit(autoCommit)
          conn.close
        }
      }
      catch {
        case t: Throwable => {
          t.printStackTrace
        }
      }
      finally {
        config.removeThreadLocalConnection
      }
    }
  }

  def tx(transactionLevel: Int, atom: IAtom): Boolean = {
    return tx(config, transactionLevel, atom)
  }

  /**
   * Execute transaction with default transaction level.
   * @see #tx(int, IAtom)
   */
  def tx(atom: IAtom): Boolean = {
    return tx(config, config.getTransactionLevel, atom)
  }

  /**
   * Find Record by cache.
   * @see #find(String, Object...)
   * @param cacheName the cache name
   * @param key the key used to get date from cache
   * @return the list of Record
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String, paras: AnyRef*): List[Record] = {
    val cache: ICache = config.getCache
    var result: List[Record] = cache.get(cacheName, key)
    if (result == null) {
      result = find(sql, paras)
      cache.put(cacheName, key, result)
    }
    return result
  }

  /**
   * @see #findByCache(String, Object, String, Object...)
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String): List[Record] = {
    return findByCache(cacheName, key, sql, NULL_PARA_ARRAY)
  }

  /**
   * Paginate by cache.
   * @see #paginate(int, int, String, String, Object...)
   * @return Page
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[Record] = {
    val cache: ICache = config.getCache
    var result: Page[Record] = cache.get(cacheName, key)
    if (result == null) {
      result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras)
      cache.put(cacheName, key, result)
    }
    return result
  }

  /**
   * @see #paginateByCache(String, Object, int, int, String, String, Object...)
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[Record] = {
    return paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect, NULL_PARA_ARRAY)
  }

  private def batch(config: Config, conn: Connection, sql: String, paras: Array[Array[AnyRef]], batchSize: Int): Array[Int] = {
    if (paras == null || paras.length == 0) throw new IllegalArgumentException("The paras array length must more than 0.")
    if (batchSize < 1) throw new IllegalArgumentException("The batchSize must more than 0.")
    var counter: Int = 0
    val pointer: Int = 0
    val result: Array[Int] = new Array[Int](paras.length)
    val pst: PreparedStatement = conn.prepareStatement(sql)
    {
      var i: Int = 0
      while (i < paras.length) {
        {
          {
            var j: Int = 0
            while (j < paras(i).length) {
              {
                val value: AnyRef = paras(i)(j)
                if (config.dialect.isOracle) {
                  if (value.isInstanceOf[Date]) pst.setDate(j + 1, value.asInstanceOf[Date])
                  else if (value.isInstanceOf[Timestamp]) pst.setTimestamp(j + 1, value.asInstanceOf[Timestamp])
                  else pst.setObject(j + 1, value)
                }
                else pst.setObject(j + 1, value)
              }
              ({
                j += 1; j - 1
              })
            }
          }
          pst.addBatch
          if (({
            counter += 1; counter
          }) >= batchSize) {
            counter = 0
            val r: Array[Int] = pst.executeBatch
            conn.commit
            {
              var k: Int = 0
              while (k < r.length) {
                result(({
                  pointer += 1; pointer - 1
                })) = r(k)
                ({
                  k += 1; k - 1
                })
              }
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val r: Array[Int] = pst.executeBatch
    conn.commit
    {
      var k: Int = 0
      while (k < r.length) {
        result(({
          pointer += 1; pointer - 1
        })) = r(k)
        ({
          k += 1; k - 1
        })
      }
    }
    DbKit.closeQuietly(pst)
    return result
  }

  /**
   * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
   * <p>
   * Example:
   * <pre>
   * String sql = "insert into user(name, cash) values(?, ?)";
   * int[] result = DbPro.use().batch("myConfig", sql, new Object[][]{{"James", 888}, {"zhanjin", 888}});
   * </pre>
   * @param sql The SQL to execute.
   * @param paras An array of query replacement parameters.  Each row in this array is one set of batch replacement values.
   * @return The number of rows updated per statement
   */
  def batch(sql: String, paras: Array[Array[AnyRef]], batchSize: Int): Array[Int] = {
    var conn: Connection = null
    var autoCommit: Boolean = null
    try {
      conn = config.getConnection
      autoCommit = conn.getAutoCommit
      conn.setAutoCommit(false)
      return batch(config, conn, sql, paras, batchSize)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      if (autoCommit != null) try {
        conn.setAutoCommit(autoCommit)
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      config.close(conn)
    }
  }

  private def batch(config: Config, conn: Connection, sql: String, columns: String, list: List[_], batchSize: Int): Array[Int] = {
    if (list == null || list.size == 0) return new Array[Int](0)
    val element: AnyRef = list.get(0)
    if (!(element.isInstanceOf[Record]) && !(element.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]])) throw new IllegalArgumentException("The element in list must be Model or Record.")
    if (batchSize < 1) throw new IllegalArgumentException("The batchSize must more than 0.")
    val isModel: Boolean = element.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]
    val columnArray: Array[String] = columns.split(",")
    {
      var i: Int = 0
      while (i < columnArray.length) {
        columnArray(i) = columnArray(i).trim
        ({
          i += 1; i - 1
        })
      }
    }
    var counter: Int = 0
    val pointer: Int = 0
    val size: Int = list.size
    val result: Array[Int] = new Array[Int](size)
    val pst: PreparedStatement = conn.prepareStatement(sql)
    {
      var i: Int = 0
      while (i < size) {
        {
          val map: Map[_, _] = if (isModel) (list.get(i).asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]).getAttrs else (list.get(i).asInstanceOf[Record]).getColumns
          {
            var j: Int = 0
            while (j < columnArray.length) {
              {
                val value: AnyRef = map.get(columnArray(j))
                if (config.dialect.isOracle) {
                  if (value.isInstanceOf[Date]) pst.setDate(j + 1, value.asInstanceOf[Date])
                  else if (value.isInstanceOf[Timestamp]) pst.setTimestamp(j + 1, value.asInstanceOf[Timestamp])
                  else pst.setObject(j + 1, value)
                }
                else pst.setObject(j + 1, value)
              }
              ({
                j += 1; j - 1
              })
            }
          }
          pst.addBatch
          if (({
            counter += 1; counter
          }) >= batchSize) {
            counter = 0
            val r: Array[Int] = pst.executeBatch
            conn.commit
            {
              var k: Int = 0
              while (k < r.length) {
                result(({
                  pointer += 1; pointer - 1
                })) = r(k)
                ({
                  k += 1; k - 1
                })
              }
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val r: Array[Int] = pst.executeBatch
    conn.commit
    {
      var k: Int = 0
      while (k < r.length) {
        result(({
          pointer += 1; pointer - 1
        })) = r(k)
        ({
          k += 1; k - 1
        })
      }
    }
    DbKit.closeQuietly(pst)
    return result
  }

  /**
   * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
   * <p>
   * Example:
   * <pre>
   * String sql = "insert into user(name, cash) values(?, ?)";
   * int[] result = DbPro.use().batch("myConfig", sql, "name, cash", modelList, 500);
   * </pre>
   * @param sql The SQL to execute.
   * @param columns the columns need be processed by sql.
   * @param modelOrRecordList model or record object list.
   * @param batchSize batch size.
   * @return The number of rows updated per statement
   */
  def batch(sql: String, columns: String, modelOrRecordList: List[_], batchSize: Int): Array[Int] = {
    var conn: Connection = null
    var autoCommit: Boolean = null
    try {
      conn = config.getConnection
      autoCommit = conn.getAutoCommit
      conn.setAutoCommit(false)
      return batch(config, conn, sql, columns, modelOrRecordList, batchSize)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      if (autoCommit != null) try {
        conn.setAutoCommit(autoCommit)
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      config.close(conn)
    }
  }

  private def batch(config: Config, conn: Connection, sqlList: List[String], batchSize: Int): Array[Int] = {
    if (sqlList == null || sqlList.size == 0) throw new IllegalArgumentException("The sqlList length must more than 0.")
    if (batchSize < 1) throw new IllegalArgumentException("The batchSize must more than 0.")
    var counter: Int = 0
    val pointer: Int = 0
    val size: Int = sqlList.size
    val result: Array[Int] = new Array[Int](size)
    val st: Statement = conn.createStatement
    {
      var i: Int = 0
      while (i < size) {
        {
          st.addBatch(sqlList.get(i))
          if (({
            counter += 1; counter
          }) >= batchSize) {
            counter = 0
            val r: Array[Int] = st.executeBatch
            conn.commit
            {
              var k: Int = 0
              while (k < r.length) {
                result(({
                  pointer += 1; pointer - 1
                })) = r(k)
                ({
                  k += 1; k - 1
                })
              }
            }
          }
        }
        ({
          i += 1; i - 1
        })
      }
    }
    val r: Array[Int] = st.executeBatch
    conn.commit
    {
      var k: Int = 0
      while (k < r.length) {
        result(({
          pointer += 1; pointer - 1
        })) = r(k)
        ({
          k += 1; k - 1
        })
      }
    }
    DbKit.closeQuietly(st)
    return result
  }

  /**
   * Execute a batch of SQL INSERT, UPDATE, or DELETE queries.
   * Example:
   * <pre>
   * int[] result = DbPro.use().batch("myConfig", sqlList, 500);
   * </pre>
   * @param sqlList The SQL list to execute.
   * @param batchSize batch size.
   * @return The number of rows updated per statement
   */
  def batch(sqlList: List[String], batchSize: Int): Array[Int] = {
    var conn: Connection = null
    var autoCommit: Boolean = null
    try {
      conn = config.getConnection
      autoCommit = conn.getAutoCommit
      conn.setAutoCommit(false)
      return batch(config, conn, sqlList, batchSize)
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      if (autoCommit != null) try {
        conn.setAutoCommit(autoCommit)
      }
      catch {
        case e: Exception => {
          e.printStackTrace
        }
      }
      config.close(conn)
    }
  }

  private final val config: Config = null
}




