package com.zonework.cadttee.domain.queues.receive.listener;

import com.zonework.cadttee.domain.queues.configuration.properties.ConnectionType;

import java.util.Locale;

public interface ListenerManager {

	String LISTENER_MANAGER_BEAN_ID_SUFFIX = "ListenerManager";

	void stop(String queue);

	void start(String queue);

	static String listenerManagerBeanId(ConnectionType type) {
		return type.name().toLowerCase(Locale.ENGLISH) + LISTENER_MANAGER_BEAN_ID_SUFFIX;
	}

}
