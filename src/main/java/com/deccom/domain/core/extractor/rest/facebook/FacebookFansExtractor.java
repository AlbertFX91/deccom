package com.deccom.domain.core.extractor.rest.facebook;

import com.deccom.domain.core.CVStyleUtil;
import com.deccom.domain.core.extractor.ControlVariableExtractor;
import com.deccom.domain.core.extractor.rest.RESTExtractor;
import com.deccom.domain.core.fields.DeccomField;
import com.deccom.domain.core.fields.InputType;
import com.deccom.service.impl.util.RESTUtil;

public class FacebookFansExtractor extends RESTExtractor implements ControlVariableExtractor {

	@DeccomField(type = InputType.URL)
	private String facebookPageURL;

	public FacebookFansExtractor() {
		super();

		String url, jsonPath;

		url = "https://graph.facebook.com/";
		jsonPath = "$.fan_count";

		setUrl(url);
		setJsonPath(jsonPath);
		setStyle(CVStyleUtil.facebookFans);
	}

	public String getFacebookPageURL() {
		return facebookPageURL;
	}

	public void setFacebookPageURL(String facebookPageURL) {
		this.facebookPageURL = facebookPageURL;
	}

	@Override
	public Double getData() {
		String url, body, value;

		url = getUrl() + RESTUtil.getFacebookPageID(getUrl(), getFacebookPageURL()) + "?fields=fan_count";
		body = RESTUtil.getResponseFacebook(url);
		value = getByJSONPath(body, getJsonPath());

		return Double.parseDouble(value);
	}

	public static String getResponseFacebook(String url) {
		System.out.println("getResponseFacebook: " + RESTUtil.getResponseFacebook(url));
		return RESTUtil.getResponseFacebook(url);
	}

}
