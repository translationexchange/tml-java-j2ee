package com.tr8n.j2ee.tags;

import java.util.Map;

public class TrlTag extends TrTag {

	private static final long serialVersionUID = 3767931504996116594L;

	protected Map<String, Object> getOptionsMap() {
		Map<String, Object> options = super.getOptionsMap();
		options.put("skip_decorations", true);
		return options; 
	}	
	
}
