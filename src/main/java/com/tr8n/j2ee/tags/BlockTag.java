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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.tr8n.core.Session;
import com.tr8n.core.Tr8n;
import com.tr8n.core.Utils;

public class BlockTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	private String options;
	
	private Map<String, Object> optionsMap;

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	private Map<String,Object> dynamicAttributes = new HashMap<String,Object>();  

	public void setDynamicAttribute(String uri,
            String localName,
            Object value)
     throws JspException {
		dynamicAttributes.put(localName, value);		
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> parseAttributeData(String data) {
		data = data.replaceAll(Pattern.quote("'"), "\"");
		return (Map<String, Object>) Utils.parseJSON(data);
	}
	
	private void parseAttribute(Map<String, Object> tokens, String key, Object value) {
    	List<String> elements = Arrays.asList(key.split(Pattern.quote(".")));
    	Integer lastIndex = elements.size()-1;
    	if (elements.size() > 1) {
    		for (int i=0; i<lastIndex; i++) {
    			String subKey = elements.get(i);
    			Map<String, Object> object = new HashMap<String, Object>();
    			tokens.put(subKey, object);
    			tokens = object;
    		}
    	}
    	tokens.put(elements.get(lastIndex), value);
	}
	
	private void parseAttributes() {
		if (getOptions() != null) {
			optionsMap = parseAttributeData(getOptions());
		} else {
			optionsMap = new HashMap<String, Object>(); 
		}

	    Iterator<Entry<String, Object>> entries = dynamicAttributes.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entries.next();
            String key = entry.getKey();
            String value = (String) entry.getValue();
            Object object = value;
            if (value.startsWith("{")) 
            	object = parseAttributeData(value);
            	
            if (key.startsWith("option.")) { 
            	key = key.replaceAll(Pattern.quote("option."), "");
            	parseAttribute(optionsMap, key, object);
            } else {
            	parseAttribute(optionsMap, key, object);
            }
        }
	}
	
	protected Map<String, Object> getOptionsMap() {
		if (optionsMap == null) 
			parseAttributes();
		
		return optionsMap; 
	}	
	
	private void reset() {
		options = null;
	    optionsMap = null;
	}

	public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
	    Session tr8nSession = (Session) request.getAttribute("tr8n");
	    if (tr8nSession != null) {
	    	tr8nSession.beginBlockWithOptions(getOptionsMap());
	    }
		return EVAL_BODY_BUFFERED;
    }

	public int doEndTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    	    Session tr8nSession = (Session) request.getAttribute("tr8n");
    	    if (tr8nSession != null) {
    	    	tr8nSession.endBlock();
    	    }

    	    if (getBodyContent() != null) {
    	    	JspWriter out = pageContext.getOut();
    	    	out.write(getBodyContent().getString());
    	    }
        } catch(Exception e) {   
        	Tr8n.getLogger().logException(e);
            throw new JspException(e.getMessage());
        } finally {
        	reset();
        }
        return EVAL_PAGE;		
	}
	
}
