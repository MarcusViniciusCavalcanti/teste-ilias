package com.zonework.cadttee.structure.message;

public enum ReasonMessages implements ExceptionMessageBuilder {
    MESSAGE_000("MESSAGE-000"),
    MESSAGE_001("MESSAGE-001"),
    MESSAGE_002("MESSAGE-002"),
    MESSAGE_003("MESSAGE-003"),
    MESSAGE_004("MESSAGE-004"),
    MESSAGE_005("MESSAGE-005"),
    MESSAGE_006("MESSAGE-006");

    private final String code;

    ReasonMessages(String message) {
        code = message;
    }

    @Override
    public String getCode() {
        return code;
    }
}
