package com.zonework.cadttee.domain.queues.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConnectionType {

	RABBIT(true, PropertiesNames.RABBIT_ACTIVATION, false);

	private final boolean suportsListening;

	private final String activationProperty;

	private final boolean supportsDlq;

}
