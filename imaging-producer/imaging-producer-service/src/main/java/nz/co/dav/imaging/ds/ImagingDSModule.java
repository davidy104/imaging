package nz.co.dav.imaging.ds;

import java.util.Map;

import nz.co.dav.imaging.convert.ImageMetaMapToModelConverter;
import nz.co.dav.imaging.ds.impl.ImagingProcessDSImpl;
import nz.co.dav.imaging.model.ImageMetaModel;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

public class ImagingDSModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ImagingProcessDS.class).to(ImagingProcessDSImpl.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	@Named("imageMetaMapToModelConverter")
	public Function<Map<String, String>, ImageMetaModel> imageMetaMapToModelConverter() {
		return new ImageMetaMapToModelConverter();
	}
}
