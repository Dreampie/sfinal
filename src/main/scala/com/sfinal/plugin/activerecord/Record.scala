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
import java.util.HashMap
import java.util.Map
import java.util.Set
import java.util.Map.Entry

/**
 * Record
 */
object Record {
  private final val serialVersionUID: Long = 905784513600884082L
}

class Record extends Serializable {
  /**
   * Set the containerFactory by configName.
   * Only the containerFactory of the config used by Record for getColumnsMap()
   * @param configName the config name
   */
  def setContainerFactoryByConfigName(configName: String): Record = {
    val config: Config = DbKit.getConfig(configName)
    if (config == null) throw new IllegalArgumentException("Config not found: " + configName)
    processColumnsMap(config)
    return this
  }

  private[activerecord] def setColumnsMap(columns: Map[String, AnyRef]) {
    this.columns = columns
  }

  @SuppressWarnings(Array("unchecked")) private def processColumnsMap(config: Config) {
    if (columns == null || columns.size == 0) {
      columns = config.containerFactory.getColumnsMap
    }
    else {
      val columnsOld: Map[String, AnyRef] = columns
      columns = config.containerFactory.getColumnsMap
      columns.putAll(columnsOld)
    }
  }

  /**
   * Return columns map.
   */
  @SuppressWarnings(Array("unchecked")) def getColumns: Map[String, AnyRef] = {
    if (columns == null) {
      if (DbKit.config == null) columns = DbKit.brokenConfig.containerFactory.getColumnsMap
      else columns = DbKit.config.containerFactory.getColumnsMap
    }
    return columns
  }

  /**
   * Set columns value with map.
   * @param columns the columns map
   */
  def setColumns(columns: Map[String, AnyRef]): Record = {
    this.getColumns.putAll(columns)
    return this
  }

  /**
   * Set columns value with record.
   * @param record the record
   */
  def setColumns(record: Record): Record = {
    getColumns.putAll(record.getColumns)
    return this
  }

  /**
   * Remove attribute of this record.
   * @param column the column name of the record
   */
  def remove(column: String): Record = {
    getColumns.remove(column)
    return this
  }

  /**
   * Remove columns of this record.
   * @param columns the column names of the record
   */
  def remove(columns: String*): Record = {
    if (columns != null) for (c <- columns) this.getColumns.remove(c)
    return this
  }

  /**
   * Remove columns if it is null.
   */
  def removeNullValueColumns: Record = {
    {
      val it: Iterator[Map.Entry[String, AnyRef]] = getColumns.entrySet.iterator
      while (it.hasNext) {
        val e: Map.Entry[String, AnyRef] = it.next
        if (e.getValue == null) {
          it.remove
        }
      }
    }
    return this
  }

  /**
   * Keep columns of this record and remove other columns.
   * @param columns the column names of the record
   */
  def keep(columns: String*): Record = {
    if (columns != null && columns.length > 0) {
      val newColumns: Map[String, AnyRef] = new HashMap[String, AnyRef](columns.length)
      for (c <- columns) if (this.getColumns.containsKey(c)) newColumns.put(c, this.getColumns.get(c))
      this.getColumns.clear
      this.getColumns.putAll(newColumns)
    }
    else this.getColumns.clear
    return this
  }

  /**
   * Keep column of this record and remove other columns.
   * @param column the column names of the record
   */
  def keep(column: String): Record = {
    if (getColumns.containsKey(column)) {
      val keepIt: AnyRef = getColumns.get(column)
      getColumns.clear
      getColumns.put(column, keepIt)
    }
    else getColumns.clear
    return this
  }

  /**
   * Remove all columns of this record.
   */
  def clear: Record = {
    getColumns.clear
    return this
  }

  /**
   * Set column to record.
   * @param column the column name
   * @param value the value of the column
   */
  def set(column: String, value: AnyRef): Record = {
    getColumns.put(column, value)
    return this
  }

  /**
   * Get column of any mysql type
   */
  @SuppressWarnings(Array("unchecked")) def get(column: String): T = {
    return getColumns.get(column).asInstanceOf[T]
  }

  /**
   * Get column of any mysql type. Returns defaultValue if null.
   */
  @SuppressWarnings(Array("unchecked")) def get(column: String, defaultValue: AnyRef): T = {
    val result: AnyRef = getColumns.get(column)
    return (if (result != null) result else defaultValue).asInstanceOf[T]
  }

  /**
   * Get column of mysql type: varchar, char, enum, set, text, tinytext, mediumtext, longtext
   */
  def getStr(column: String): String = {
    return getColumns.get(column).asInstanceOf[String]
  }

  /**
   * Get column of mysql type: int, integer, tinyint(n) n > 1, smallint, mediumint
   */
  def getInt(column: String): Integer = {
    return getColumns.get(column).asInstanceOf[Integer]
  }

  /**
   * Get column of mysql type: bigint
   */
  def getLong(column: String): Long = {
    return getColumns.get(column).asInstanceOf[Long]
  }

  /**
   * Get column of mysql type: unsigned bigint
   */
  def getBigInteger(column: String): BigInteger = {
    return getColumns.get(column).asInstanceOf[BigInteger]
  }

  /**
   * Get column of mysql type: date, year
   */
  def getDate(column: String): Date = {
    return getColumns.get(column).asInstanceOf[Date]
  }

  /**
   * Get column of mysql type: time
   */
  def getTime(column: String): Time = {
    return getColumns.get(column).asInstanceOf[Time]
  }

  /**
   * Get column of mysql type: timestamp, datetime
   */
  def getTimestamp(column: String): Timestamp = {
    return getColumns.get(column).asInstanceOf[Timestamp]
  }

  /**
   * Get column of mysql type: real, double
   */
  def getDouble(column: String): Double = {
    return getColumns.get(column).asInstanceOf[Double]
  }

  /**
   * Get column of mysql type: float
   */
  def getFloat(column: String): Float = {
    return getColumns.get(column).asInstanceOf[Float]
  }

  /**
   * Get column of mysql type: bit, tinyint(1)
   */
  def getBoolean(column: String): Boolean = {
    return getColumns.get(column).asInstanceOf[Boolean]
  }

  /**
   * Get column of mysql type: decimal, numeric
   */
  def getBigDecimal(column: String): BigDecimal = {
    return getColumns.get(column).asInstanceOf[BigDecimal]
  }

  /**
   * Get column of mysql type: binary, varbinary, tinyblob, blob, mediumblob, longblob
   * I have not finished the test.
   */
  def getBytes(column: String): Array[Byte] = {
    return getColumns.get(column).asInstanceOf[Array[Byte]]
  }

  /**
   * Get column of any type that extends from Number
   */
  def getNumber(column: String): Number = {
    return getColumns.get(column).asInstanceOf[Number]
  }

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append(super.toString).append(" {")
    var first: Boolean = true
    import scala.collection.JavaConversions._
    for (e <- getColumns.entrySet) {
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
    if (!(o.isInstanceOf[Record])) return false
    if (o eq this) return true
    return this.getColumns == (o.asInstanceOf[Record]).getColumns
  }

  override def hashCode: Int = {
    return if (getColumns == null) 0 else getColumns.hashCode
  }

  /**
   * Return column names of this record.
   */
  def getColumnNames: Array[String] = {
    val attrNameSet: Set[String] = getColumns.keySet
    return attrNameSet.toArray(new Array[String](attrNameSet.size))
  }

  /**
   * Return column values of this record.
   */
  def getColumnValues: Array[AnyRef] = {
    val attrValueCollection: Collection[AnyRef] = getColumns.values
    return attrValueCollection.toArray(new Array[AnyRef](attrValueCollection.size))
  }

  /**
   * Return json string of this record.
   */
  def toJson: String = {
    return com.jfinal.kit.JsonKit.toJson(getColumns, 4)
  }

  private var columns: Map[String, AnyRef] = null
}





