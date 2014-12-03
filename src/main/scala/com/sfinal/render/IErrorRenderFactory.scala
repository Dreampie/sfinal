package com.sfinal.render

/**
 * Created by ice on 14-12-3.
 */
trait IErrorRenderFactory {
  def render(errorCode: Int, view: Nothing): Render
}
