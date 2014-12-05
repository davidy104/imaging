package nz.co.dav.imaging.consume;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import nz.co.dav.imaging.consume.config.ConfigurationServiceModule;
import nz.co.dav.imaging.consume.ds.ImagingDSModule;
import nz.co.dav.imaging.consume.integration.ImageCamelContextModule;

import org.apache.camel.CamelContext;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jolokia.jvmagent.JvmAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.sun.jersey.api.client.Client;

public class Bootstrap extends GuiceResteasyBootstrapServletContextListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Bootstrap.class);

	private CamelContext camelContext;
	private Client jerseyClient;

	@Override
	protected void withInjector(Injector injector) {
		try {
			this.camelContext = injector.getInstance(CamelContext.class);
			this.camelContext.start();
		} catch (final Exception e) {
			LOGGER.error("Failed to start camel context", e);
		}
		jerseyClient = injector.getInstance(Client.class);
	}

	@Override
	protected List<? extends Module> getModules(final ServletContext context) {
		JvmAgent.agentmain(null);
		return Arrays.asList(new ConfigurationServiceModule(),
				new SharedModule(),
				new ImageCamelContextModule(),
				new ImagingDSModule());
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {
		if (this.camelContext != null) {
			try {
				this.camelContext.stop();
			} catch (final Exception e) {
				LOGGER.error("Failed to stop camel context", e);
			}
		}
		jerseyClient.destroy();
		super.contextDestroyed(event);
		JvmAgent.agentmain("mode=stop");
	}

}
