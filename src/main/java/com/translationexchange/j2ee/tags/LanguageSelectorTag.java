/*
 * Copyright (c) 2018 Translation Exchange, Inc. All rights reserved.
 *
 *  _______                  _       _   _             ______          _
 * |__   __|                | |     | | (_)           |  ____|        | |
 *    | |_ __ __ _ _ __  ___| | __ _| |_ _  ___  _ __ | |__  __  _____| |__   __ _ _ __   __ _  ___
 *    | | '__/ _` | '_ \/ __| |/ _` | __| |/ _ \| '_ \|  __| \ \/ / __| '_ \ / _` | '_ \ / _` |/ _ \
 *    | | | | (_| | | | \__ \ | (_| | |_| | (_) | | | | |____ >  < (__| | | | (_| | | | | (_| |  __/
 *    |_|_|  \__,_|_| |_|___/_|\__,_|\__|_|\___/|_| |_|______/_/\_\___|_| |_|\__,_|_| |_|\__, |\___|
 *                                                                                        __/ |
 *                                                                                       |___/
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Michael Berkovich
 */

package com.translationexchange.j2ee.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.translationexchange.core.Session;

public class LanguageSelectorTag extends TagSupport implements DynamicAttributes {

  private static final long serialVersionUID = 1L;

  private String type;
  private String element;
  private String className;
  private String style;
  private boolean toggle = true;
  private String toggleLabel;
  private String toggleLabelCancel;
  private boolean poweredBy = true;

  public boolean isToggle() {
    return toggle;
  }

  public void setToggle(boolean toggle) {
    this.toggle = toggle;
  }

  public String getToggleLabel() {
    if (toggleLabel == null) return "Activate Translate Mode";
    return toggleLabel;
  }

  public void setToggleLabel(String toggleLabel) {
    this.toggleLabel = toggleLabel;
  }

  public String getToggleLabelCancel() {
    if (toggleLabel == null) return "Deactivate translation mode";
    return toggleLabelCancel;
  }

  public void setToggleLabelCancel(String toggleLabelCancel) {
    this.toggleLabelCancel = toggleLabelCancel;
  }

  public boolean isPoweredBy() {
    return poweredBy;
  }

  public void setPoweredBy(boolean poweredBy) {
    this.poweredBy = poweredBy;
  }

  public String getStyle() {
    return style;
  }

  public void setStyle(String style) {
    this.style = style;
  }

  public String getClassName() {
    if (className == null) return "dropdown";
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getElement() {
    if (element == null) return "div";
    return element;
  }

  public void setElement(String element) {
    this.element = element;
  }

  public String getType() {
    if (type == null) return "default";
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int doStartTag() throws JspException {
    try {
      Session session = getTmlSession();
      if (session == null)
        return EVAL_PAGE;

      out("<" + getElement() + " data-tml-language-selector='" + getType() + "' ");
      if (isToggle())
        out("data-tml-toggle='true' ");

      if (isPoweredBy())
        out("data-tml-powered-by='true' ");

      out("data-tml-toggle_label='" + getToggleLabel() + "' ");
      out("data-tml-toggle_label_cancel='" + getToggleLabelCancel() + "' ");

      out("data-tml-class='" + getClassName() + "' ");
      out("data-tml-style='" + getStyle() + "' ");

      out("></" + getElement() + ">");
    } catch (Exception e) {
      throw new JspException(e.getMessage());
    }
    return EVAL_PAGE;
  }

}
