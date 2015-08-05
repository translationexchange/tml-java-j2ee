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

package com.translationexchange.j2ee.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.j2ee.utils.SecurityUtils;

public abstract class LocalizedServlet extends HttpServlet {
	public static final String TML_SESSION_KEY = "tml";
	public static final String COOKIE_PREFIX = "trex_";
	public static final String COOKIE_SUFFIX = "_translationexchange";

	private static final long serialVersionUID = 8628674922010886330L;
	private static final int DO_GET 	= 1;
	private static final int DO_POST 	= 2;
	private static final int DO_PUT 	= 3;
	private static final int DO_DELETE 	= 4;

  	private String getCookieName() {
  		String key = (String) Tml.getConfig().getApplication().get("key");
  		return COOKIE_PREFIX + key; 
  	}
  	
  	private String getSessionCookie(String key, HttpServletRequest request) throws UnsupportedEncodingException {
	    for (Cookie c : request.getCookies()) {
	    	if (c.getName().equals(key))
	    		return URLDecoder.decode(c.getValue(), "UTF-8");
	    }
	    return null;
  	}

  	private void setSessionCookie(String key, String value, HttpServletResponse response) throws UnsupportedEncodingException {
		 Cookie cookie = new Cookie(key, URLEncoder.encode(value, "UTF-8"));
	     response.addCookie(cookie);
    }
  	
    protected Session getTml(HttpServletRequest req) {
		return (Session) req.getAttribute(TML_SESSION_KEY);
	}
  	
    protected void doLocalizedGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTml(req).translate("HTTP method GET is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doLocalizedPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTml(req).translate("HTTP method POST is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doLocalizedDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTml(req).translate("HTTP method DELETE is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }    
    
    protected void doLocalizedPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTml(req).translate("HTTP method PUT is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }
    
    /**
     * Can be extended by the sub-class
     * @return
     */
    protected String getCurrentLocale() {
    	return null;
    }

    /**
     * Can be extended by the sub-class
     * @return
     */
    protected String getCurrentSource() {
    	return null;
    }
    
    protected void execute(int type, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    Session tmlSession = null;
	    Long t0 = (new Date()).getTime();
	    
	    try {
	    	Map<String, Object> params = SecurityUtils.decode(getSessionCookie(getCookieName(), req));
	    	if (params != null) Tml.getLogger().debug(params);

	    	String locale = getCurrentLocale();
		    if (locale == null) {
		    	locale = req.getParameter("locale");
		    	if (locale != null) {
		    		params.put("locale", locale);
		    		setSessionCookie(getCookieName(), SecurityUtils.encode(params), resp);
		    	} else if (params.get("locale") != null) {
		    		locale = (String) params.get("locale");
		    	} else {
		    		locale = req.getLocale().getLanguage();
		    	}
		    }
		    params.put("locale", locale);

		    Tml.getLogger().debug("Selected locale: " + locale);
		    
	    	String source = getCurrentSource();
		    if (source == null) {
		    	URL url = new URL(req.getRequestURL().toString());
		    	source = url.getPath();
		    }
		    params.put("source", source);
		    
		    tmlSession = new Session(params);
		    req.setAttribute(TML_SESSION_KEY, tmlSession);
		    
		    switch (type) {
				case DO_GET:
					doLocalizedGet(req, resp);
					break;
				case DO_POST:
					doLocalizedPost(req, resp);
					break;
				case DO_PUT:
					doLocalizedPut(req, resp);
					break;
				case DO_DELETE:
					doLocalizedDelete(req, resp);
					break;
				default:
					break;
			}
	    	
	    } finally {
		    if (tmlSession != null) 
		    	tmlSession.getApplication().submitMissingTranslationKeys();
		    
		    Long t1 = (new Date()).getTime();
		    
		    // Ensure that we always fetch the version from Cache 
		    Tml.getCache().setVersion(null);
		    
		    Tml.getLogger().debug("Request took: " + (t1-t0) + " mls");
	    }
    }
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute(DO_GET, req, resp);
	}	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute(DO_POST, req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute(DO_DELETE, req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		execute(DO_PUT, req, resp);
	}
	
}
