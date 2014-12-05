package nz.co.dav.imaging.consume.integration;

import java.util.concurrent.TimeUnit;

import org.apache.camel.ThreadPoolRejectedPolicy;
import org.apache.camel.guice.GuiceCamelContext;
import org.apache.camel.spi.CamelContextNameStrategy;
import org.apache.camel.spi.ThreadPoolProfile;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class ImageCamelContext extends GuiceCamelContext {

	@Inject
	public ImageCamelContext(final Injector injector) {
		super(injector);
		setStreamCaching(Boolean.FALSE);
		getExecutorServiceManager().registerThreadPoolProfile(
				initialGeneralThreadPoolProfile());
	}

	@Override
	@Inject(optional = true)
	public void setNameStrategy(final CamelContextNameStrategy nameStrategy) {
		super.setNameStrategy(nameStrategy);
	}

	private ThreadPoolProfile initialGeneralThreadPoolProfile() {
		ThreadPoolProfile profile = new ThreadPoolProfile();
		profile.setId("genericThreadPool");
		profile.setKeepAliveTime(120L);
		profile.setPoolSize(5);
		profile.setMaxPoolSize(20);
		profile.setTimeUnit(TimeUnit.SECONDS);
		profile.setRejectedPolicy(ThreadPoolRejectedPolicy.Abort);
		return profile;
	}

}
