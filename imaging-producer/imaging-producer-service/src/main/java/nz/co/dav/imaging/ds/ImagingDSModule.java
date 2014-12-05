package nz.co.dav.imaging.ds;

import nz.co.dav.imaging.ds.impl.ImagingProcessDSImpl;

import com.google.inject.AbstractModule;

public class ImagingDSModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ImagingProcessDS.class).to(ImagingProcessDSImpl.class).asEagerSingleton();
	}

	// @Provides
	// @Singleton
	// public ImagingProcessDS imagingProcessDS() {
	// return new ImagingProcessDSImpl();
	// }
}
