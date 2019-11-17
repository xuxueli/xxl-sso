package com.xxl.sso.sample.controller;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.entity.ReturnT;
import com.xxl.sso.core.user.XxlSsoUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Wu Weijie
 */
@RestController
@RequestMapping("/demo")
public class IndexController {

    @GetMapping("/user")
    public Mono<ReturnT<XxlSsoUser>> index(ServerWebExchange exchange) {
        XxlSsoUser xxlUser = exchange.getAttribute(Conf.SSO_USER);
        return Mono.just(new ReturnT<>(xxlUser));
    }
}