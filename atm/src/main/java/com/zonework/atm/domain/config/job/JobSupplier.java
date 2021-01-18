package com.zonework.atm.domain.config.job;

import java.time.LocalDateTime;
import java.util.List;

public interface JobSupplier<T> {

    LocalDateTime getLastExecution(List<T> list);

    List<T> getElements(LocalDateTime jobLastExecution);

    void executeForElement(T element);

    String getJobName();

}
