package nz.co.dav.imaging.repository.event

import groovy.util.logging.Slf4j
import nz.co.dav.imaging.event.ImageMetaDataPersistEvent
import nz.co.dav.imaging.model.ImageMetaModel
import nz.co.dav.imaging.repository.ImagingMetaDataRepository

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe

@Slf4j
class ImageMetaDataPersistEventHandler {

	ImagingMetaDataRepository imagingMetaDataRepository

	EventBus imageMetaDataPersistEventBus

	public ImageMetaDataPersistEventHandler(final EventBus imageMetaDataPersistEventBus,final ImagingMetaDataRepository imagingMetaDataRepository) {
		this.imageMetaDataPersistEventBus = imageMetaDataPersistEventBus
		imageMetaDataPersistEventBus.register(this)
		this.imagingMetaDataRepository = imagingMetaDataRepository
	}

	@Subscribe
	Set<String> persist(final ImageMetaDataPersistEvent imageMetaDataPersistEvent) {
		log.info "imageMetaDataPersistEvent:{} $imageMetaDataPersistEvent"
		Set<String> nodeUris = []

		imageMetaDataPersistEvent.imageMetaDataSet.each {
			String nodeUri
			ImageMetaModel imageMetaModel = new ImageMetaModel(tag:it['tag'],name:it['name'],createTime:it['DateTimeOriginal'],s3Prefix:it['s3Prefix'],s3Path:it['imagesS3Keis'])
			imageMetaModel.metaMap = it
			try {
				nodeUri = imagingMetaDataRepository.createImageMetaData(imageMetaModel)
				nodeUris << nodeUri
			} catch (final Exception e) {
				log.error "persist image metadata error. $e"
			}
		}

		return nodeUris
	}
}
