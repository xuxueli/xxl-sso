package com.xxl.sso.core.test.util;

import com.xxl.sso.core.util.JedisTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JedisToolTest {
    private static Logger logger = LoggerFactory.getLogger(JedisToolTest.class);

    @Test
    public void test() {
        String nodes = "127.0.0.1:6379";

        JedisTool jedisTool = new JedisTool(nodes, null, null);
        jedisTool.start();

        jedisTool.set("key", "666", 5*60);
        System.out.println(jedisTool.get("key"));

        jedisTool.del("key");
        System.out.println(jedisTool.get("key"));
    }


}
