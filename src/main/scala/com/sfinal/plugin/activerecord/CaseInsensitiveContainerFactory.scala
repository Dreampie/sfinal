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

import java.util.Collection
import java.util.HashMap
import java.util.HashSet
import java.util.Map
import java.util.Set

import com.sfinal.plugin.activerecord.IContainerFactory

@SuppressWarnings(Array("rawtypes", "unchecked")) object CaseInsensitiveContainerFactory {
  private def convertCase(key: AnyRef): AnyRef = {
    if (key.isInstanceOf[String]) return if (toLowerCase) (key.asInstanceOf[String]).toLowerCase else (key.asInstanceOf[String]).toUpperCase
    return key
  }

  private var toLowerCase: Boolean = false

  object CaseInsensitiveSet {
    private final val serialVersionUID: Long = 102410961064096233L
  }

  class CaseInsensitiveSet extends HashSet {
    override def add(e: AnyRef): Boolean = {
      return super.add(convertCase(e))
    }

    override def remove(e: AnyRef): Boolean = {
      return super.remove(convertCase(e))
    }

    override def contains(e: AnyRef): Boolean = {
      return super.contains(convertCase(e))
    }

    override def addAll(c: Collection[_]): Boolean = {
      var modified: Boolean = false
      import scala.collection.JavaConversions._
      for (o <- c) if (super.add(convertCase(o))) modified = true
      return modified
    }
  }

  object CaseInsensitiveMap {
    private final val serialVersionUID: Long = 6843981594457576677L
  }

  class CaseInsensitiveMap extends HashMap {
    override def get(key: AnyRef): AnyRef = {
      return super.get(convertCase(key))
    }

    override def containsKey(key: AnyRef): Boolean = {
      return super.containsKey(convertCase(key))
    }

    override def put(key: AnyRef, value: AnyRef): AnyRef = {
      return super.put(convertCase(key), value)
    }

    override def putAll(m: Map[_, _]) {
      import scala.collection.JavaConversions._
      for (e <- (m.entrySet).asInstanceOf[Set[Map.Entry[_, _]]]) super.put(convertCase(e.getKey), e.getValue)
    }

    override def remove(key: AnyRef): AnyRef = {
      return super.remove(convertCase(key))
    }
  }

}

@SuppressWarnings(Array("rawtypes", "unchecked")) class CaseInsensitiveContainerFactory extends IContainerFactory {
  def this() {
    this()
  }

  def this(toLowerCase: Boolean) {
    this()
    CaseInsensitiveContainerFactory.toLowerCase = toLowerCase
  }

  def getAttrsMap: Map[String, AnyRef] = {
    return new CaseInsensitiveContainerFactory.CaseInsensitiveMap
  }

  def getColumnsMap: Map[String, AnyRef] = {
    return new CaseInsensitiveContainerFactory.CaseInsensitiveMap
  }

  def getModifyFlagSet: Set[String] = {
    return new CaseInsensitiveContainerFactory.CaseInsensitiveSet
  }
}


