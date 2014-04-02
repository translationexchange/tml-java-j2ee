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

public class TrTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	private String label;
	
	private String description;

	private String tokens;

	private String options;
	
	private Map<String, Object> tokensMap;
	
	private Map<String, Object> optionsMap;
	
	public String getLabel() {
		if (label != null) 
			return label;
		if (getBodyContent() != null)
			return getBodyContent().getString();
		return null;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTokens() {
		return tokens;
	}

	public void setTokens(String tokens) {
		this.tokens = tokens;
	}

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
		if (getTokens() != null) {
			tokensMap = parseAttributeData(getTokens());
		} else {
			tokensMap = new HashMap<String, Object>();
		}
		
		if (getOptions() != null) {
			optionsMap = parseAttributeData(getTokens());
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
            } else if (key.startsWith("token.")) {
            	key = key.replaceAll(Pattern.quote("token."), "");
            	parseAttribute(tokensMap, key, object);
            } else {
            	parseAttribute(tokensMap, key, object);
            }
        }
	}
	
	protected Map<String, Object> getTokensMap() {
		if (tokensMap == null) 
			parseAttributes();
		
		return tokensMap; 
	}
	
	protected Map<String, Object> getOptionsMap() {
		if (optionsMap == null) 
			parseAttributes();
		
		return optionsMap; 
	}	
	
	
	private void reset() {
		label = null;
		description = null;
		tokens = null;
		options = null;
	    tokensMap = null;
	    optionsMap = null;
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
    }

	public int doEndTag() throws JspException {
        try {
//        	PageContext pageContext = (PageContext) getJspContext();  
//          HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
            JspWriter out = pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

    	    Session tr8nSession = (Session) request.getAttribute("tr8n");
    	    if (tr8nSession != null) {
    	    	Map<String, Object> options = getOptionsMap();
    	    	options.put("session", tr8nSession);
    	    	out.write(tr8nSession.translate(getLabel(), getDescription(), getTokensMap(), options));
    	    } else {
            	out.write(getLabel());
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
