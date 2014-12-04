package nz.co.dav.imaging.ds;

import nz.co.dav.imaging.ds.impl.ImagingProcessDSImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ImagingDSModule extends AbstractModule {

	@Override
	protected void configure() {

	}

	@Provides
	@Singleton
	public ImagingProcessDS imagingProcessDS() {
		return new ImagingProcessDSImpl();
	}
}
