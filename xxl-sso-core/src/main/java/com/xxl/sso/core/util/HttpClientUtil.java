package com.xxl.sso.core.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http util to send data
 * 
 * @author xuxueli
 * @version 2015-11-28 15:30:59
 */
public class HttpClientUtil {
	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	/**
	 * http post request
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, String> params){
		HttpPost httpPost = null;
		CloseableHttpClient httpClient = null;
		try{
			// httpPost config
			httpPost = new HttpPost(url);
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for(Map.Entry<String,String> entry : params.entrySet()){
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			}
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			httpPost.setConfig(requestConfig);
			
			// httpClient = HttpClients.createDefault();	// default retry 3 times
			// httpClient = HttpClients.custom().setRetryHandler(new DefaultHttpRequestRetryHandler(3, true)).build();
			httpClient = HttpClients.custom().disableAutomaticRetries().build();
			
			// parse response
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				if (response.getStatusLine().getStatusCode() == 200) {
					String responseMsg = EntityUtils.toString(entity, "UTF-8");
					EntityUtils.consume(entity);
					return responseMsg;
				}
				EntityUtils.consume(entity);
			}
			logger.info("http statusCode error, statusCode:" + response.getStatusLine().getStatusCode());
			return null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally{
			if (httpPost!=null) {
				httpPost.releaseConnection();
			}
			if (httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		System.out.println(post("https://www.baidu.com/", null));
	}
	
}
