package com.xxl.sso.core.test.util;

import com.xxl.sso.core.util.JedisTool;
import com.xxl.tool.id.UUIDTool;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link JedisTool} test
 *
 * @author xuxueli 2018-04-02
 */
public class JedisToolTest {

    @Test
    public void test() {
        String key = "xxl-sso-test-" + UUIDTool.getSimpleUUID();
        JedisTool jedisTool = new JedisTool("127.0.0.1:6379", null, null);

        // start twice: idempotent, should not throw or leak a second pool
        jedisTool.start();
        jedisTool.start();

        try {
            jedisTool.set(key, "666", 5 * 60);
            assertEquals("666", jedisTool.get(key));

            jedisTool.del(key);
            assertNull(jedisTool.get(key));
        } finally {
            // stop twice: idempotent
            jedisTool.stop();
            jedisTool.stop();
        }

        // restart after stop
        jedisTool.start();
        try {
            jedisTool.set(key, "777", 5 * 60);
            assertEquals("777", jedisTool.get(key));
            jedisTool.del(key);
        } finally {
            jedisTool.stop();
        }
    }

}
