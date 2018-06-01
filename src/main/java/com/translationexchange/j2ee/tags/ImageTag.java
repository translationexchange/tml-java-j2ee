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

import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;

public class ImageTag extends TrTag {

  private static final long serialVersionUID = 1L;

  public static List<String> getImageAttributes() {
    return Utils.buildStringList("src", "title", "label", "style", "class", "onclick");
  }

  public static String getImageHtml(Session session, Map<String, Object> options) {
    StringBuffer html = new StringBuffer();

    html.append("<img ");

    // TODO: add support for locale based images
    // logo_ru.png
    // logo_es.png
    // logo_rtl.png

    for (String attr : getImageAttributes()) {
      if (options.get(attr) == null)
        continue;
      html.append(attr + "='" + options.get(attr).toString().replace("'", "\"") + "' ");
    }

    html.append("/>");

    return html.toString();
  }

  public List<String> getExcludedAttributes() {
    return getImageAttributes();
  }

  public int doEndTag() throws JspException {
    try {
      Session session = getTmlSession();
      if (session == null)
        return EVAL_PAGE;

      translateAttributes(session, Utils.buildStringList("title", "label"));
      out(getImageHtml(session, getDynamicAttributes()));

    } catch (Exception e) {
      Tml.getLogger().logException(e);
      throw new JspException(e.getMessage());
    } finally {
      reset();
    }
    return EVAL_PAGE;
  }
}
