package com.wechat.checkin.common.exception;

import com.wechat.checkin.common.response.ResultCode;
import lombok.Getter;

/**
 * 系统异常类
 *
 * @author WeChat Check-in System
 * @since 1.0.0
 */
@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    public SystemException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public SystemException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    public SystemException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public SystemException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}