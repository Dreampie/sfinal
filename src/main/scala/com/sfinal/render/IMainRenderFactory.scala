package com.sfinal.render

/**
 * Created by ice on 14-12-3.
 */
trait IMainRenderFactory {
  /**
   * Return the render.
   * @param view the view for this render.
   */
  def render(view: String): Render

  /**
   * The extension of the view.
   * <p>
   * It must start with dot char "."
   * Example: ".html" or ".ftl"
   * </p>
   */
  def viewExtension: String
}
