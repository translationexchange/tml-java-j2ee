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
 * 
 * @author Michael Berkovich
 * 
 * Usage:
 * 
 * <tml:scripts/> 
 * 
 */
package com.translationexchange.j2ee.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;

import com.translationexchange.core.Application;
import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.core.languages.Language;

public class ScriptsTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	
	private void writeCSS(Session session) throws Exception {
		Application application = session.getApplication();
		out("<style type='text/css'>");
		out(application.getCss());
		out("</style>");
	}
	
	private void writeJS(Session session) throws Exception {
		Application application = session.getApplication();
		Language language = session.getCurrentLanguage();
		String source = session.getCurrentSource();
		
//		 if opts[:js]
//			        js_opts = opts[:js].is_a?(Hash) ? opts[:js] : {}
//			        js_host = js_opts[:host] || 'https://tools.translationexchange.com/tml/stable/tml.min.js'
//			        html = []
//			      out("<script src='#{js_host}'></script>"
//			      out('<script>'
//			      out('tml.init({'
//			      out("    key:    '#{tml_application.key}', "
//			      out("    token:  '#{tml_application.token}', "
//			      out("    debug: #{js_opts[:debug] || false},"
//			        if js_opts[:onload]
//			        out('    onLoad: function() {'
//			        out("       #{js_opts[:onload]}"
//			        out('    }'
//			        end
//			      out('});'
//			      out('</script>'
//			        return html.join.html_safe
//			      end

//		Tml.getConfig().get

//	      agent_host = agent_config[:host] || 'https://tools.translationexchange.com/agent/stable/agent.min.js'
//	      if agent_config[:cache]
//	        t = Time.now
//	        t = t - (t.to_i % agent_config[:cache].to_i).seconds
//	        agent_host += "?ts=#{t.to_i}"
//	      end
		
        List<Map <String, Object>> languages = new ArrayList<Map <String, Object>>();

		Map<String, Object> config = Utils.buildMap(
			"locale", 	language.getLocale(),
			"source", 	source,
			"css", 		application.getCss(),
			"sdk", 		"tml-java v" + Tml.VERSION,
			"languages", languages
		);
		
        for (Language lang : application.getLanguages()) {
            Map <String, Object> info = new HashMap <String, Object>();
            info.put("locale", lang.getLocale());
            info.put("english_name", lang.getEnglishName());
            info.put("native_name", lang.getNativeName());
            info.put("flag_url", lang.getFlagUrl());
            languages.add(info);
        }

        out("<script>");
		out("(function() {");
		out("var script = window.document.createElement('script');");
		out("script.setAttribute('id', 'tml-agent');");
		out("script.setAttribute('type', 'application/javascript');");
		out("script.setAttribute('src', '" + Tml.getConfig().getAgent().get("host") + "');");
		out("script.setAttribute('charset', 'UTF-8');");
		out("script.onload = function() {");
		out("   Trex.init('" + application.getKey() + "', " + Utils.buildJSON(config) + ");");
		out("};");
		out("window.document.getElementsByTagName('head')[0].appendChild(script);");
		out("})();");
		out("</script>");
	}
	
	public int doStartTag() throws JspException {
        try {
    	    Session session = getTmlSession();
    	    
    	    if (session == null)
    	    	return EVAL_PAGE;
    	    
    	    writeJS(session);
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        } finally {
        }
        return EVAL_PAGE;
    }
	
}






