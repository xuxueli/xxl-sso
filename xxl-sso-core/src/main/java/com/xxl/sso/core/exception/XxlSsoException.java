package com.xxl.sso.core.exception;

import com.xxl.sso.core.constant.Const;

/**
 * xxl sso exception
 *
 * <p>
 * 统一运行时异常，默认错误码为 {@link Const#CODE_LOGIN_ERROR}；认证组件可携带
 * {@link Const#CODE_LOGIN_FAIL} 等错误码用于区分失败场景。
 * </p>
 *
 * @author xuxueli 2018-04-02 21:01:41
 */
public class XxlSsoException extends RuntimeException {
    private static final long serialVersionUID = 42L;

    /**
     * error code, default {@link Const#CODE_LOGIN_ERROR}
     */
    private int errorCode = Const.CODE_LOGIN_ERROR;

    /**
     * create exception with default error code
     *
     * @param msg   error message
     */
    public XxlSsoException(String msg) {
        super(msg);
    }

    /**
     * create exception with error code
     *
     * @param errorCode error code
     * @param msg       error message
     */
    public XxlSsoException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    /**
     * create exception with default error code and cause
     *
     * @param msg   error message
     * @param cause cause
     */
    public XxlSsoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * create exception with default error code and cause
     *
     * @param cause cause
     */
    public XxlSsoException(Throwable cause) {
        super(cause);
    }

    /**
     * get error code
     *
     * @return error code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * set error code
     *
     * @param errorCode error code
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
