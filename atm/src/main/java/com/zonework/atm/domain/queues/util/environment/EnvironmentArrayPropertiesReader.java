package com.zonework.atm.domain.queues.util.environment;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

public class EnvironmentArrayPropertiesReader {

	private final Environment env;

	private final String prefix;

	private final String requiredProperty;

	private int currentIndex;

	public EnvironmentArrayPropertiesReader(Environment env, String prefix, String requiredProperty) {
		this.env = env;
		this.prefix = prefix;
		this.requiredProperty = requiredProperty;
	}

	public boolean hasNext() {
		return env.getProperty(propertyPath(requiredProperty)) != null;
	}

	public String getProperty(String property) {
		return env.getProperty(propertyPath(property));
	}

	public String getRequiredProperty(String property) {
		String value = env.getProperty(propertyPath(property));
		Assert.notNull(value, String.format("Property %s is required", propertyPath(property)));
		return value;
	}

	public <T> T getProperty(String property, Class<T> expectedType) {
		return env.getProperty(propertyPath(property), expectedType) ;
	}

	public <T> T getProperty(String property, Class<T> expectedType, T defaultValue) {
		return env.getProperty(propertyPath(property), expectedType, defaultValue);
	}

	public <T> T getRequiredProperty(String property, Class<T> expectedType) {
		T value = getProperty(property, expectedType);
		Assert.notNull(value, String.format("Property %s is required", propertyPath(property)));
		return value;
	}

	public void advance() {
		currentIndex++;
	}

	private String propertyPath(String property) {
		return String.format("%s[%s].%s", prefix, currentIndex, property);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

}
