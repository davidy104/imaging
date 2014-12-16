package nz.co.dav.imaging.webuser.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class ImagingWebInitializer implements
		WebApplicationInitializer {
	private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
	private static final String DISPATCHER_SERVLET_MAPPING = "/";

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		registerDispatcherServlet(servletContext);
		registerSitemesh(servletContext);
		registerHttPutFormContentFilter(servletContext);
		registerHiddenHttpMethodFilter(servletContext);
	}

	private void registerSitemesh(ServletContext servletContext) {
		// Sitemesh
		FilterRegistration.Dynamic sitemesh = servletContext.addFilter(
				"sitemesh", new ConfigurableSiteMeshFilter());
		EnumSet<DispatcherType> sitemeshDispatcherTypes = EnumSet.of(
				DispatcherType.REQUEST, DispatcherType.FORWARD);
		sitemesh.addMappingForUrlPatterns(sitemeshDispatcherTypes, true,
				"*.jsp");
	}

	private void registerDispatcherServlet(ServletContext servletContext) {
		AnnotationConfigWebApplicationContext rootContext = createContext(
				ApplicationContextConfiguration.class,
				WebMvcContextConfiguration.class);
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);
		servletContext.addListener(new ContextLoaderListener(rootContext));
	}

	private AnnotationConfigWebApplicationContext createContext(
			final Class<?>... annotatedClasses) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.register(annotatedClasses);
		return context;
	}

	private void registerHttPutFormContentFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic registration = servletContext.addFilter(
				"httpPutFormContentFilter", HttpPutFormContentFilter.class);
		registration.addMappingForServletNames(
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD),
				false, DISPATCHER_SERVLET_NAME);
	}

	private void registerHiddenHttpMethodFilter(ServletContext servletContext) {
		FilterRegistration.Dynamic registration = servletContext.addFilter(
				"hiddenHttpMethodFilter", HiddenHttpMethodFilter.class);
		registration.addMappingForServletNames(
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD),
				false, DISPATCHER_SERVLET_NAME);
	}

}
