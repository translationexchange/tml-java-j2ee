/**
 * Copyright (c) 2016 Translation Exchange, Inc. All rights reserved.
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
 */

package com.translationexchange.j2ee.tags;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.translationexchange.core.Session;
import com.translationexchange.core.languages.Language;

public class LanguageFlagTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public static String getLanguageFlagHtml(Language language, Session session, Map<String, Object> options) {
		if (!session.getApplication().isFeatureEnabled("language_flags")) {
			return "";
		}

		StringBuffer html = new StringBuffer();

		Map<String, Object> imageOptions = new HashMap<String, Object>();
		imageOptions.put("src", language.getFlagUrl());
		imageOptions.put("style", getStringAttribute(options, "style", "vertical-align:middle;"));
		imageOptions.put("title", language.getNativeName());
		
		html.append(ImageTag.getImageHtml(session, imageOptions));

		return html.toString();
	}
	
	public int doStartTag() throws JspException {
        try {
            Session session = getTmlSession();
    	    
            if (session == null)
    	    	return EVAL_PAGE;

    	    out(getLanguageFlagHtml(getLanguageAttribute(session), session, getDynamicAttributes()));
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        }
        return EVAL_PAGE;
    }
}
