/*
 * Copyright (c) 2018 Translation Exchange, Inc. All rights reserved.
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
 */

package com.translationexchange.j2ee.tags;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.translationexchange.core.Session;
import com.translationexchange.core.Tml;
import com.translationexchange.core.Utils;

public class TrTag extends BodyTagSupport {
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
      return getBodyContent().getString().trim();
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

  public List<String> getExcludedAttributes() {
    return null;
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseAttributeData(String data) {
    data = data.replaceAll(Pattern.quote("'"), "\"");
    return (Map<String, Object>) Utils.parseJSON(data);
  }

  private void parseAttribute(Map<String, Object> tokens, String key, Object value) {
    List<String> elements = Arrays.asList(key.split(Pattern.quote(".")));
    Integer lastIndex = elements.size() - 1;
    if (elements.size() > 1) {
      for (int i = 0; i < lastIndex; i++) {
        String subKey = elements.get(i);
        Map<String, Object> object = new HashMap<String, Object>();
        tokens.put(subKey, object);
        tokens = object;
      }
    }
    tokens.put(elements.get(lastIndex), value);
  }

  protected boolean isExcludedAttribute(String key) {
    return (getExcludedAttributes() != null && getExcludedAttributes().contains(key));
  }

  private void parseAttributes() {
    if (getTokens() != null) {
      tokensMap = parseAttributeData(getTokens());
    } else {
      tokensMap = new HashMap<String, Object>();
    }

    if (getOptions() != null) {
      optionsMap = parseAttributeData(getOptions());
    } else {
      optionsMap = new HashMap<String, Object>();
    }

    Iterator<Entry<String, Object>> entries = getDynamicAttributes().entrySet().iterator();
    while (entries.hasNext()) {
      Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entries.next();
      String key = entry.getKey();
      String value = entry.getValue().toString();
      Object object = value;

      if (isExcludedAttribute(key))
        continue;

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


  protected void reset() {
    label = null;
    description = null;
    tokens = null;
    options = null;
    tokensMap = null;
    optionsMap = null;
  }

  protected String translate(Session tmlSession, String label) {
    Map<String, Object> options = getOptionsMap();
    options.put("session", tmlSession);
    Map<String, Object> tokens = getTokensMap();
    String description = getDescription();
    return tmlSession.translate(label, description, tokens, options);
  }

  protected void translateAttributes(Session session, List<String> attributes) throws JspException {
    for (String attr : attributes) {
      if (getDynamicAttributes().get(attr) == null)
        continue;

      String value = getDynamicAttributes().get(attr).toString();
      if (value != null) {
        setDynamicAttribute(null, attr, translate(session, value));
      }
    }
  }

  public int doStartTag() throws JspException {
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    try {
      JspWriter out = pageContext.getOut();

      Session tmlSession = getTmlSession();

      if (tmlSession != null) {
        out.write(translate(tmlSession, getLabel()));
      } else {
        out.write(getLabel());
      }

    } catch (Exception e) {
      Tml.getLogger().logException(e);
      throw new JspException(e.getMessage());
    } finally {
      reset();
    }
    return EVAL_PAGE;
  }

}
