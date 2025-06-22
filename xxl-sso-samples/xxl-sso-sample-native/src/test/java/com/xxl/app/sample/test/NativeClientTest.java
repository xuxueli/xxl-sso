package com.xxl.app.sample.test;

import com.xxl.sso.core.constant.Const;
import com.xxl.sso.core.model.LoginInfo;
import com.xxl.tool.gson.GsonTool;
import com.xxl.tool.http.HttpTool;
import com.xxl.tool.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2018-04-09 11:38:15
 */
public class NativeClientTest {
	private static Logger logger = LoggerFactory.getLogger(NativeClientTest.class);

	public static String ssoServer = "http://xxlssoserver.com:8080/xxl-sso-server";

	public static String client01 = "http://xxlssoclient1.com:8082/xxl-sso-sample-native/";
	public static String client02 = "http://xxlssoclient2.com:8082/xxl-sso-sample-native/";

	@Test
	public void test() throws Exception {

		// 登录：获取 token
		String token = loginTest();					// Login 》Server OpenApi ：general “token”， write redis
		Assertions.assertNotNull(token);

		// 登陆状态校验
		String username = logincheckTest(token);	// Check 》 Server OpenApi：param “token”
		Assertions.assertNotNull(username);

		clientApiRequestTest(client01, token);		// Check2 》Client Filter：header “token”
		clientApiRequestTest(client02, token);

		// 注销：注销 token
		boolean loginoutResult = logoutTest(token);	// Logout 》 Server OpenApi ：param “token”
		Assertions.assertTrue(loginoutResult);

		// 登陆状态校验
		username = logincheckTest(token);
		Assertions.assertNull(username);

		clientApiRequestTest(client01, token);
		clientApiRequestTest(client02, token);
	}

	/**
	 * Client API Request, SSO APP Filter
	 */
	private void clientApiRequestTest(String url, String token) throws IOException {

		Map<String, String> headerParam = new HashMap<>();
		headerParam.put(Const.XXL_SSO_TOKEN, token);

		String resultJson = HttpTool.postBody(url, null, headerParam, 3*1000);
		Response<String> loginResult = GsonTool.fromJson(resultJson, Response.class, String.class);

		if (loginResult.isSuccess()) {

			String username = loginResult.getData();

			logger.info("模拟请求APP应用接口，请求成功，登陆用户 = " + username);
		} else {

			String failMsg = loginResult.getMsg();

			logger.info("模拟请求APP应用接口，请求失败：" + failMsg);
		}
	}

	/**
	 * Login
	 */
	private String loginTest() throws IOException {
		// url
		String loginUrl = ssoServer + "/app/login";

		// param
		Map<String, String> params = new HashMap<>();
		params.put("username", "user");
		params.put("password", "123456");
		String requestBody = GsonTool.toJson(params);

		// request
		String loginResultJson = HttpTool.postBody(loginUrl, requestBody);
		Response<String> loginResult = GsonTool.fromJson(loginResultJson, Response.class, String.class);

		if (loginResult.isSuccess()) {

			String token = loginResult.getData();
			logger.info("登录成功，token = " + token);

			return token;
		} else {

			String failMsg = loginResult.getMsg();
			logger.info("登录失败：" + failMsg);

			return null;
		}

	}

	/**
	 * Logout
	 */
	private boolean logoutTest(String token) throws IOException {
		// url
		String logoutUrl = ssoServer + "/app/logout";

		// param
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		String requestBody = GsonTool.toJson(params);

		// request
		String logoutResultJson = HttpTool.postBody(logoutUrl, requestBody);
		Response<String> loginResult = GsonTool.fromJson(logoutResultJson, Response.class, String.class);

		if (loginResult.isSuccess()) {

			logger.info("注销成功");
			return true;
		} else {

			String failMsg = loginResult.getMsg();
			logger.info("注销失败：" + failMsg);
			return false;
		}

	}

	/**
	 * Login Check
	 */
	private String logincheckTest(String token) throws IOException {
		// url
		String logincheckUrl = ssoServer + "/app/logincheck";

		// param
		Map<String, String> params = new HashMap<>();
		params.put("token", token);
		String requestBody = GsonTool.toJson(params);

		// request
		String logincheckResultJson = HttpTool.postBody(logincheckUrl, requestBody);
		Response<LoginInfo> loginResult = GsonTool.fromJson(logincheckResultJson, Response.class, LoginInfo.class);

		if (loginResult.isSuccess()) {

			LoginInfo loginInfo = loginResult.getData();

			logger.info("当前为登录状态，登陆用户 = " + loginInfo.getUserName());

			return loginInfo.getUserName();
		} else {

			logger.info("当前为注销状态");
			return null;
		}

	}

}