package com.zonework.atm.domain.queues.send.batch;


import com.zonework.atm.domain.queues.send.dto.MessageToSend;
import com.zonework.atm.domain.queues.send.dto.batch.BatchOfMessages;
import lombok.Data;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Splits a batch of messages based on a max message size limit.
 */

@Data
public class BatchSizeLimitSplitter {

	public List<BatchOfMessages> split(BatchOfMessages original, long maxBytesSize, long bytesOverheadByMessage, Charset charset) {
		Assert.notNull(charset, "Charset must be provided");

		if (maxBytesSize == 0) {
			return Arrays.asList(original);
		}

		List<BatchOfMessages> splittedBatches = new ArrayList<>();

		List<MessageToSend> currentBatchContent = new ArrayList<>();

		long currentTotalSize = 0;

		for (MessageToSend message : original.getMessagesToSend()) {
			long messageSize = getMessageSize(message, bytesOverheadByMessage, charset);

			if (messageSize > maxBytesSize) {
				throw new IllegalArgumentException(String.format("The maxSize in batch for each batch is %s but there is a single message with a size larger than that: %s", maxBytesSize, messageSize));
			}

			if (currentTotalSize + messageSize > maxBytesSize) {
				splittedBatches.add(new BatchOfMessages(currentBatchContent));

				currentBatchContent = Arrays.asList(message);
				currentTotalSize = messageSize;
			} else {
				currentTotalSize += messageSize;
				currentBatchContent.add(message);
			}
		}

		splittedBatches.add(new BatchOfMessages(currentBatchContent));

		return splittedBatches;
	}

	private long getMessageSize(MessageToSend message, long bytesOverheadByMessage, Charset charset) {
		return message.getBody().getBytes(charset).length + bytesOverheadByMessage;
	}

}
