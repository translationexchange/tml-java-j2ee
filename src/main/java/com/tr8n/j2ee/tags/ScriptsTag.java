/*
 *  Copyright (c) 2014 Michael Berkovich, http://tr8nhub.com All rights reserved.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package com.tr8n.j2ee.tags;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.tr8n.core.Application;
import com.tr8n.core.Session;

public class ScriptsTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private void writeCSSFunction(JspWriter out, Session session) throws Exception {
		Application application = session.getApplication();
		
		out.write("function tr8nAddCSS(doc, value, inline) {\n");
		out.write("  var css = null; \n");
		out.write("  if (inline) {\n");
		out.write("     css=doc.createElement('style'); css.type='text/css';\n");
		out.write("     if (css.styleSheet) css.styleSheet.cssText = value;\n"); 
		out.write("     else css.appendChild(document.createTextNode(value));\n");
		out.write("  } else {\n");
		out.write("     css = doc.createElement('link'); css.setAttribute('type', 'text/css'); css.setAttribute('rel', 'stylesheet'); css.setAttribute('media', 'screen');\n");
		out.write("     if (value.indexOf('//') != -1) css.setAttribute('href', value);\n");
		out.write("     else css.setAttribute('href', '" + application.getHost() + "' + value);\n");
		out.write("  }\n");
		out.write("  doc.getElementsByTagName('head')[0].appendChild(css);\n");
		out.write("  return css;\n");
		out.write("}\n");
	}
	
	private void writeJSFunction(JspWriter out, Session session) throws Exception {
		Application application = session.getApplication();
		
		out.write("function tr8nAddJS(doc, id, src, onload) {\n");
		out.write("  var script = doc.createElement('script'); script.setAttribute('id', id); script.setAttribute('type', 'application/javascript');\n");
		out.write("  if (src.indexOf('//') != -1) script.setAttribute('src', src);\n");
		out.write("  else script.setAttribute('src', '" + application.getHost() + "' + src);\n");
		out.write("  script.setAttribute('charset', 'UTF-8');\n");
		out.write("  if (onload) script.onload = onload;\n");
		out.write("  doc.getElementsByTagName('head')[0].appendChild(script);\n");
		out.write("  return script;\n");
		out.write("}\n");	
	}
	
	private void writeScripts(JspWriter out, Session session) throws Exception {
		Application application = session.getApplication();
				
		out.write("(function() {\n");
		out.write("  if (window.addEventListener) window.addEventListener('load', tr8nInit, false);\n");
		out.write("  else if (window.attachEvent) window.attachEvent('onload', tr8nInit);\n");
		out.write("  window.setTimeout(function() {tr8nInit();}, 1000);\n");

		out.write("  function tr8nInit() {\n");
		out.write("  	if (window.tr8n_already_initialized) return;\n");
		out.write("  	window.tr8n_already_initialized = true;\n");

		out.write("  	tr8nAddCSS(window.document, '/assets/tr8n/tools.css', false);\n");
		out.write("  	tr8nAddCSS(window.document, \"" + application.getCss() + "\", true);\n");
		out.write("  	tr8nAddJS(window.document, 'tr8n-jssdk', '/assets/tools.js?t=" + (new Date()).getTime() + "', function() {\n");
		out.write("  		Tr8n.app_key = '" + application.getKey() + "';\n");
		out.write("  		Tr8n.host = '" + application.getHost() + "';\n");
		out.write("  		Tr8n.sources = [];\n");
		out.write("  		Tr8n.default_locale = '" + application.getDefaultLocale() + "';\n");
		out.write("  		Tr8n.page_locale = '" + session.getCurrentLanguage().getLocale() + "';\n");
		out.write("  		Tr8n.locale = '" + session.getCurrentLanguage().getLocale() + "';\n");
		
		if (application.isFeatureEnabled("shortcuts")) {
            Iterator<Entry<String, String>> entries = application.getShortcuts().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) entries.next();
        		out.write("  		shortcut.add('" + entry.getKey() + "', function() {\n");
        		out.write("  		  		" + entry.getValue() + "\n");
        		out.write("  		});\n");
            }
		}

		out.write("  		if (typeof(tr8n_on_ready) === 'function') tr8n_on_ready(); \n");
		out.write("  	});\n");
		out.write("  }\n");
		out.write("})();\n");	
	}
	
	
	public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

    	    Session tr8nSession = (Session) request.getAttribute("tr8n");
    	    if (tr8nSession == null)
    	    	return EVAL_PAGE;
    	    	
    	    out.write("<script>\n");
    	    writeCSSFunction(out, tr8nSession);
    	    writeJSFunction(out, tr8nSession);
    	    writeScripts(out, tr8nSession);
    	    out.write("</script>\n");    	    
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        } finally {
        }
        return EVAL_PAGE;
    }
	
}






