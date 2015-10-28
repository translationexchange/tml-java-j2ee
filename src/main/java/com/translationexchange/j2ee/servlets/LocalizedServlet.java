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
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;
import com.translationexchange.j2ee.utils.SecurityUtils;
import com.translationexchange.j2ee.utils.SessionUtils;

public abstract class LocalizedServlet extends HttpServlet {
	public static final String TML_SESSION_KEY = "tml";

	private static final long serialVersionUID = 8628674922010886330L;
	private static final int DO_GET 	= 1;
	private static final int DO_POST 	= 2;
	private static final int DO_PUT 	= 3;
	private static final int DO_DELETE 	= 4;

    protected Session getTmlSession(HttpServletRequest req) {
		return (Session) req.getAttribute(TML_SESSION_KEY);
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
    
    protected void doLocalizedGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTmlSession(req).translate("HTTP method GET is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doLocalizedPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTmlSession(req).translate("HTTP method POST is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doLocalizedDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTmlSession(req).translate("HTTP method DELETE is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }    
    
    protected void doLocalizedPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTmlSession(req).translate("HTTP method PUT is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    /**
     * Prepares session parameters
     * 
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
	protected Map<String, Object> prepareSessionParams(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	Map<String, Object> params = SecurityUtils.decode(SessionUtils.getSessionCookie(SessionUtils.getCookieName(), req));
    	if (params != null) Tml.getLogger().debug(params);
    	
    	// Locale can be forced by the user
    	String locale = getCurrentLocale();
	    if (locale == null) {
	    	// Or passed as a parameter
	    	locale = req.getParameter("locale");
	    	if (locale != null) {
	    		params.put("locale", locale);
	    		SessionUtils.setSessionCookie(SessionUtils.getCookieName(), SecurityUtils.encode(params), resp);
			    Tml.getLogger().debug("Param Locale: " + locale);
	    	} else if (params.get("locale") != null) {
	    		// Or loaded from the cookie 
	    		locale = (String) params.get("locale");
			    Tml.getLogger().debug("Cookie Locale: " + locale);
	    	} else {
	    		// Or taken from the Accepted Locale header 
	    		List<String> locales = new ArrayList<String>();
	    		Enumeration<Locale> e = req.getLocales();
	    		while (e.hasMoreElements()) {
	    			locales.add(e.nextElement().getLanguage());
	    		}
	    		locale = Utils.join(locales, ",");
	    		Tml.getLogger().debug("Header Locale: " + locale);
	    	}
	    } else {
		    Tml.getLogger().debug("User Locale: " + locale);
	    }
	    params.put("locale", locale);
	    
    	String source = getCurrentSource();
	    if (source == null) {
	    	URL url = new URL(req.getRequestURL().toString());
	    	source = url.getPath();
		    Tml.getLogger().debug("Url Source: " + source);
	    } else {
		    Tml.getLogger().debug("User Source: " + source);
	    }
	    params.put("source", source);
	    	
	    return params;
    }
    
    protected void execute(int type, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	Tml.getLogger().debug("Requesting " + req.getRequestURL().toString());
    	
    	Session tmlSession = null;
	    Long t0 = (new Date()).getTime();
	    
	    try {
	    	
		    tmlSession = new Session(prepareSessionParams(req, resp));
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
