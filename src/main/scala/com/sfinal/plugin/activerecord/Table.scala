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

import java.util.Collections
import java.util.Map
import com.sfinal.kit.StrKit

/**
 * Table save the table meta info like column name and column type.
 */
class Table {
  def this(name: String, modelClass: Class[_ <: Model[_]]) {
    this()
    if (StrKit.isBlank(name)) throw new IllegalArgumentException("Table name can not be blank.")
    if (modelClass == null) throw new IllegalArgumentException("Model class can not be null.")
    this.name = name.trim
    this.modelClass = modelClass
  }

  def this(name: String, primaryKey: String, modelClass: Class[_ <: Model[_]]) {
    this()
    if (StrKit.isBlank(name)) throw new IllegalArgumentException("Table name can not be blank.")
    if (StrKit.isBlank(primaryKey)) throw new IllegalArgumentException("Primary key can not be blank.")
    if (modelClass == null) throw new IllegalArgumentException("Model class can not be null.")
    this.name = name.trim
    setPrimaryKey(primaryKey.trim)
    this.modelClass = modelClass
  }

  private[activerecord] def setPrimaryKey(primaryKey: String) {
    val keyArr: Array[String] = primaryKey.split(",")
    if (keyArr.length > 2) throw new IllegalArgumentException("Supports only two primary key for Composite primary key.")
    if (keyArr.length > 1) {
      if (StrKit.isBlank(keyArr(0)) || StrKit.isBlank(keyArr(1))) throw new IllegalArgumentException("The composite primary key can not be blank.")
      this.primaryKey = keyArr(0).trim
      this.secondaryKey = keyArr(1).trim
    }
    else {
      this.primaryKey = primaryKey
    }
  }

  private[activerecord] def setColumnTypeMap(columnTypeMap: Map[String, Class[_]]) {
    if (columnTypeMap == null) throw new IllegalArgumentException("columnTypeMap can not be null")
    this.columnTypeMap = columnTypeMap
  }

  def getName: String = {
    return name
  }

  private[activerecord] def setColumnType(columnLabel: String, columnType: Class[_]) {
    columnTypeMap.put(columnLabel, columnType)
  }

  def getColumnType(columnLabel: String): Class[_] = {
    return columnTypeMap.get(columnLabel)
  }

  /**
   * Model.save() need know what columns belongs to himself that he can saving to db.
   * Think about auto saving the related table's column in the future.
   */
  def hasColumnLabel(columnLabel: String): Boolean = {
    return columnTypeMap.containsKey(columnLabel)
  }

  /**
   * update() and delete() need this method.
   */
  def getPrimaryKey: String = {
    return primaryKey
  }

  def getSecondaryKey: String = {
    return secondaryKey
  }

  def getModelClass: Class[_ <: Model[_]] = {
    return modelClass
  }

  def getColumnTypeMap: Map[String, Class[_]] = {
    return Collections.unmodifiableMap(columnTypeMap)
  }

  private var name: String = null
  private var primaryKey: String = null
  private var secondaryKey: String = null
  private var columnTypeMap: Map[String, Class[_]] = null
  private var modelClass: Class[_ <: Model[_]] = null
}







