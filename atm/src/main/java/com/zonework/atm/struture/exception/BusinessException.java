package com.zonework.atm.struture.exception;

import org.springframework.util.Assert;

import java.util.Arrays;

public class BusinessException extends RuntimeException {

    private final String code;
    private final String field;
    private final String action;
    private final Object[] args;

    private BusinessException(Throwable cause, String code, String field, String action, Object... args) {
        super(code + " - " + Arrays.toString(args), cause);
        Assert.hasLength(code, "Não se pode criar uma BusinessException com um code vazio ou não informado");
        this.code = code;
        this.field = field;
        this.action = action;
        this.args = args;
    }

    public BusinessException(String code, Object... args) {
        this((Throwable) null, code, (String) null, (String) null, args);
    }

    public BusinessException(Throwable cause, String code, Object... args) {
        this(cause, code, (String) null, (String) null, args);
    }

    public static BusinessException buExceptionComField(String code, String field, Object... args) {
        return buExceptionComField((Throwable) null, code, field, args);
    }

    public static BusinessException buExceptionComField(Throwable cause, String code, String field, Object... args) {
        return new BusinessException(cause, code, field, (String) null, args);
    }

    public static BusinessException buExceptionComFieldAction(String code, String field, String action, Object... args) {
        return new BusinessException(code, field, action, args);
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getAction() {
        return action;
    }

    public Object[] getArgs() {
        return args;
    }

}
