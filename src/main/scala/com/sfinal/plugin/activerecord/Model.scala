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

import java.io.Serializable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList
import java.util.Iterator
import java.util.List
import java.util.Map
import java.util.Set
import java.util.Map.Entry
import com.sfinal.plugin.activerecord.{ModelBuilder, TableMapping, Table}
import com.sfinal.plugin.activerecord.cache.ICache
import com.sfinal.plugin.activerecord.DbKit.NULL_PARA_ARRAY

/**
 * Model.
 * <p>
 * A clever person solves a problem.
 * A wise person avoids it.
 * A stupid person makes it.
 */
object Model {
  private final val serialVersionUID: Long = -990334519496260591L
}

abstract class Model extends Serializable {
  private def getAttrsMap: Map[String, AnyRef] = {
    val config: Config = getConfig
    if (config == null) return DbKit.brokenConfig.containerFactory.getAttrsMap
    return config.containerFactory.getAttrsMap
  }

  private def getModifyFlag: Set[String] = {
    if (modifyFlag == null) {
      val config: Config = getConfig
      if (config == null) modifyFlag = DbKit.brokenConfig.containerFactory.getModifyFlagSet
      else modifyFlag = config.containerFactory.getModifyFlagSet
    }
    return modifyFlag
  }

  private def getConfig: Config = {
    return DbKit.getConfig(getClass)
  }

  private def getTable: Table = {
    return TableMapping.me.getTable(getClass)
  }

  /**
   * Set attribute to model.
   * @param attr the attribute name of the model
   * @param value the value of the attribute
   * @return this model
   * @throws ActiveRecordException if the attribute is not exists of the model
   */
  def set(attr: String, value: AnyRef): M = {
    if (getTable.hasColumnLabel(attr)) {
      attrs.put(attr, value)
      getModifyFlag.add(attr)
      return this.asInstanceOf[M]
    }
    throw new ActiveRecordException("The attribute name is not exists: " + attr)
  }

  /**
   * Put key value pair to the model when the key is not attribute of the model.
   */
  def put(key: String, value: AnyRef): M = {
    attrs.put(key, value)
    return this.asInstanceOf[M]
  }

  /**
   * Get attribute of any mysql type
   */
  def get(attr: String): T = {
    return (attrs.get(attr)).asInstanceOf[T]
  }

  /**
   * Get attribute of any mysql type. Returns defaultValue if null.
   */
  def get(attr: String, defaultValue: AnyRef): T = {
    val result: AnyRef = attrs.get(attr)
    return (if (result != null) result else defaultValue).asInstanceOf[T]
  }

  /**
   * Get attribute of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
   */
  def getStr(attr: String): String = {
    return attrs.get(attr).asInstanceOf[String]
  }

  /**
   * Get attribute of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
   */
  def getInt(attr: String): Integer = {
    return attrs.get(attr).asInstanceOf[Integer]
  }

  /**
   * Get attribute of mysql type: bigint, unsign int
   */
  def getLong(attr: String): Long = {
    return attrs.get(attr).asInstanceOf[Long]
  }

  /**
   * Get attribute of mysql type: unsigned bigint
   */
  def getBigInteger(attr: String): BigInteger = {
    return attrs.get(attr).asInstanceOf[BigInteger]
  }

  /**
   * Get attribute of mysql type: date, year
   */
  def getDate(attr: String): Date = {
    return attrs.get(attr).asInstanceOf[Date]
  }

  /**
   * Get attribute of mysql type: time
   */
  def getTime(attr: String): Time = {
    return attrs.get(attr).asInstanceOf[Time]
  }

  /**
   * Get attribute of mysql type: timestamp, datetime
   */
  def getTimestamp(attr: String): Timestamp = {
    return attrs.get(attr).asInstanceOf[Timestamp]
  }

  /**
   * Get attribute of mysql type: real, double
   */
  def getDouble(attr: String): Double = {
    return attrs.get(attr).asInstanceOf[Double]
  }

  /**
   * Get attribute of mysql type: float
   */
  def getFloat(attr: String): Float = {
    return attrs.get(attr).asInstanceOf[Float]
  }

  /**
   * Get attribute of mysql type: bit, tinyint(1)
   */
  def getBoolean(attr: String): Boolean = {
    return attrs.get(attr).asInstanceOf[Boolean]
  }

  /**
   * Get attribute of mysql type: decimal, numeric
   */
  def getBigDecimal(attr: String): BigDecimal = {
    return attrs.get(attr).asInstanceOf[BigDecimal]
  }

  /**
   * Get attribute of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
   */
  def getBytes(attr: String): Array[Byte] = {
    return attrs.get(attr).asInstanceOf[Array[Byte]]
  }

  /**
   * Get attribute of any type that extends from Number
   */
  def getNumber(attr: String): Number = {
    return attrs.get(attr).asInstanceOf[Number]
  }

  /**
   * Paginate.
   * @param pageNumber the page number
   * @param pageSize the page size
   * @param select the select part of the sql statement
   * @param sqlExceptSelect the sql statement excluded select part
   * @param paras the parameters of sql
   * @return Page
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[M] = {
    val config: Config = getConfig
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

  private def paginate(config: Config, conn: Connection, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[M] = {
    if (pageNumber < 1 || pageSize < 1) throw new ActiveRecordException("pageNumber and pageSize must be more than 0")
    if (config.dialect.isTakeOverModelPaginate) return config.dialect.takeOverModelPaginate(conn, getClass, pageNumber, pageSize, select, sqlExceptSelect, paras)
    var totalRow: Long = 0
    var totalPage: Int = 0
    val result: List[_] = Db.query(config, conn, "select count(*) " + DbKit.replaceFormatSqlOrderBy(sqlExceptSelect), paras)
    val size: Int = result.size
    if (size == 1) totalRow = (result.get(0).asInstanceOf[Number]).longValue
    else if (size > 1) totalRow = result.size
    else return new Page[M](new ArrayList[M](0), pageNumber, pageSize, 0, 0)
    totalPage = (totalRow / pageSize).asInstanceOf[Int]
    if (totalRow % pageSize != 0) {
      totalPage += 1
    }
    val sql: StringBuilder = new StringBuilder
    config.dialect.forPaginate(sql, pageNumber, pageSize, select, sqlExceptSelect)
    val list: List[M] = find(conn, sql.toString, paras)
    return new Page[M](list, pageNumber, pageSize, totalPage, totalRow.asInstanceOf[Int])
  }

  /**
   * @see #paginate(int, int, String, String, Object...)
   */
  def paginate(pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[M] = {
    return paginate(pageNumber, pageSize, select, sqlExceptSelect, NULL_PARA_ARRAY)
  }

  /**
   * Return attribute Map.
   * <p>
   * Danger! The update method will ignore the attribute if you change it directly.
   * You must use set method to change attribute that update method can handle it.
   */
  protected def getAttrs: Map[String, AnyRef] = {
    return attrs
  }

  /**
   * Return attribute Set.
   */
  def getAttrsEntrySet: Set[Map.Entry[String, AnyRef]] = {
    return attrs.entrySet
  }

  /**
   * Save model.
   */
  def save: Boolean = {
    val config: Config = getConfig
    val table: Table = getTable
    val sql: StringBuilder = new StringBuilder
    val paras: List[AnyRef] = new ArrayList[AnyRef]
    config.dialect.forModelSave(table, attrs, sql, paras)
    var conn: Connection = null
    var pst: PreparedStatement = null
    var result: Int = 0
    try {
      conn = config.getConnection
      if (config.dialect.isOracle) pst = conn.prepareStatement(sql.toString, Array[String](table.getPrimaryKey))
      else pst = conn.prepareStatement(sql.toString, Statement.RETURN_GENERATED_KEYS)
      config.dialect.fillStatement(pst, paras)
      result = pst.executeUpdate
      getGeneratedKey(pst, table)
      getModifyFlag.clear
      return result >= 1
    }
    catch {
      case e: Exception => {
        throw new ActiveRecordException(e)
      }
    }
    finally {
      config.close(pst, conn)
    }
  }

  /**
   * Get id after save method.
   */
  private def getGeneratedKey(pst: PreparedStatement, table: Table) {
    val pKey: String = table.getPrimaryKey
    if (get(pKey) == null || getConfig.dialect.isOracle) {
      val rs: ResultSet = pst.getGeneratedKeys
      if (rs.next) {
        val colType: Class[_] = table.getColumnType(pKey)
        if (colType eq classOf[Integer] || colType eq classOf[Int]) set(pKey, rs.getInt(1))
        else if (colType eq classOf[Long] || colType eq classOf[Long]) set(pKey, rs.getLong(1))
        else set(pKey, rs.getObject(1))
        rs.close
      }
    }
  }

  /**
   * Delete model.
   */
  def delete: Boolean = {
    val table: Table = getTable
    val id: AnyRef = attrs.get(table.getPrimaryKey)
    if (id == null) throw new ActiveRecordException("You can't delete model without id.")
    return deleteById(table, id)
  }

  /**
   * Delete model by id.
   * @param id the id value of the model
   * @return true if delete succeed otherwise false
   */
  def deleteById(id: AnyRef): Boolean = {
    if (id == null) throw new IllegalArgumentException("id can not be null")
    return deleteById(getTable, id)
  }

  private def deleteById(table: Table, id: AnyRef): Boolean = {
    val config: Config = getConfig
    var conn: Connection = null
    try {
      conn = config.getConnection
      val sql: String = config.dialect.forModelDeleteById(table)
      return Db.update(config, conn, sql, id) >= 1
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
   * Update model.
   */
  def update: Boolean = {
    if (getModifyFlag.isEmpty) return false
    val table: Table = getTable
    val pKey: String = table.getPrimaryKey
    val id: AnyRef = attrs.get(pKey)
    if (id == null) throw new ActiveRecordException("You can't update model without Primary Key.")
    val config: Config = getConfig
    val sql: StringBuilder = new StringBuilder
    val paras: List[AnyRef] = new ArrayList[AnyRef]
    config.dialect.forModelUpdate(table, attrs, getModifyFlag, pKey, id, sql, paras)
    if (paras.size <= 1) {
      return false
    }
    var conn: Connection = null
    try {
      conn = config.getConnection
      val result: Int = Db.update(config, conn, sql.toString, paras.toArray)
      if (result >= 1) {
        getModifyFlag.clear
        return true
      }
      return false
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
   * Find model.
   */
  private def find(conn: Connection, sql: String, paras: AnyRef*): List[M] = {
    val config: Config = getConfig
    val modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]] = getClass
    if (config.devMode) checkTableName(modelClass, sql)
    val pst: PreparedStatement = conn.prepareStatement(sql)
    config.dialect.fillStatement(pst, paras)
    val rs: ResultSet = pst.executeQuery
    val result: List[M] = ModelBuilder.build(rs, modelClass)
    DbKit.closeQuietly(rs, pst)
    return result
  }

  /**
   * Find model.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return the list of Model
   */
  def find(sql: String, paras: AnyRef*): List[M] = {
    val config: Config = getConfig
    var conn: Connection = null
    try {
      conn = config.getConnection
      return find(conn, sql, paras)
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
   * Check the table name. The table name must in sql.
   */
  private def checkTableName(modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]], sql: String) {
    val table: Table = TableMapping.me.getTable(modelClass)
    if (!sql.toLowerCase.contains(table.getName.toLowerCase)) throw new ActiveRecordException("The table name: " + table.getName + " not in your sql.")
  }

  /**
   * @see #find(String, Object...)
   */
  def find(sql: String): List[M] = {
    return find(sql, NULL_PARA_ARRAY)
  }

  /**
   * Find first model. I recommend add "limit 1" in your sql.
   * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
   * @param paras the parameters of sql
   * @return Model
   */
  def findFirst(sql: String, paras: AnyRef*): M = {
    val result: List[M] = find(sql, paras)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * @see #findFirst(String, Object...)
   * @param sql an SQL statement
   */
  def findFirst(sql: String): M = {
    val result: List[M] = find(sql, NULL_PARA_ARRAY)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * Find model by id.
   * @param id the id value of the model
   */
  def findById(id: AnyRef): M = {
    return findById(id, "*")
  }

  /**
   * Find model by id. Fetch the specific columns only.
   * Example: User user = User.dao.findById(15, "name, age");
   * @param id the id value of the model
   * @param columns the specific columns separate with comma character ==> ","
   */
  def findById(id: AnyRef, columns: String): M = {
    val table: Table = getTable
    val sql: String = getConfig.dialect.forModelFindById(table, columns)
    val result: List[M] = find(sql, id)
    return if (result.size > 0) result.get(0) else null
  }

  /**
   * Set attributes with other model.
   * @param model the Model
   * @return this Model
   */
  def setAttrs(model: M): M = {
    return setAttrs(model.getAttrs)
  }

  /**
   * Set attributes with Map.
   * @param attrs attributes of this model
   * @return this Model
   */
  def setAttrs(attrs: Map[String, AnyRef]): M = {
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) set(e.getKey, e.getValue)
    return this.asInstanceOf[M]
  }

  /**
   * Remove attribute of this model.
   * @param attr the attribute name of the model
   * @return this model
   */
  def remove(attr: String): M = {
    attrs.remove(attr)
    getModifyFlag.remove(attr)
    return this.asInstanceOf[M]
  }

  /**
   * Remove attributes of this model.
   * @param attrs the attribute names of the model
   * @return this model
   */
  def remove(attrs: String*): M = {
    if (attrs != null) for (a <- attrs) {
      this.attrs.remove(a)
      this.getModifyFlag.remove(a)
    }
    return this.asInstanceOf[M]
  }

  /**
   * Remove attributes if it is null.
   * @return this model
   */
  def removeNullValueAttrs: M = {
    {
      val it: Iterator[Map.Entry[String, AnyRef]] = attrs.entrySet.iterator
      while (it.hasNext) {
        val e: Map.Entry[String, AnyRef] = it.next
        if (e.getValue == null) {
          it.remove
          getModifyFlag.remove(e.getKey)
        }
      }
    }
    return this.asInstanceOf[M]
  }

  /**
   * Keep attributes of this model and remove other attributes.
   * @param attrs the attribute names of the model
   * @return this model
   */
  def keep(attrs: String*): M = {
    if (attrs != null && attrs.length > 0) {
      val config: Config = getConfig
      val newAttrs: Map[String, AnyRef] = config.containerFactory.getAttrsMap
      val newModifyFlag: Set[String] = config.containerFactory.getModifyFlagSet
      for (a <- attrs) {
        if (this.attrs.containsKey(a)) newAttrs.put(a, this.attrs.get(a))
        if (this.getModifyFlag.contains(a)) newModifyFlag.add(a)
      }
      this.attrs = newAttrs
      this.modifyFlag = newModifyFlag
    }
    else {
      this.attrs.clear
      this.getModifyFlag.clear
    }
    return this.asInstanceOf[M]
  }

  /**
   * Keep attribute of this model and remove other attributes.
   * @param attr the attribute name of the model
   * @return this model
   */
  def keep(attr: String): M = {
    if (attrs.containsKey(attr)) {
      val keepIt: AnyRef = attrs.get(attr)
      val keepFlag: Boolean = getModifyFlag.contains(attr)
      attrs.clear
      getModifyFlag.clear
      attrs.put(attr, keepIt)
      if (keepFlag) getModifyFlag.add(attr)
    }
    else {
      attrs.clear
      getModifyFlag.clear
    }
    return this.asInstanceOf[M]
  }

  /**
   * Remove all attributes of this model.
   * @return this model
   */
  def clear: M = {
    attrs.clear
    getModifyFlag.clear
    return this.asInstanceOf[M]
  }

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append(super.toString).append(" {")
    var first: Boolean = true
    import scala.collection.JavaConversions._
    for (e <- attrs.entrySet) {
      if (first) first = false
      else sb.append(", ")
      var value: AnyRef = e.getValue
      if (value != null) value = value.toString
      sb.append(e.getKey).append(":").append(value)
    }
    sb.append("}")
    return sb.toString
  }

  override def equals(o: AnyRef): Boolean = {
    if (!(o.isInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]])) return false
    if (o eq this) return true
    return this.attrs == (o.asInstanceOf[Model[_ <: Model[_ <: Model[_ <: Model[_]]]]]).attrs
  }

  override def hashCode: Int = {
    return (if (attrs == null) 0 else attrs.hashCode) ^ (if (getModifyFlag == null) 0 else getModifyFlag.hashCode)
  }

  /**
   * Find model by cache.
   * @see #find(String, Object...)
   * @param cacheName the cache name
   * @param key the key used to get date from cache
   * @return the list of Model
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String, paras: AnyRef*): List[M] = {
    val cache: ICache = getConfig.getCache
    var result: List[M] = cache.get(cacheName, key)
    if (result == null) {
      result = find(sql, paras)
      cache.put(cacheName, key, result)
    }
    return result
  }

  /**
   * @see #findByCache(String, Object, String, Object...)
   */
  def findByCache(cacheName: String, key: AnyRef, sql: String): List[M] = {
    return findByCache(cacheName, key, sql, NULL_PARA_ARRAY)
  }

  /**
   * Paginate by cache.
   * @see #paginate(int, int, String, String, Object...)
   * @param cacheName the cache name
   * @param key the key used to get date from cache
   * @return Page
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String, paras: AnyRef*): Page[M] = {
    val cache: ICache = getConfig.getCache
    var result: Page[M] = cache.get(cacheName, key)
    if (result == null) {
      result = paginate(pageNumber, pageSize, select, sqlExceptSelect, paras)
      cache.put(cacheName, key, result)
    }
    return result
  }

  /**
   * @see #paginateByCache(String, Object, int, int, String, String, Object...)
   */
  def paginateByCache(cacheName: String, key: AnyRef, pageNumber: Int, pageSize: Int, select: String, sqlExceptSelect: String): Page[M] = {
    return paginateByCache(cacheName, key, pageNumber, pageSize, select, sqlExceptSelect, NULL_PARA_ARRAY)
  }

  /**
   * Return attribute names of this model.
   */
  def getAttrNames: Array[String] = {
    val attrNameSet: Set[String] = attrs.keySet
    return attrNameSet.toArray(new Array[String](attrNameSet.size))
  }

  /**
   * Return attribute values of this model.
   */
  def getAttrValues: Array[AnyRef] = {
    val attrValueCollection: Collection[AnyRef] = attrs.values
    return attrValueCollection.toArray(new Array[AnyRef](attrValueCollection.size))
  }

  /**
   * Return json string of this model.
   */
  def toJson: String = {
    return com.jfinal.kit.JsonKit.toJson(attrs, 4)
  }

  /**
   * Attributes of this model
   */
  private var attrs: Map[String, AnyRef] = getAttrsMap
  /**
   * Flag of column has been modified. update need this flag
   */
  private var modifyFlag: Set[String] = null
}



