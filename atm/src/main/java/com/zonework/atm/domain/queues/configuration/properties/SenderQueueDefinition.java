package com.zonework.atm.domain.queues.configuration.properties;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class SenderQueueDefinition {
		@NonNull
		private final String name;

		@NonNull
		private final ConnectionType connection;

		@Builder.Default
		private final boolean topic = false;
}
