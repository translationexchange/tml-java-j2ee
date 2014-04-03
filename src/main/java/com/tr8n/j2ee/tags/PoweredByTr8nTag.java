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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class PoweredByTr8nTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	private static final String SITE = "https://tr8nhub.com";

	public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.write("<div>");
    	    out.write("<a href='" + SITE + "' style='font-size:14px;color:#ccc'>");
    	    out.write("Powered by Tr8n");
    	    out.write("<br>");
    	    out.write("<img src='" + SITE + "/assets/tr8n/tr8n_logo.png' style='padding:10px;width:80px;'>");
    	    out.write("</a>");
    	    out.write("</div>");
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        }
        return EVAL_PAGE;
    }
}
