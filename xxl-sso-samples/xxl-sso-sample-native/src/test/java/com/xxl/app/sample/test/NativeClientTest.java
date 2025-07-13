package com.xxl.app.sample.test;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.sso.sample.openapi.model.LoginCheckRequest;
import com.xxl.sso.sample.openapi.model.LoginRequest;
import com.xxl.sso.sample.openapi.model.LogoutRequest;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2018-04-09 11:38:15
 */
public class NativeClientTest {
	private static Logger logger = LoggerFactory.getLogger(NativeClientTest.class);

	/**
	 * native client, support multi-domain client applications
	 */
	public static String client01 = "http://xxlssoclient1.com:8082/xxl-sso-sample-native";
	public static String client02 = "http://xxlssoclient2.com:8082/xxl-sso-sample-native";

	/**
	 * For openapi server, it is recommended to deploy and provide services separately
	 */
	public static String openApiServer = client01;

	@Test
	public void nativeClietTest() throws Exception {

		/**
		 * 1、login（登录）：生成并获取 token
		 * 		- request：Client -> OpenApi
		 * 		- logic：general “token”， write redis
		 */
		String token = login();
		Assertions.assertNotNull(token);

		/**
		 * 2、logincheck（登录态校验）：校验登录态，获取登录用户信息
		 */
		String username = logincheck(token);	// Check 》 Server OpenApi：param “token”
		Assertions.assertNotNull(username);

		/**
		 * 3、模拟请求APP应用接口：客户端校验登录态
		 */
		clientApiRequestTest(client01, token);		// Check2 》Client Filter：header “token”
		clientApiRequestTest(client02, token);

		/**
		 * 4、logout（注销）：注销 token
		 * 		- request：Client -> OpenApi
		 * 		- logic：remove “token”， remove redis
		 */
		boolean loginoutResult = logout(token);	// Logout 》 Server OpenApi ：param “token”
		Assertions.assertTrue(loginoutResult);

		/**
		 * 4、模拟请求APP应用接口：客户端校验登录态
		 */
		username = logincheck(token);
		Assertions.assertNull(username);

		/**
		 * 模拟请求APP应用接口
		 */
		clientApiRequestTest(client01, token);
		clientApiRequestTest(client02, token);
	}

	/**
	 * Client API Request, with XxlSsoNativeFilter
	 */
	private void clientApiRequestTest(String url, String token) {

		// url
		String finalUrl = url + "/";

		// param
		Map<String, String> headerParam = new HashMap<>();
		headerParam.put(Const.XXL_SSO_TOKEN, token);

		// invoke
		String resultJson = HttpTool.postBody(finalUrl, null, headerParam, 3*1000);
		Response<LoginInfo> loginResult = GsonTool.fromJson(resultJson, Response.class, LoginInfo.class);

		// result
		if (loginResult.isSuccess()) {
			logger.info("模拟请求APP应用接口：请求成功，登陆用户 = " + loginResult.getData());
		} else {
			logger.info("模拟请求APP应用接口：请求失败：" + loginResult.getMsg());
		}
	}

	/**
	 * Login
	 */
	private String login() {
		// url
		String loginUrl = openApiServer + "/native/openapi/login";

		// param
		LoginRequest loginRequest = new LoginRequest("user","123456" );
		String requestBody = GsonTool.toJson(loginRequest);

		// invoke
		String loginResultJson = HttpTool.postBody(loginUrl, requestBody);
		Response<String> loginResult = GsonTool.fromJson(loginResultJson, Response.class, String.class);

		// result
		if (loginResult.isSuccess()) {

			String token = loginResult.getData();
			logger.info("登录成功：token = " + token);
			return token;
		} else {
			logger.info("登录失败：" + loginResult.getMsg());
			return null;
		}

	}

	/**
	 * Logout
	 */
	private boolean logout(String token) {
		// url
		String logoutUrl = openApiServer + "/native/openapi/logout";

		// param
		LogoutRequest logoutRequest = new LogoutRequest(token);
		String requestBody = GsonTool.toJson(logoutRequest);

		// invoke
		String logoutResultJson = HttpTool.postBody(logoutUrl, requestBody);
		Response<String> loginResult = GsonTool.fromJson(logoutResultJson, Response.class, String.class);

		// result
		if (loginResult.isSuccess()) {
			logger.info("注销成功");
			return true;
		} else {
			logger.info("注销失败：" + loginResult.getMsg());
			return false;
		}

	}

	/**
	 * Login Check
	 */
	private String logincheck(String token) {
		// url
		String logincheckUrl = openApiServer + "/native/openapi/logincheck";

		// param
		LoginCheckRequest loginCheckRequest = new LoginCheckRequest(token);
		String requestBody = GsonTool.toJson(loginCheckRequest);

		// invoke
		String logincheckResultJson = HttpTool.postBody(logincheckUrl, requestBody);
		Response<LoginInfo> loginResult = GsonTool.fromJson(logincheckResultJson, Response.class, LoginInfo.class);

		// result
		if (loginResult.isSuccess()) {
			logger.info("当前为登录状态：登陆用户 = " + loginResult.getData());
			return loginResult.getData().getUserName();
		} else {
			logger.info("当前为注销状态");
			return null;
		}

	}

}