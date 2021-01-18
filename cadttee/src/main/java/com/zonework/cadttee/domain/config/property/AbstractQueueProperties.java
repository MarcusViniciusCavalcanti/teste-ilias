package com.zonework.cadttee.domain.config.property;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractQueueProperties {

    private String name;
    private String dlqName;

}
