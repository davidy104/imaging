package nz.co.dav.imaging.consume.integration;

import groovy.json.JsonSlurper;
import nz.co.dav.imaging.consume.config.ConfigurationService;
import nz.co.dav.imaging.consume.integration.config.ImageCamelContext;
import nz.co.dav.imaging.consume.integration.processor.GetImageMetaResponseTransformer;
import nz.co.dav.imaging.consume.integration.processor.ImageBytesAggregationStrategy;
import nz.co.dav.imaging.consume.integration.processor.ImageFetchFromS3Processor;
import nz.co.dav.imaging.consume.integration.processor.SendEmailTransformer;
import nz.co.dav.imaging.consume.integration.route.ImageBatchToEmail;
import nz.co.dav.imaging.consume.integration.route.ImageBatchToLocalFileRoute;
import nz.co.dav.imaging.consume.integration.route.ImageMetaAPIOperationRoute;
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
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class ImageCamelContextModule extends CamelModuleWithMatchingRoutes {

	@Override
	protected void configureCamelContext() {
		bind(CamelContext.class).to(ImageCamelContext.class).asEagerSingleton();
		bind(Registry.class).toProvider(RegistryProvider.class);
		bind(RouteBuilder.class)
				.annotatedWith(Names.named("imageBatchToLocalFileRoute"))
				.to(ImageBatchToLocalFileRoute.class);
		bind(ImageBatchToEmail.class).toProvider(ImageToEmailRouteBuilderProvicer.class).in(Scopes.SINGLETON);
		bind(RouteBuilder.class)
				.annotatedWith(Names.named("imageMetaAPIOperationRoute"))
				.to(ImageMetaAPIOperationRoute.class).in(Scopes.SINGLETON);
		bind(RouteBuilder.class)
				.annotatedWith(Names.named("imageReceivingRoute"))
				.to(ImageReceivingRoute.class).asEagerSingleton();
	}

	public static class ImageToEmailRouteBuilderProvicer implements Provider<ImageBatchToEmail> {
		@Inject
		ConfigurationService configurationService;

		@Override
		public ImageBatchToEmail get() {
			return new ImageBatchToEmail(configurationService, new SendEmailTransformer());
		}
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
	@Named("getImageMetaResponseTransformer")
	public GetImageMetaResponseTransformer getImageMetaResponseTransformer(@Named("jsonSlurper") JsonSlurper jsonSlurper) {
		return new GetImageMetaResponseTransformer(jsonSlurper);
	}

	@Provides
	@Singleton
	@Named("imageFetchFromS3Processor")
	public ImageFetchFromS3Processor imageFetchFromS3Processor(
			@Named("AWS.S3_BUCKET_NAME") String awsS3Bucket) {
		return new ImageFetchFromS3Processor(awsS3Bucket);
	}

	@Provides
	@Singleton
	@Named("imageBytesAggregationStrategy")
	public AggregationStrategy imageBytesAggregationStrategy(
			@Named("IMAGE_SIZE_PER_GROUP") String allowedTotalImageGroupSizeStr) {
		return new ImageBytesAggregationStrategy(allowedTotalImageGroupSizeStr);
	}

}
