package com.xxl.sso.core.exception;

import com.xxl.sso.core.constant.Const;

/**
 * @author xuxueli 2018-04-02 21:01:41
 */
public class XxlSsoException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    private int errorCode = Const.CODE_SYSTEM_ERROR;

    public XxlSsoException(String msg) {
        super(msg);
    }

    public XxlSsoException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public XxlSsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public XxlSsoException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
