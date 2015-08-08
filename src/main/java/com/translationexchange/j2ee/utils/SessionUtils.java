package com.translationexchange.j2ee.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.translationexchange.core.Tml;

public class SessionUtils {
	public static final String COOKIE_PREFIX = "trex_";
	public static final String COOKIE_SUFFIX = "_translationexchange";

	public static String getCookieName() {
  		String key = (String) Tml.getConfig().getApplication().get("key");
  		return COOKIE_PREFIX + key; 
  	}
  	
	public static String getSessionCookie(String key, HttpServletRequest request) throws UnsupportedEncodingException {
		if (request == null || request.getCookies() == null) 
			return null;
					
	    for (Cookie c : request.getCookies()) {
	    	if (c.getName().equals(key))
	    		return URLDecoder.decode(c.getValue(), "UTF-8");
	    }
	    return null;
  	}

	public static void setSessionCookie(String key, String value, HttpServletResponse response) throws UnsupportedEncodingException {
		 Cookie cookie = new Cookie(key, URLEncoder.encode(value, "UTF-8"));
	     response.addCookie(cookie);
    }
}
