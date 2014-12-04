package nz.co.dav.imaging.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigurationServiceModule extends AbstractModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationServiceModule.class);

	@Override
	protected void configure() {
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(Resources.getResource("image.properties").getFile()));
			Names.bindProperties(binder(), properties);
		} catch (final FileNotFoundException e) {
			LOGGER.error("The configuration file Test.properties can not be found", e);
		} catch (final IOException e) {
			LOGGER.error("I/O Exception during loading configuration", e);
		}
		bind(ConfigurationService.class).to(ConfigurationServiceImpl.class).asEagerSingleton();
	}
}
