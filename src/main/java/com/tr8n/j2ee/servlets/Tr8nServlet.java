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

package com.tr8n.j2ee.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tr8n.core.Session;
import com.tr8n.core.Tr8n;
import com.tr8n.j2ee.utils.SecurityUtils;

public abstract class Tr8nServlet extends HttpServlet {

	private static final long serialVersionUID = 8628674922010886330L;
	private static final int DO_GET 	= 1;
	private static final int DO_POST 	= 2;
	private static final int DO_PUT 	= 3;
	private static final int DO_DELETE 	= 4;

  	private String getSessionCookie(String key, HttpServletRequest request) throws UnsupportedEncodingException {
	    for (Cookie c : request.getCookies()) {
	    	if (c.getName().equals("tr8n_" + key))
	    		return URLDecoder.decode(c.getValue(), "UTF-8");
	    }
	    return null;
  	}

    protected Session getTr8n(HttpServletRequest req) {
		return (Session) req.getAttribute("tr8n");
	}
  	
    protected void doTr8nGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTr8n(req).translate("HTTP method GET is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doTr8nPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTr8n(req).translate("HTTP method POST is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }

    protected void doTr8nDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTr8n(req).translate("HTTP method DELETE is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }    
    
    protected void doTr8nPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        String protocol = req.getProtocol();
        String msg = getTr8n(req).translate("HTTP method PUT is not supported by this URL"); 
        if (protocol.endsWith("1.1")) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, msg);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, msg);
        }
    }    
    
    protected void execute(int type, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    Session tr8nSession = null;
	    Long t0 = (new Date()).getTime();
	    
	    try {
	    	tr8nSession = new Session();
	    	req.setAttribute("tr8n", tr8nSession);
		    tr8nSession.init(SecurityUtils.decodeAndVerify(getSessionCookie(tr8nSession.getApplication().getKey(), req), tr8nSession.getApplication().getSecret()));
		    tr8nSession.setCurrentSource(req.getRequestURI().toString());
		    
		    switch (type) {
				case DO_GET:
					doTr8nGet(req, resp);
					break;
				case DO_POST:
					doTr8nPost(req, resp);
					break;
				case DO_PUT:
					doTr8nPut(req, resp);
					break;
				case DO_DELETE:
					doTr8nDelete(req, resp);
					break;
				default:
					break;
			}
	    	
	    } finally {
		    if (tr8nSession != null) 
		    	tr8nSession.getApplication().submitMissingTranslationKeys();
		    
		    Long t1 = (new Date()).getTime();
		    
		    Tr8n.getLogger().debug("Request took: " + (t1-t0) + " mls");
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
