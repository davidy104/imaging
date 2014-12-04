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
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class ImageCamelContextModule extends CamelModuleWithMatchingRoutes {

	@Override
	protected void configureCamelContext() {
		bind(CamelContext.class).to(ImageCamelContext.class).asEagerSingleton();
		bind(Processor.class).annotatedWith(Names.named("imageMetadataRetrievingProcessor")).to(ImageMetadataRetrievingProcessor.class).asEagerSingleton();
		bind(RouteBuilder.class).annotatedWith(Names.named("imageProcessRoute")).to(ImageProcessRoute.class).asEagerSingleton();
		bind(ImageProcess.class);
	}

	@Provides
	public Registry registry(final AmazonS3 amazonS3) {
		final SimpleRegistry simpleRegistry = new SimpleRegistry();
		simpleRegistry.put("amazonS3", amazonS3);
		return simpleRegistry;
	}

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
