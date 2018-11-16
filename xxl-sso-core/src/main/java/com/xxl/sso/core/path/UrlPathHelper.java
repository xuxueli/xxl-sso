package com.xxl.sso.core.path;

import com.xxl.sso.core.filter.XxlSsoWebFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * Helper class for URL path matching. Provides support for URL paths in
 * RequestDispatcher includes and support for consistent URL decoding.	(borrowed from spring)
 */
public class UrlPathHelper {
	private static Logger logger = LoggerFactory.getLogger(XxlSsoWebFilter.class);


	public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
	public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

	/**
	 * Return the request URI for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * <p>As the value returned by <code>request.getRequestURI()</code> is <i>not</i>
	 * decoded by the servlet container, this method will decode it.
	 * <p>The URI that the web container resolves <i>should</i> be correct, but some
	 * containers like JBoss/Jetty incorrectly include ";" strings like ";jsessionid"
	 * in the URI. This method cuts off such incorrect appendices.
	 *
	 * @param request current HTTP request
	 * @return the request URI
	 */
	public static String getRequestUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
		if (uri == null) {
			uri = request.getRequestURI();
		}
		return normalize(decodeAndCleanUriString(request, uri));
	}

	/**
	 * Normalize a relative URI path that may have relative values ("/./",
	 * "/../", and so on ) it it.  <strong>WARNING</strong> - This method is
	 * useful only for normalizing application-generated paths.  It does not
	 * try to perform security checks for malicious input.
	 * Normalize operations were was happily taken from org.apache.catalina.util.RequestUtil in
	 * Tomcat trunk, r939305
	 *
	 * @param path Relative path to be normalized
	 * @return normalized path
	 */
	public static String normalize(String path) {
		return normalize(path, true);
	}


	/**
	 * Normalize a relative URI path that may have relative values ("/./",
	 * "/../", and so on ) it it.  <strong>WARNING</strong> - This method is
	 * useful only for normalizing application-generated paths.  It does not
	 * try to perform security checks for malicious input.
	 * Normalize operations were was happily taken from org.apache.catalina.util.RequestUtil in
	 * Tomcat trunk, r939305
	 *
	 * @param path             Relative path to be normalized
	 * @param replaceBackSlash Should '\\' be replaced with '/'
	 * @return normalized path
	 */
	private static String normalize(String path, boolean replaceBackSlash) {

		if (path == null)
			return null;

		// Create a place for the normalized path
		String normalized = path;

		if (replaceBackSlash && normalized.indexOf('\\') >= 0) {
			normalized = normalized.replace('\\', '/');
		}

		if ("/.".equals(normalized)) {
			return "/";
		}

		// Add a leading "/" if necessary
		if (!normalized.startsWith("/")) {
			normalized = "/" + normalized;
		}

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0) {
				break;
			}
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0) {
				break;
			}
			if (index == 0) {
				return (null);  // Trying to go outside our context
			}
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}
		// Return the normalized path that we have completed
		return normalized;
	}


	/**
	 * Decode the supplied URI string and strips any extraneous portion after a ';'.
	 *
	 * @param request the incoming HttpServletRequest
	 * @param uri     the application's URI string
	 * @return the supplied URI string stripped of any extraneous portion after a ';'.
	 */
	private static String decodeAndCleanUriString(HttpServletRequest request, String uri) {
		uri = decodeRequestString(request, uri);
		int semicolonIndex = uri.indexOf(';');
		return (semicolonIndex != -1 ? uri.substring(0, semicolonIndex) : uri);
	}


	@SuppressWarnings("deprecation")
	public static String decodeRequestString(HttpServletRequest request, String source) {
		String enc = determineEncoding(request);
		try {
			return URLDecoder.decode(source, enc);
		} catch (UnsupportedEncodingException ex) {
			logger.warn("SSO Decode ERROR with code {}, source={}", enc, source);
			return URLDecoder.decode(source);
		}
	}

	protected static String determineEncoding(HttpServletRequest request) {
		String enc = request.getCharacterEncoding();
		if (enc == null) {
			enc = DEFAULT_CHARACTER_ENCODING;
		}
		return enc;
	}


}
