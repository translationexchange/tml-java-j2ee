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
 */

package com.translationexchange.j2ee.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.translationexchange.core.Language;
import com.translationexchange.core.Session;
import com.translationexchange.j2ee.servlets.LocalizedServlet;

public class TagSupport extends javax.servlet.jsp.tagext.TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = -2954461560095601814L;

	private Map<String,Object> dynamicAttributes = new HashMap<String,Object>();  

	protected Session getTmlSession() {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
	    return (Session) request.getAttribute(LocalizedServlet.TML_SESSION_KEY);
	}
	
	protected void out(JspWriter writer, String str) throws Exception {
		writer.write(str + "\n");	
	}

	protected void out(String str) throws Exception {
		out(pageContext.getOut(), str);
	}

	protected List<String> getSupportedAttributeNames() {
		return new ArrayList<String>();
	}

	public Map<String,Object> getDynamicAttributes() {
		return dynamicAttributes;
	}
	
	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		dynamicAttributes.put(localName, localName);		
	}
	
	public Object getDynamicAttribute(String name) {
		return dynamicAttributes.get(name);
	}

	public String getStringAttribute(String name) {
		return getStringAttribute(name, null);
	}

	public String getStringAttribute(String name, String defaultValue) {
		return getStringAttribute(getDynamicAttributes(), name, defaultValue);
	}

	public static String getStringAttribute(Map<String, Object> options, String name, String defaultValue) {
		String value = (String) options.get(name);
		if (value == null) return defaultValue;
		return value;
	}
	
	public int getIntAttribute(String name) {
		return getIntAttribute(name, 0);
	}
	
	public int getIntAttribute(String name, int defaultValue) {
		return getIntAttribute(getDynamicAttributes(), name, defaultValue);
	}
	
	public static int getIntAttribute(Map<String, Object> options, String name, int defaultValue) {
		String value = (String) options.get(name);
		if (value == null) return defaultValue;
		return Integer.parseInt(value);
	}
	
	public boolean getBooleanAttribute(String name) {
		return getBooleanAttribute(getDynamicAttributes(), name, false);
	}

	public boolean getBooleanAttribute(String name, boolean defaultValue) {
		return getBooleanAttribute(getDynamicAttributes(), name, defaultValue);
	}
	
	public static boolean getBooleanAttribute(Map<String, Object> options, String name, boolean defaultValue) {
		String value = (String) options.get(name);
		if (value == null) return defaultValue;
		return Boolean.parseBoolean(value);
	}
	
	public static Object getObjectAttribute(Map<String, Object> options, String name, Object defaultValue) {
		Object value = options.get(name);
		if (value == null) return defaultValue;
		return value;
	}	
	
	protected Language getLanguageAttribute(Session session) {
		String locale = getStringAttribute(getDynamicAttributes(), "locale", session.getCurrentLanguage().getLocale());
		return session.getApplication().getLanguage(locale);
	}
}
