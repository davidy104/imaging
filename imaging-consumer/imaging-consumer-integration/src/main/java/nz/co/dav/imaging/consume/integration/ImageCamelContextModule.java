package nz.co.dav.imaging.consume.integration;

import nz.co.dav.imaging.consume.integration.config.ImageCamelContext;
import nz.co.dav.imaging.consume.integration.processor.ImageBytesAggregationStrategy;
import nz.co.dav.imaging.consume.integration.processor.ImageEventMessageReceivingProcessor;
import nz.co.dav.imaging.consume.integration.processor.ImageFetchFromS3Processor;
import nz.co.dav.imaging.consume.integration.route.ImageReceivingRoute;

import org.apache.camel.CamelContext;
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
		bind(RouteBuilder.class).annotatedWith(Names.named("imageReceivingRoute")).to(ImageReceivingRoute.class).asEagerSingleton();
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

	@Provides
	public CamelContextNameStrategy camelContextNameStrategy() {
		return new ExplicitCamelContextNameStrategy("ImageConsumer");
	}

	@Provides
	@Singleton
	@Named("imageEventMessageReceivingProcessor")
	public ImageEventMessageReceivingProcessor imageEventMessageReceivingProcessor() {
		return new ImageEventMessageReceivingProcessor();
	}

	@Provides
	@Singleton
	@Named("imageFetchFromS3Processor")
	public ImageFetchFromS3Processor imageFetchFromS3Processor() {
		return new ImageFetchFromS3Processor();
	}

	@Provides
	@Singleton
	@Named("imageBytesAggregationStrategy")
	public AggregationStrategy imageBytesAggregationStrategy() {
		return new ImageBytesAggregationStrategy();
	}

}
