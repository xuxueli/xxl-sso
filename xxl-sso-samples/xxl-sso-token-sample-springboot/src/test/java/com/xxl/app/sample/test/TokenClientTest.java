package com.xxl.app.sample.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.app.sample.test.util.HttpClientUtil;
import com.xxl.sso.core.conf.Conf;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2018-04-09 11:38:15
 */
public class TokenClientTest {
	private static Logger logger = LoggerFactory.getLogger(TokenClientTest.class);

	public static String ssoServer = "http://xxlssoserver.com:8080/xxl-sso-server";

	public static String client01 = "http://xxlssoclient1.com:8082/xxl-sso-token-sample-springboot/";
	public static String client02 = "http://xxlssoclient2.com:8082/xxl-sso-token-sample-springboot/";

	@Test
	public void test() throws Exception {

		// 登录：获取 sso sessionId
		String sessionId = loginTest();
		Assert.assertNotNull(sessionId);

		// 登陆状态校验
		String username = logincheckTest(sessionId);
		Assert.assertNotNull(username);

		clientApiRequestTest(client01, sessionId);
		clientApiRequestTest(client02, sessionId);

		// 注销：销毁 sso sessionId
		boolean loginoutResult = logoutTest(sessionId);
		Assert.assertTrue(loginoutResult);

		// 登陆状态校验
		username = logincheckTest(sessionId);
		Assert.assertNull(username);

		clientApiRequestTest(client01, sessionId);
		clientApiRequestTest(client02, sessionId);
	}

	/**
	 * Client API Request, SSO APP Filter
	 *
	 * @param clientApiUrl
	 * @return
	 */
	private void clientApiRequestTest(String clientApiUrl, String sessionId) throws IOException {

		Map<String, String> headerParam = new HashMap<>();
		headerParam.put(Conf.SSO_SESSIONID, sessionId);


		String resultJson = HttpClientUtil.post(clientApiUrl, null, headerParam);
		Map<String, Object> loginResult = new ObjectMapper().readValue(resultJson, Map.class);

		int code = (int) loginResult.get("code");
		if (code == 200) {

			Map user = (Map) loginResult.get("data");
			String username = (String) user.get("username");

			logger.info("模拟请求APP应用接口，请求成功，登陆用户 = " + username);
		} else {

			String failMsg = (String) loginResult.get("msg");

			logger.info("模拟请求APP应用接口，请求失败：" + failMsg);
		}
	}

	/**
	 * SSO Login
	 *
	 * @return
	 */
	private String loginTest() throws IOException {
		// login url
		String loginUrl = ssoServer + "/app/login";

		// login param
		Map<String, String> loginParam = new HashMap<>();
		loginParam.put("username", "user");
		loginParam.put("password", "123456");

		String loginResultJson = HttpClientUtil.post(loginUrl, loginParam, null);
		Map<String, Object> loginResult = new ObjectMapper().readValue(loginResultJson, Map.class);

		int code = (int) loginResult.get("code");
		if (code == 200) {

			String sessionId = (String) loginResult.get("data");
			logger.info("登录成功，sessionid = " + sessionId);

			return sessionId;
		} else {

			String failMsg = (String) loginResult.get("msg");
			logger.info("登录失败：" + failMsg);

			return null;
		}

	}

	/**
	 * SSO Logout
	 *
	 * @param sessionId
	 * @return
	 */
	private boolean logoutTest(String sessionId) throws IOException {
		// logout url
		String logoutUrl = ssoServer + "/app/logout";

		// logout param
		Map<String, String> logoutParam = new HashMap<>();
		logoutParam.put("sessionId", sessionId);

		String logoutResultJson = HttpClientUtil.post(logoutUrl, logoutParam, null);
		Map<String, Object> logoutResult = new ObjectMapper().readValue(logoutResultJson, Map.class);

		int code = (int) logoutResult.get("code");
		if (code == 200) {

			logger.info("注销成功");
			return true;
		} else {

			String failMsg = (String) logoutResult.get("msg");
			logger.info("注销失败：" + failMsg);

			return false;
		}

	}

	/**
	 * SSO Login Check
	 *
	 * @param sessionId
	 * @return
	 */
	private String logincheckTest(String sessionId) throws IOException {
		// logout url
		String logincheckUrl = ssoServer + "/app/logincheck";

		// logout param
		Map<String, String> logincheckParam = new HashMap<>();
		logincheckParam.put("sessionId", sessionId);

		String logincheckResultJson = HttpClientUtil.post(logincheckUrl, logincheckParam, null);
		Map<String, Object> logincheckResult = new ObjectMapper().readValue(logincheckResultJson, Map.class);

		int code = (int) logincheckResult.get("code");
		if (code == 200) {

			Map user = (Map) logincheckResult.get("data");
			String username = (String) user.get("username");

			logger.info("当前为登录状态，登陆用户 = " + username);

			return username;
		} else {

			logger.info("当前为注销状态");
			return null;
		}

	}

}