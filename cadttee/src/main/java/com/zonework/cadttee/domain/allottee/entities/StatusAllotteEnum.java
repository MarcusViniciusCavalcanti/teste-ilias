package com.zonework.cadttee.domain.allottee.entities;

import com.zonework.cadttee.structure.dto.OperationEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum StatusAllotteEnum {

    REGISTRATION_PENDING(1, "REGISTRATION_PENDING") {
        @Override
        public OperationEnum operation() {
            return OperationEnum.CREATION;
        }
    },

    EXCLUDED(2, "EXCLUDED") {
        @Override
        public OperationEnum operation() {
            return OperationEnum.EXCLUSION;
        }
    },

    ACTIVE(4, "ACTIVE") {
        @Override
        public OperationEnum operation() {
            return OperationEnum.UPDATE_STATUS;
        }
    },

    REJECTED(5, "REJECTED") {
        @Override
        public OperationEnum operation() {
            return OperationEnum.REJECTED;
        }
    };

    private static final Map<Integer, StatusAllotteEnum> VALUES =
        Stream.of(values()).collect(Collectors.toMap(StatusAllotteEnum::getStatusID, Function.identity()));

    private final Integer statusID;

    private final String statusEnumName;

    public static StatusAllotteEnum getById(Integer id) {
        return VALUES.get(id);
    }

    public abstract OperationEnum operation();
}
