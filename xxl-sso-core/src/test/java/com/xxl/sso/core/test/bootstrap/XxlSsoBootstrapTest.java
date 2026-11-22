package com.xxl.sso.core.test.bootstrap;

import com.xxl.sso.core.bootstrap.XxlSsoBootstrap;
import com.xxl.sso.core.exception.XxlSsoException;
import com.xxl.sso.core.store.impl.LocalLoginStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link XxlSsoBootstrap} test
 *
 * @author xuxueli 2026-11-22
 */
public class XxlSsoBootstrapTest {

    @Test
    public void startWithoutLoginStoreTest() {
        XxlSsoBootstrap bootstrap = new XxlSsoBootstrap();

        // fail fast with a clear exception instead of NPE
        assertThrows(XxlSsoException.class, bootstrap::start);
    }

    @Test
    public void stopWithoutStartTest() {
        XxlSsoBootstrap bootstrap = new XxlSsoBootstrap();

        // stop before start should be tolerated
        assertDoesNotThrow(bootstrap::stop);
    }

    @Test
    public void startWithLoginStoreTest() {
        XxlSsoBootstrap bootstrap = new XxlSsoBootstrap();
        bootstrap.setLoginStore(new LocalLoginStore());

        assertDoesNotThrow(bootstrap::start);
        assertDoesNotThrow(bootstrap::stop);
    }

}
