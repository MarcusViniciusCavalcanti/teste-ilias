package com.zonework.atm.domain.queues.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanUtils {

	private static final BeanUtilsNonNullCopier NON_NULL_COPIER = new BeanUtilsNonNullCopier();

	public static void copyNonNullProperties(Object source, Object destination) {
		try {
			NON_NULL_COPIER.copyProperties(destination, source);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private static class BeanUtilsNonNullCopier extends BeanUtilsBean {

		@Override
		public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException {
			if (value != null) {
				super.copyProperty(bean, name, value);
			}
		}

	}

}
