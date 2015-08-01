/**
 * Copyright (c) 2015 Translation Exchange, Inc. All rights reserved.
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
 * 
 * Usage:
 * 
 * <tml:scripts/> 
 * 
 */
package com.translationexchange.j2ee.tags;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.translationexchange.core.Application;
import com.translationexchange.core.Language;
import com.translationexchange.core.Session;

public class ScriptsTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private void writeCSS(JspWriter writer, Session session) throws Exception {
		Application application = session.getApplication();
		out(writer, "<style type='text/css'>");
		out(writer, application.getCss());
		out(writer, "</style>");
	}
	
	private void writeJS(JspWriter writer, Session session) throws Exception {
		Application application = session.getApplication();
		Language language = session.getCurrentLanguage();
		String source = session.getCurrentSource();

		out(writer, "<script>");
		out(writer, "(function() {");
		out(writer, "	if (window.tml_already_initialized) return;");
		out(writer, "	window.tml_already_initialized = true;");
		out(writer, "	var script = window.document.createElement('script');");
		out(writer, "   script.setAttribute('id', 'tml-tools');");
		out(writer, "   script.setAttribute('type', 'application/javascript');");
		out(writer, "   script.setAttribute('src', '" + application.getTools("javascript") + "');");
		out(writer, "   script.setAttribute('charset', 'UTF-8');");
		out(writer, "   script.onload = function() {");
		out(writer, "     Tml.Utils.insertCSS(window.document, '" + application.getTools("stylesheet") + "', false);");
		out(writer, "     Tml.app_key = '" + application.getKey() + "';");
		out(writer, "     Tml.host = '" + application.getTools("host") + "';");
		out(writer, "     Tml.locale = '" + language.getLocale() + "';");
		out(writer, "     Tml.current_source = '" + source + "';");
		
		if (application.isFeatureEnabled("shortcuts")) {
	        Iterator<Map.Entry<String, String>> entries = application.getShortcuts().entrySet().iterator();
	        while (entries.hasNext()) { 
                Map.Entry<String, String> entry = (Map.Entry<String, String>) entries.next();
        		out(writer, "     shortcut.add('" + entry.getKey() + "', function() {");
        		out(writer, "       " + entry.getValue());
        		out(writer, "     });");
	        }
		}

		out(writer, "     if (typeof(tml_on_ready) === 'function') tml_on_ready(); ");
		out(writer, "   };");
		out(writer, "   window.document.getElementsByTagName('head')[0].appendChild(script);");
		out(writer, "})();");
		out(writer, "</script>");		
	}
	
	public int doStartTag() throws JspException {
        try {
            JspWriter writer = pageContext.getOut();

    	    Session tmlSession = getTmlSession();
    	    if (tmlSession == null)
    	    	return EVAL_PAGE;
    	    
    	    writeCSS(writer, tmlSession);
    	    writeJS(writer, tmlSession);
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        } finally {
        }
        return EVAL_PAGE;
    }
	
}






