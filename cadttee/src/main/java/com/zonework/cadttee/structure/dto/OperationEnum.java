package com.zonework.cadttee.structure.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zonework.cadttee.domain.allottee.entities.Allottee;
import com.zonework.cadttee.domain.allottee.entities.StatusAllotteEnum;
import com.zonework.cadttee.domain.allottee.factory.AllotteeFactory;

public enum OperationEnum {
    CREATION {
        @Override
        public String getValueByOperation(Allottee allottee) {
            return StatusAllotteEnum.REGISTRATION_PENDING.getStatusEnumName();
        }
    }, EXCLUSION {
        @Override
        public String getValueByOperation(Allottee allottee) {
            return StatusAllotteEnum.EXCLUDED.getStatusEnumName();
        }
    }, REJECTED {
        @Override
        public String getValueByOperation(Allottee allottee) {
            return StatusAllotteEnum.REJECTED.getStatusEnumName();
        }
    }, UPDATE {
        @Override
        public String getValueByOperation(Allottee allottee) {
            try {
                var dto = AllotteeFactory.buildEntityByAllottee(allottee);
                return new ObjectMapper().writeValueAsString(dto);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }, UPDATE_STATUS {
        @Override
        public String getValueByOperation(Allottee allottee) {
            return allottee.getStatus().getValue();
        }
    };

    public abstract String getValueByOperation(Allottee allottee);
}
