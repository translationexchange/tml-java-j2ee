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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.translationexchange.core.Session;
import com.translationexchange.core.Utils;
import com.translationexchange.core.languages.Language;

public class FeaturedLanguagesTag extends TagSupport {

  private static final long serialVersionUID = 1L;
  private static final int LIST_LIMIT = 8;

  public int doStartTag() throws JspException {
    try {
      JspWriter out = pageContext.getOut();

      Session tmlSession = getTmlSession();
      if (tmlSession == null)
        return EVAL_PAGE;

      List<String> links = new ArrayList<String>();
      List<Language> languages = tmlSession.getApplication().getFeaturedLanguages();

      if (languages != null && languages.size() > 0) {
        for (int i = 0; i < Math.min(LIST_LIMIT, languages.size()); i++) {
          Language language = languages.get(i);
          links.add("<a href='#' onClick='Tml.UI.LanguageSelector.change(\"" + language.getLocale() + "\")'>" + language.getNativeName() + "</a>");
        }

        out.write(Utils.join(links, "&nbsp; &middot; &nbsp;"));
        if (languages.size() > LIST_LIMIT) {
          out.write("&nbsp; &nbsp; <a href='#' onClick='Tml.UI.LanguageSelector.show()'>&raquo;</a>");
        }
      }
    } catch (Exception e) {
      throw new JspException(e.getMessage());
    }
    return EVAL_PAGE;
  }
}
