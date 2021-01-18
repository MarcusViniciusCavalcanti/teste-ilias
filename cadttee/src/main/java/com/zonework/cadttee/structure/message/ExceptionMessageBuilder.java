package com.zonework.cadttee.structure.message;

import com.zonework.cadttee.structure.exception.BusinessException;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Supplier;

public interface ExceptionMessageBuilder {

    default <T extends BusinessException> Supplier<T> asSupplier(Class<T> sourceClass, Object... args) {
        return () -> as(sourceClass, args);
    }

    default <T extends BusinessException> Supplier<T> asSupplier(Throwable cause,
                                                                 Class<T> sourceClass,
                                                                 Object... args) {
        return () -> as(cause, sourceClass, args);
    }

    default <T extends BusinessException> Supplier<T> asSupplier(Class<T> sourceClass) {
        return () -> as(sourceClass);
    }

    default <T extends BusinessException> Supplier<T> asSupplier(Throwable cause, Class<T> sourceClass) {
        return () -> as(cause, sourceClass);
    }

    default <T extends BusinessException> T as(Class<T> sourceClass) {
        return as(sourceClass, new Object[0]);
    }

    default <T extends BusinessException> T as(Throwable cause, Class<T> sourceClass) {
        return as(cause, sourceClass, new Object[0]);
    }

    default <T extends BusinessException> T as(Class<T> sourceClass, Object... args) {
        return as(null, sourceClass, args);
    }

    default <T extends BusinessException> T as(Throwable cause, Class<T> sourceClass, Object... args) {
        Objects.requireNonNull(sourceClass);

        //@formatter:off
        try {
            if (Objects.isNull(cause)) {
                return sourceClass.getConstructor(String.class, Object[].class).newInstance(getCode(), args);
            }
            return sourceClass
                .getConstructor(Throwable.class, String.class, Object[].class)
                .newInstance(cause, getCode(), args);
        } catch (NoSuchMethodException
            | InstantiationException
            | IllegalAccessException
            | InvocationTargetException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        //@formatter:on
    }

    String getCode();

}
