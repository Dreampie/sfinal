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
package com.sfinal.server

import java.io.File
import java.io.IOException
import java.util.HashMap
import java.util.Map
import java.util.Timer
import java.util.TimerTask
import com.sfinal.kit.StrKit

/**
 * Scanner.
 */
abstract class Scanner {
  def this(rootDir: String, interval: Int) {
    this()
    if (StrKit.isBlank(rootDir)) throw new IllegalArgumentException("The parameter rootDir can not be blank.")
    this.rootDir = new File(rootDir)
    if (!this.rootDir.isDirectory) throw new IllegalArgumentException("The directory " + rootDir + " is not exists.")
    if (interval <= 0) throw new IllegalArgumentException("The parameter interval must more than zero.")
    this.interval = interval
  }

  def onChange

  private def working {
    scan(rootDir)
    compare
    preScan.clear
    preScan.putAll(curScan)
    curScan.clear
  }

  private def scan(file: File) {
    if (file == null || !file.exists) return
    if (file.isFile) {
      try {
        curScan.put(file.getCanonicalPath, new TimeSize(file.lastModified, file.length))
      }
      catch {
        case e: IOException => {
          e.printStackTrace
        }
      }
    }
    else if (file.isDirectory) {
      val fs: Array[File] = file.listFiles
      if (fs != null) for (f <- fs) scan(f)
    }
  }

  private def compare {
    if (preScan.size == 0) return
    if (!(preScan == curScan)) onChange
  }

  def start {
    if (!running) {
      timer = new Timer("JFinal-Scanner", true)
      task = new TimerTask {
        def run {
          working
        }
      }
      timer.schedule(task, 1010L * interval, 1010L * interval)
      running = true
    }
  }

  def stop {
    if (running) {
      timer.cancel
      task.cancel
      running = false
    }
  }

  private var timer: Timer = null
  private var task: TimerTask = null
  private var rootDir: File = null
  private var interval: Int = 0
  private var running: Boolean = false
  private final val preScan: Map[String, TimeSize] = new HashMap[String, TimeSize]
  private final val curScan: Map[String, TimeSize] = new HashMap[String, TimeSize]
}

class TimeSize {
  def this(time: Long, size: Long) {
    this()
    this.time = time
    this.size = size
  }

  override def hashCode: Int = {
    return (time ^ size).asInstanceOf[Int]
  }

  override def equals(o: AnyRef): Boolean = {
    if (o.isInstanceOf[TimeSize]) {
      val ts: TimeSize = o.asInstanceOf[TimeSize]
      return ts.time == this.time && ts.size == this.size
    }
    return false
  }

  override def toString: String = {
    return "[t=" + time + ", s=" + size + "]"
  }

  private[server] final val time: Long = 0L
  private[server] final val size: Long = 0L
}


