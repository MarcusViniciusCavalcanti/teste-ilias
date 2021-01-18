package com.zonework.atm.struture.utils;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionRunnableHelper {

    @Transactional
    public void withTransaction(Runnable runnable) {
        runnable.run();
    }

}
