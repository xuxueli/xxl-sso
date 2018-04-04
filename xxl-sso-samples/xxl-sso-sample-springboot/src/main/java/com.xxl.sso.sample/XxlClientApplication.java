package com.xxl.sso.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xuxueli 2018-03-22 23:41:47
 */
@EnableAutoConfiguration
@SpringBootApplication
public class XxlClientApplication {

	public static void main(String[] args) {
        SpringApplication.run(XxlClientApplication.class, args);
	}

}