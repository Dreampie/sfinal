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

import java.util.HashMap
import java.util.Map

/**
 * TableMapping save the mapping between model class and table.
 */
object TableMapping {
  def me: TableMapping = {
    return me
  }

  private var me: TableMapping = new TableMapping
}

class TableMapping {
  private def this() {
    this()
  }

  def putTable(table: Table) {
    modelToTableMap.put(table.getModelClass, table)
  }

  @SuppressWarnings(Array("rawtypes")) def getTable(modelClass: Class[_ <: Model[_ <: Model[_ <: Model[_]]]]): Table = {
    val table: Table = modelToTableMap.get(modelClass)
    if (table == null) throw new RuntimeException("The Table mapping of model: " + modelClass.getName + " not exists. Please add mapping to ActiveRecordPlugin: activeRecordPlugin.addMapping(tableName, YourModel.class).")
    return table
  }

  private final val modelToTableMap: Map[Class[_ <: Model[_]], Table] = new HashMap[Class[_ <: Model[_]], Table]
}



