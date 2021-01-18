package com.zonework.cadttee.domain.queues.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.stream.Collector;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtils {

	public static <T> Collector<T, ?, T> toSingleton(String errorMessage) {
	    return Collectors.collectingAndThen(
	            Collectors.toList(),
	            list -> {
	                if (list.size() != 1) {
	                    throw new IllegalStateException(errorMessage);
	                }
	                return list.get(0);
	            }
	    );
	}

}
