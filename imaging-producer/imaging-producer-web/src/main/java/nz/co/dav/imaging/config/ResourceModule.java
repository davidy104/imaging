package nz.co.dav.imaging.config;

import nz.co.dav.imaging.resources.ImagingResource;

import com.google.inject.AbstractModule;

public class ResourceModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ImagingResource.class);
	}
}
