package nz.co.dav.imaging.integration;

import nz.co.dav.imaging.integration.config.ImageCamelContext;
import nz.co.dav.imaging.integration.ds.ImageProcess;
import nz.co.dav.imaging.integration.processor.ImageMetadataAggregationStrategy;
import nz.co.dav.imaging.integration.processor.ImageMetadataRetrievingProcessor;
import nz.co.dav.imaging.integration.processor.ImageScalingProcessor;
import nz.co.dav.imaging.integration.route.ImageProcessRoute;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.spi.CamelContextNameStrategy;
import org.apache.camel.spi.Registry;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sqs.AmazonSQS;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class ImageCamelContextModule extends CamelModuleWithMatchingRoutes {

	@Override
	protected void configureCamelContext() {
		bind(CamelContext.class).to(ImageCamelContext.class).asEagerSingleton();
		bind(Registry.class).toProvider(RegistryProvider.class);
		// bind(ImageEventConsumer.class).toProvider(ImageEventConsumerProvider.class).asEagerSingleton();
		bind(Processor.class).annotatedWith(Names.named("imageMetadataRetrievingProcessor")).to(ImageMetadataRetrievingProcessor.class).asEagerSingleton();
		bind(RouteBuilder.class).annotatedWith(Names.named("imageProcessRoute")).to(ImageProcessRoute.class).asEagerSingleton();
		bind(ImageProcess.class);
	}

	public static class RegistryProvider implements Provider<Registry> {
		@Inject
		AmazonS3 amazonS3;

		@Inject
		AmazonSQS amazonSQS;

		@Inject
		AmazonSNSClient amazonSNSClient;

		@Override
		public Registry get() {
			final SimpleRegistry simpleRegistry = new SimpleRegistry();
			simpleRegistry.put("amazonS3", amazonS3);
			simpleRegistry.put("amazonSqs", amazonSQS);
			simpleRegistry.put("amazonSns", amazonSNSClient);
			return simpleRegistry;
		}
	}

	// public static class ImageEventConsumerProvider implements
	// Provider<ImageEventConsumer> {
	// @Inject
	// @Named("AWS.SQS_EVENT_QUEUE_NAME")
	// String awsSqsEventQueueName;
	//
	// @Override
	// public ImageEventConsumer get() {
	// final String endpointUri = "aws-sqs://" + awsSqsEventQueueName +
	// "?amazonSQSClient=#amazonSqs&delay=3000";
	// return new ImageEventConsumer(endpointUri);
	// }
	// }

	@Provides
	public CamelContextNameStrategy camelContextNameStrategy() {
		return new ExplicitCamelContextNameStrategy("Image");
	}

	@Provides
	@Singleton
	@Named("imageScalingProcessor")
	public Processor imageScalingProcessor() {
		return new ImageScalingProcessor();
	}

	@Provides
	@Singleton
	@Named("imageMetadataAggregationStrategy")
	public AggregationStrategy imageMetadataAggregationStrategy() {
		return new ImageMetadataAggregationStrategy();
	}

}
