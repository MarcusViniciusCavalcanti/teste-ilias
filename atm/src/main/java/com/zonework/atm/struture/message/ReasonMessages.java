package com.zonework.atm.struture.message;

// TODO refatorar
public enum ReasonMessages implements ExceptionMessageBuilder {
    MESSAGE_000("MESSAGE-000"),
    MESSAGE_001("MESSAGE-001"),
    MESSAGE_002("MESSAGE-002");

    private final String code;

    ReasonMessages(String message) {
        code = message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
