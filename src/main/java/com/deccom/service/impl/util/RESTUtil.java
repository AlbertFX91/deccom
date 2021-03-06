package com.deccom.service.impl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.deccom.config.ApplicationProperties;
import com.deccom.service.impl.RESTServiceImpl;
import com.jayway.jsonpath.JsonPath;

@EnableConfigurationProperties({ ApplicationProperties.class })
@Component
public class RESTUtil {

	private static final Logger log = LoggerFactory.getLogger(RESTServiceImpl.class);
	private static final String i18nCodeRoot = "operations.REST";

	// Client
	public static final String USER_AGENT = "Chrome/60.0.3112.101";

	private static ApplicationProperties applicationProperties;

	@Autowired
	public RESTUtil(ApplicationProperties applicationProperties) {
		RESTUtil.applicationProperties = applicationProperties;
	}

	/**
	 * Sends a HTTP GET request to an URL.
	 * 
	 * @param url
	 *            the url to send the request to
	 * @return the requested JSON as a String
	 */
	public static String getResponse(String url) {

		URL obj;
		HttpURLConnection con;
		String result;

		try {
			// The path is transformed into an URL object to establish the
			// connection
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			// Optional default is GET
			con.setRequestMethod("GET");

			// Adding request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			result = getResponse(con);

			return result;

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Wrong URL format", i18nCodeRoot + ".urlerror", "RESTService", e);

		} catch (java.net.UnknownHostException | java.io.FileNotFoundException e) {
			throw new RESTServiceException("Unreachable URL", i18nCodeRoot + ".unreachableurl", "RESTService", e);

		} catch (IOException e) {
			throw new RESTServiceException("Data cannot be read", i18nCodeRoot + ".unreadabledata", "RESTService", e);
		}

	}

	public static String getResponseFacebook(String url) {

		URL obj;
		HttpURLConnection con;
		String result;

		try {
			// The path is transformed into an URL object to establish the
			// connection
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			// Optional default is GET
			con.setRequestMethod("GET");

			// Adding request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			// Facebook authentication settings
			setFacebookAuthentication(con);

			result = getResponse(con);

			return result;

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Wrong URL format", i18nCodeRoot + ".urlerror", "RESTService", e);

		} catch (java.net.UnknownHostException | java.io.FileNotFoundException e) {
			throw new RESTServiceException("Unreachable URL", i18nCodeRoot + ".unreachableurl", "RESTService", e);

		} catch (IOException e) {
			throw new RESTServiceException("Data cannot be read", i18nCodeRoot + ".unreadabledata", "RESTService", e);
		}

	}
	
	public static String getFacebookPageID(String url, String facebookPageURL) {
		String urlRequest, body, key, value;

		urlRequest = url + "?ids=" + facebookPageURL;
		body = RESTUtil.getResponseFacebook(urlRequest);
		key = "\"id\":";
		value = body.substring(body.indexOf(key) + key.length() + 1, body.lastIndexOf("\""));

		return value;
	}

	public static String getResponseTwitter(String url) {

		URL obj;
		HttpURLConnection con;
		String result;

		try {
			// The path is transformed into an URL object to establish the
			// connection
			obj = new URL(url);
			con = (HttpURLConnection) obj.openConnection();

			// Optional default is GET
			con.setRequestMethod("GET");

			// Adding request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			// Twitter authentication settings
			setTwitterAuthentication(con);

			result = getResponse(con);

			return result;

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Wrong URL format", i18nCodeRoot + ".urlerror", "RESTService", e);

		} catch (java.net.UnknownHostException | java.io.FileNotFoundException e) {
			throw new RESTServiceException("Unreachable URL", i18nCodeRoot + ".unreachableurl", "RESTService", e);

		} catch (IOException e) {
			throw new RESTServiceException("Data cannot be read", i18nCodeRoot + ".unreadabledata", "RESTService", e);
		}

	}

	/**
	 * Sends a HTTP GET request to an URL.
	 * 
	 * @param url
	 *            the url to send the request to
	 * @return the requested JSON as a String
	 */
	public static String getResponse(HttpURLConnection con) {

		int responseCode;
		BufferedReader in;
		String inputLine, result, responseMessage;
		StringBuffer response;

		try {

			// Here we will know if the response is positive or not
			responseCode = con.getResponseCode();
			responseMessage = con.getResponseMessage();

			// log.debug("\nSending 'GET' request to URL : " + url);
			log.debug("Response Code : " + responseCode);
			log.debug("Response Message : " + responseMessage);

			// Now, it is time to read the data as a string using BufferedReader
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			response = new StringBuffer();

			// This string will contain the JSON sent as response line by line
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Here is the full response. Now we have to deal with it
			result = response.toString();

			return result;

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Wrong URL format", i18nCodeRoot + ".urlerror", "RESTService", e);

		} catch (java.net.UnknownHostException | java.io.FileNotFoundException e) {
			throw new RESTServiceException("Unreachable URL", i18nCodeRoot + ".unreachableurl", "RESTService", e);

		} catch (IOException e) {
			throw new RESTServiceException("Data cannot be read", i18nCodeRoot + ".unreadabledata", "RESTService", e);
		}

	}

	/**
	 * Tells if the response contains an array with many JSON objects or just one
	 * JSON object.
	 * 
	 * @param response
	 *            the response to check
	 * @return the answer to whether the request it is an array or not
	 */
	public static Boolean checkResponse(String response) {

		char character;

		character = response.charAt(0);

		return character == '[';

	}

	/**
	 * Sends a HTTP GET request to an URL and captures the data using a JSONPath.
	 * 
	 * @param url
	 *            the URL to send the request to
	 * @param jsonPath
	 *            the JSONPath query to capture the data
	 * @return the data captured
	 */
	public static String getByJSONPath(String value, String jsonPath) {

		return JsonPath.parse(value).read(jsonPath).toString();

	}

	/**
	 * Adds the requested Twitter authentication configuration to a request.
	 * 
	 * @param con
	 *            the connection to be configured
	 */
	private static void setTwitterAuthentication(HttpURLConnection con) throws IOException {
		String bearerToken;

		bearerToken = requestTwitterBearerToken("https://api.twitter.com/oauth2/token");

		con.setRequestProperty("Host", "api.twitter.com");
		con.setRequestProperty("Authorization", "Bearer " + bearerToken);
	}

	/**
	 * Constructs the request for requesting a bearer token for Twitter.
	 * 
	 * @param endPointUrl
	 *            the URL to request the token
	 * @return the bearer token as a string
	 */
	@SuppressWarnings("unused")
	private static String requestTwitterBearerToken(String endPointUrl) throws IOException {
		HttpsURLConnection connection = null;
		String encodedCredentials = encodeKeys(applicationProperties.getTwitterConsumerKey(),
				applicationProperties.getTwitterConsumerSecret());

		try {
			URL url = new URL(endPointUrl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Host", "api.twitter.com");
			connection.setRequestProperty("Authorization", "Basic " + encodedCredentials);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Content-Length", "29");
			connection.setUseCaches(false);

			writeRequest(connection, "grant_type=client_credentials");

			// Parse the JSON response into a JSON mapped object to fetch fields
			// from.
			/*
			 * JSONObject obj = (JSONObject) JSONValue .parse(readResponse(connection));
			 */

			try {
				JSONObject obj = new JSONObject(readResponse(connection));

				if (obj != null) {
					try {
						String tokenType = (String) obj.get("token_type");
						String token = (String) obj.get("access_token");

						return ((tokenType.equals("bearer")) && (token != null)) ? token : "";
					} catch (JSONException e) {
						throw new RESTServiceException("Wrong credentials", i18nCodeRoot + ".wrongcredentials",
								"RESTService", e);
					}

				}
			} catch (JSONException e) {
				throw new RESTServiceException("Wrong credentials", i18nCodeRoot + ".wrongcredentials", "RESTService",
						e);
			}

			return new String();

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Invalid endpoint URL specified",
					i18nCodeRoot + ".invalidendpointURLspecified", "RESTService", e);

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * Encodes the consumer key and secret.
	 * 
	 * @param consumerKey
	 *            the user's API Key
	 * @param consumerSecret
	 *            the user's API Secret
	 * @return the basic authorization key
	 */
	private static String encodeKeys(String consumerKey, String consumerSecret) {
		try {
			String encodedConsumerKey = URLEncoder.encode(consumerKey, "UTF-8");
			String encodedConsumerSecret = URLEncoder.encode(consumerSecret, "UTF-8");
			String fullKey = encodedConsumerKey + ":" + encodedConsumerSecret;
			byte[] encodedBytes = Base64.encodeBase64(fullKey.getBytes());
			return new String(encodedBytes);
		} catch (UnsupportedEncodingException e) {
			return new String();
		}

	}

	/**
	 * Writes a request to a connection.
	 * 
	 * @param connection
	 *            the connection to write the request to
	 * @param textBody
	 *            the body of the request
	 * @return tells if the request was properly sent or not
	 */
	// Writes a request to a connection
	private static boolean writeRequest(HttpsURLConnection connection, String textBody) {
		try {
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			wr.write(textBody);
			wr.flush();
			wr.close();

			return true;

		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Reads a response for a given connection.
	 * 
	 * @param connection
	 *            the connection that the response was for
	 * @return the response as a string
	 */
	// Reads a response for a given connection and returns it as a string.
	private static String readResponse(HttpsURLConnection connection) {
		try {
			StringBuilder str = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";

			while ((line = br.readLine()) != null) {
				str.append(line + System.getProperty("line.separator"));
			}

			return str.toString();

		} catch (IOException e) {
			return new String();
		}
	}

	private static void setFacebookAuthentication(HttpURLConnection con) throws IOException {
		String bearerToken;

		bearerToken = requestFacebookBearerToken("https://graph.facebook.com/v2.11/oauth/access_token");

		con.setRequestProperty("Host", "graph.facebook.com");
		con.setRequestProperty("Authorization", "Bearer " + bearerToken);
	}

	@SuppressWarnings("unused")
	private static String requestFacebookBearerToken(String endPointUrl) throws IOException {
		String applicationKey, secretKey, accessTokenRequestURL;
		HttpsURLConnection connection;

		applicationKey = applicationProperties.getFacebookApplicationKey();
		secretKey = applicationProperties.getFacebookSecretKey();
		connection = null;

		accessTokenRequestURL = endPointUrl + "?client_id=" + applicationKey + "&client_secret=" + secretKey;

		try {
			// URL url = new URL(endPointUrl);
			URL url = new URL(accessTokenRequestURL);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Host", "graph.facebook.com");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			connection.setRequestProperty("Content-Length", "29");
			connection.setUseCaches(false);

			writeRequest(connection, "grant_type=client_credentials");

			// Parse the JSON response into a JSON mapped object to fetch fields
			// from.
			/*
			 * JSONObject obj = (JSONObject) JSONValue .parse(readResponse(connection));
			 */

			try {
				JSONObject obj = new JSONObject(readResponse(connection));

				if (obj != null) {
					try {
						String tokenType = (String) obj.get("token_type");
						String token = (String) obj.get("access_token");

						return ((tokenType.equals("bearer")) && (token != null)) ? token : "";

					} catch (JSONException e) {
						throw new RESTServiceException("Wrong credentials", i18nCodeRoot + ".wrongcredentials",
								"RESTService", e);
					}

				}

			} catch (JSONException e) {
				throw new RESTServiceException("Wrong credentials", i18nCodeRoot + ".wrongcredentials", "RESTService",
						e);
			}

			return new String();

		} catch (MalformedURLException e) {
			throw new RESTServiceException("Invalid endpoint URL specified",
					i18nCodeRoot + ".invalidendpointURLspecified", "RESTService", e);

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

}