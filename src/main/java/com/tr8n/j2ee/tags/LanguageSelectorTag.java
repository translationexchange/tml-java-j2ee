package com.tr8n.j2ee.tags;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.tr8n.core.Session;

public class LanguageSelectorTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
    	    Session tr8nSession = (Session) request.getAttribute("tr8n");
    	    if (tr8nSession == null)
    	    	return EVAL_PAGE;
            
            out.write("<a href='#' onClick='Tr8n.UI.LanguageSelector.show()'>");
            out.write("<img src='" + tr8nSession.getCurrentLanguage().getFlagUrl() + "' style='align:middle'>");
            out.write("&nbsp;");
            out.write(tr8nSession.getCurrentLanguage().getEnglishName());
            out.write("</a>");
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        }
        return EVAL_PAGE;
    }
}
