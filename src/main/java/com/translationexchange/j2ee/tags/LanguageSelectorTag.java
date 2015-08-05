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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.translationexchange.core.Language;
import com.translationexchange.core.Session;

public class LanguageSelectorTag extends TagSupport implements DynamicAttributes {

	private static final long serialVersionUID = 1L;

	private String type;
	private String element;
	private String className;
	private String style;
	private boolean toggle = true;
	private String toggleLabel; 
	private String toggleLabelCancel; 
	private boolean poweredBy = true; 

	public boolean isToggle() {
		return toggle;
	}

	public void setToggle(boolean toggle) {
		this.toggle = toggle;
	}

	public String getToggleLabel() {
		if (toggleLabel == null) return "Help Us Translate";
		return toggleLabel;
	}

	public void setToggleLabel(String toggleLabel) {
		this.toggleLabel = toggleLabel;
	}

	public String getToggleLabelCancel() {
		if (toggleLabel == null) return "Disable translation mode";
		return toggleLabelCancel;
	}

	public void setToggleLabelCancel(String toggleLabelCancel) {
		this.toggleLabelCancel = toggleLabelCancel;
	}

	public boolean isPoweredBy() {
		return poweredBy;
	}

	public void setPoweredBy(boolean poweredBy) {
		this.poweredBy = poweredBy;
	}
	
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getClassName() {
		if (className == null) return "dropdown";
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getElement() {
		if (element == null) return "div";
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getType() {
		if (type == null) return "default";
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private void createDefaultSelectorHtml(Session session) throws Exception {
        out("<a href='#' onClick='Tml.UI.LanguageSelector.show()'>");
		out("<img src='" + session.getCurrentLanguage().getFlagUrl() + "' style='align:middle'>");
		out("&nbsp;");
		out(session.getCurrentLanguage().getEnglishName());
		out("</a>");
	}

	private void createPopupScriptTag() throws Exception {
		out("<script>");
		out("(function () {");
		out("'use strict';");
		out("function addEvent(evnt, elem, func) {");
		out("	if (elem.addEventListener) elem.addEventListener(evnt, func, false);");
		out("	else if (elem.attachEvent) elem.attachEvent('on' + evnt, func);");
		out("	else elem[evnt] = func;");
		out("}");
		out("function hasClass(elem, cls) {");
		out("	return elem.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));");
		out("}");
		out("function addClass(elem, cls) {");
		out("	if (!hasClass(elem, cls)) elem.className += ' ' + cls;");
		out("}");
		out("function removeClass(elem, cls) {");
		out("	if (hasClass(elem, cls)) {");
		out("		var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');");
		out("		elem.className = elem.className.replace(reg, ' ');");
		out("	}");
		out("}");
		out("function toggleClass(elem, cls) {");
		out("	if (!hasClass(elem, cls)) addClass(elem, cls);");
		out("	else removeClass(elem, cls);");
		out("}");
		out("var LanguageSelector = function (element) {");
		out("	this.element = element;");
		out("	this.element.setAttribute('tabindex', '0');");
		out("	addEvent('click', this.element, this.open.bind(this));");
		out("	addEvent('blur', this.element, this.close.bind(this));");
		out("};");
		out("LanguageSelector.VERSION = '0.1.0';");
		out("LanguageSelector.prototype = {");
		out("	adjustMenu: function (parent) {");
		out("		removeClass(parent, 'trex-dropup');");
		out("		removeClass(parent, 'trex-dropleft');");
		out("		var");
		out("			menu = parent.querySelectorAll('.trex-dropdown-menu')[0],");
		out("			bounds = menu.getBoundingClientRect(),");
		out("			vHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0),");
		out("			vWidth = Math.max(document.documentElement.clientWidth, window.innerWidth || 0),");
		out("			buffer = 10;");
	    out("		if (bounds.top + menu.offsetHeight + buffer > vHeight) addClass(parent, 'trex-dropup');");
	    out("		if (bounds.left + menu.offsetWidth + buffer > vWidth)  addClass(parent, 'trex-dropleft');");
	    out("	},");
	    out("	open: function (e) {");
	    out("		e = e || window.event;");
	    out("		e.stopPropagation();");
	    out("		e.preventDefault();");
	    out("		var target = e.currentTarget || e.srcElement;");
	    out("		if (hasClass(target.parentElement, 'trex-open')) {");
	    out("			return this.close(e);");
	    out("		}");
	    out("		addClass(target.parentElement, 'trex-open');");
	    out("		this.adjustMenu(target.parentElement);");
	    out("		return false;");
	    out("	},");
	    out("	close: function (e) {");
	    out("		e = e || window.event;");
	    out("		var target = e.currentTarget || e.srcElement;");
	    out("		setTimeout(function () {");
	    out("			removeClass(target.parentElement, 'trex-open');");
	    out("		}, 500);");
	    out("	}");
	    out("};");
	    out("	var selectorList = document.querySelectorAll('[data-toggle=tml-language-selector]');");
	    out("	for (var i = 0, el, l = selectorList.length; i < l; i++) {");
	    out("		el = selectorList[i];");
	    out("		el.languageSelector = new LanguageSelector(el);");
	    out("	}");
	    out("})();");
	    out("</script>");
	}
	
	private void createSelectorScriptTag() throws Exception {
	    out("<script>");
	    out("function tml_change_locale(locale) {");
	    out("  var query_parts = window.location.href.split('#');");
	    out("  var anchor = query_parts.length > 1 ? query_parts[1] : null;");
	    out("  query_parts = query_parts[0].split('?');");
	    out("  var query = query_parts.length > 1 ? query_parts[1] : null;");
	    out("  var params = {};");
	    out("  if (query) {");
	    out("    var vars = query.split('&');");
	    out("    for (var i = 0; i < vars.length; i++) {");
	    out("      var pair = vars[i].split('=');");
	    out("      params[pair[0]] = pair[1];");
	    out("    }");
	    out("  }");
	    out("  params['locale'] = locale;");
	    out("  query = [];");
	    out("  var keys = Object.keys(params);");
	    out("  for (i = 0; i < keys.length; i++) {");
	    out("    query.push(keys[i] + '=' + params[keys[i]]);");
	    out("  }");
	    out("  var destination = query_parts[0];");
	    out("  if (query.length > 0)");
	    out("    destination = destination + '?' + query.join('&');");
	    out("  if (anchor)");
	    out("    destination = destination + '#' + anchor;");
	    out("  window.location = destination;");
	    out("}");
	    out("</script>");
	}
	
	private void createPopupSelectorHtml(Session session) throws Exception {
		String locale = getStringAttribute("locale", session.getCurrentLanguage().getLocale());
        Language language = session.getApplication().getLanguage(locale);
		
	    out("<style>");
	    out(".trex-language-selector {position: relative;display: inline-block;vertical-align: middle;}");
	    out(".trex-language-toggle,");
	    out(".trex-language-toggle:hover,");
	    out(".trex-language-toggle:focus {cursor:pointer;text-decoration:none;outline:none;}");
	    out(".trex-dropup .trex-dropdown-menu {top: auto;bottom: 100%;margin-bottom: 1px;-webkit-transform: scale(0.8) translateY(10%);transform: scale(0.8) translateY(10%);}");
	    out(".trex-dropleft .trex-dropdown-menu {left: auto; right: 0;}");
	    out(".trex-dropdown-menu {");
	    out("   -webkit-transform: scale(0.8) translateY(10%);transform: scale(0.8) translateY(10%);transition: 0.13s cubic-bezier(0.3, 0, 0, 1.3);opacity: 0;pointer-events: none;");
	    out("   display: block;font-family:Arial, sans-serif;position: absolute;");
	    out("   top: 100%;left: 0;z-index: 1000;float: left;list-style: none;background-color: #FFF;height:0px;width:0px;padding:0;overflow:hidden;");
	    out("}");
	    out(".trex-language-selector[dir=rtl] .trex-dropdown-menu {left: auto; right: 0;}");
	    out(".trex-language-selector.trex-dropleft[dir=rtl] .trex-dropdown-menu {left:0; right:auto;}");
	    out(".trex-language-selector.trex-open .trex-dropdown-menu {");
	    out("   opacity: 1;height:auto;width:auto;overflow:hidden;min-width: 250px;margin: 2px 0 0;font-size: 13px;");
	    out("   background-clip: padding-box;border: 1px solid rgba(0, 0, 0, 0.15);box-shadow: 0 2px 0 rgba(0, 0, 0, 0.05);");
	    out("   border-radius: 4px;color: #6D7C88;text-align: left;padding: 5px 0;");
	    out("   display:block;pointer-events: auto;-webkit-transform: none;transform: none;");
	    out("}");
	    out(".trex-dropdown-menu > li {");
	    out("text-align:" + language.getAlignment("left") + ";");
	    out("}");
	    out(".trex-dropdown-menu > li > a {");
	    out("  display: block; text-decoration:none !important; padding: 3px 10px;margin:0 5px;clear: both;font-weight: normal;line-height: 1.42857143;color: #333;border-radius:3px;white-space: nowrap;cursor:pointer;");
	    out("}");
	    out(".trex-dropdown-menu > li > a .trex-flag {margin-right:3px;width:23px;}");
	    out(".trex-dropdown-menu > li.trex-language-item > a:hover,");
	    out(".trex-dropdown-menu > li.trex-language-item > a:focus {text-decoration:none !important;background: #F0F2F4;}");
	    out(".trex-dropdown-menu > li.trex-language-item > a .trex-native-name {font-size: 11px;color: #A9AFB8;margin-left: 3px;}");
	    out(".trex-dropdown-menu > li.trex-selected a:after {content: '✓';right: 5px;font-weight: bold;font-size: 16px;margin: 0px 5px 0px 0px;color: #13CF80;position: absolute;}");
	    out(".trex-dropdown-menu[dir=rtl] > li.trex-selected a:after {left: 5px; right:auto !important; margin: 0px 0 0px 5px;}");
	    out(".trex-dropdown-menu .trex-credit a {border-top: solid 1px #DDD;font-size: 13px;padding: 7px 0 0;margin: 5px 15px 5px;color: #9FA7AE;font-weight: 400;}");
	    out("</style>");

	    out("<" + element + " class='trex-language-selector' dir='" + language.getDirection() + "'>");
	    out("<a class='trex-language-toggle' data-toggle='tml-language-selector' tabindex='0' dir='" + language.getDirection() + "'>");
	    out(LanguageNameTag.getLanguageNameHtml(language, session, getDynamicAttributes()));
	    out("</a>");

	    out("<ul class='trex-dropdown-menu' dir='" + language.getDirection() + "'>");

	    for (Language lang : session.getApplication().getLanguages()) {
	      out("<li class='trex-language-item " + (language.getLocale().equals(lang.getLocale()) ? "trex-selected" : "") + "' dir='" + language.getDirection() + "'>");
          out("<a href='javascript:void(0);' onclick='tml_change_locale(\"" + lang.getLocale() + "\")'>");
          out(LanguageNameTag.getLanguageNameHtml(lang,session, getDynamicAttributes()));
	      out("</a></li>");
	    }

	    if (isToggle()) {
	      out("<li class='trex-credit' dir='" + language.getDirection() + "'>");
	      out("<a href='javascript:void(0);' onclick='Tml.Utils.toggleInlineTranslations()'>");
	      if (session.isInlineModeEnabled()) {
	        out(getToggleLabelCancel());
	      } else {
	        out(getToggleLabel());
	      }
	      out("</a>");
	      out("</li>");
	    }

	    if (isPoweredBy()) {
	      out("<li class='trex-credit' dir='" + language.getDirection() + "'>");
	      out("<a href='http://translationexchange.com'>");
	      out("Powered by Translation Exchange");
	      out("</a>");
	      out("</li>");
	    }

	    out("</ul>");
	    out("</" + element + ">");
	    createSelectorScriptTag();
	    createPopupScriptTag();
	}

	private String generateHtmlAttribute(String name, String value) {
		if (value == null) return "";
		return name + "='" + value + "' ";
	}
		
	private void createBootstrapSelectorHtml(Session session) throws Exception {
		String locale = getStringAttribute("locale", session.getCurrentLanguage().getLocale());
        Language language = session.getApplication().getLanguage(locale);
		
		String className = getClassName();
		
	    createSelectorScriptTag();

	    if (!getElement().equals("none")) {
	      out("<" + element + " ");
	      out(generateHtmlAttribute("class", className));
	      out(generateHtmlAttribute("style", getStyle()));
	      out(">");
	    }

	    out("<a href='#' role='button' class='" + className + "-toggle' data-toggle='" + className + "'>");
	    out(LanguageNameTag.getLanguageNameHtml(language, session, getDynamicAttributes()));
	    out("</a>");

	    out("<ul class='" + className + "-menu' role='menu'>");

	    for (Language lang : session.getApplication().getLanguages()) {
	      out("<li role='presentation'>");

	      if (language.getLocale().equals(lang.getLocale())) {
	        out("<div style='right: 5px;font-weight: bold;font-size: 16px;margin: 0px 5px 0px 0px;color: #13CF80;position: absolute;'>✓</div>");
	      }

          out("<a href='javascript:void(0);' onclick='tml_change_locale(\"" + lang.getLocale() + "\")'>");
  	      out(LanguageNameTag.getLanguageNameHtml(lang, session, getDynamicAttributes()));
	      out("</a></li>");
	    }

	    if (isToggle()) {
	      out("<li role='presentation' class='divider'></li>");
	      out("<li role='presentation'><a href='javascript:void(0);' onclick='Tml.Utils.toggleInlineTranslations()'>");
	      if (session.isInlineModeEnabled()) {
	        out(getToggleLabelCancel());
	      } else {
	        out(getToggleLabel());
	      }
	      out("</a>");
	      out("</li>");
	    }

	    if (isPoweredBy()) {
	      out("<li role='presentation' class='divider'></li>");
	      out("<div style='padding: 0px 20px; font-size:11px; white-space: nowrap;'>");
	      out("<a href='http://translationexchange.com' style='color:#888;'>");
	      out("Powered By Translation Exchange");
	      out("</a>");
	      out("</div>");
	      out("</ul>");
	    }

	    if (!getElement().equals("none")) {
	      out("</" + element + ">");
	    }
	}	
	
	public int doStartTag() throws JspException {
        try {
            Session session = getTmlSession();
    	    if (session == null)
    	    	return EVAL_PAGE;
            
    	    if (getType().equals("popup")) {
    	    	createPopupSelectorHtml(session);
    	    } else if (getType().equals("bootstrap")) {
    	    	createBootstrapSelectorHtml(session);
    	    } else {
    	    	createDefaultSelectorHtml(session);
    	    }
        } catch(Exception e) {   
            throw new JspException(e.getMessage());
        }
        return EVAL_PAGE;
    }

}
