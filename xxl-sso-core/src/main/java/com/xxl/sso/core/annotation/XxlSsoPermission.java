package com.xxl.sso.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * permission annotation
 *
 * <pre>
 * 		@Permission						: need login, but not valid permission
 * 		@Permission("user:add")			: need login, and valid permission
 * 		@Permission(role = "admin")		: need login, and valid role
 * 		@Permission(login = false)		: not need login, not valid anything
 * </pre>
 *
 * @author xuxueli 2015-12-12 18:29:02
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlSsoPermission {

	/**
	 * need login
	 */
	boolean login() default true;

	/**
	 * permission value (need login)
	 */
	String value() default "";

	/**
	 * role value (need login)
	 */
	String role() default "";

}