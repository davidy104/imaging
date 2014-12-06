package nz.co.dav.imaging.integration.event;

import groovy.util.logging.Slf4j

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.GetQueueUrlRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.google.common.base.Joiner
import com.google.common.collect.Sets
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.Inject
@Slf4j
public class ImageS3SendEventHandler {

	Set<String> imageS3SendKeySet = Sets.<String>newHashSet();

	EventBus imageSendEventBus

	@Inject
	AmazonSQS amazonSQS

	public ImageS3SendEventHandler(final EventBus imageSendEventBus) {
		this.imageSendEventBus = imageSendEventBus
		imageSendEventBus.register(this)
	}

	@Subscribe
	void collectImageS3SentEvent(final ImageSentToS3Event imageSentToS3Event){
		log.info "collectImageS3SentEvent:{} $imageSentToS3Event"
		imageS3SendKeySet.add(imageSentToS3Event.s3Key)
	}

	@Subscribe
	void sendSqsImagesSentEvent(final ImagesSentCompletedEvent imagesSentCompletedEvent){
		log.info "sendSqsImagesSentEvent:{} $imagesSentCompletedEvent"
		log.info "imageS3SendKeySet:{} $imageS3SendKeySet"
		if(imageS3SendKeySet){
			def message = Joiner.on(",").join(imageS3SendKeySet)
			def queueUrl = amazonSQS.getQueueUrl(new GetQueueUrlRequest(
					imagesSentCompletedEvent.sqsEventQueueName)).queueUrl
			amazonSQS.sendMessage(new SendMessageRequest(queueUrl, message))
			imageS3SendKeySet.clear()
		}
	}
}
