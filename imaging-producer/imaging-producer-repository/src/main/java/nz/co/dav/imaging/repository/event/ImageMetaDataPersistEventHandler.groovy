package nz.co.dav.imaging.repository.event

import groovy.json.JsonSlurper
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

	JsonSlurper jsonSlurper

	public ImageMetaDataPersistEventHandler(final EventBus imageMetaDataPersistEventBus,final ImagingMetaDataRepository imagingMetaDataRepository,final JsonSlurper jsonSlurper) {
		this.imageMetaDataPersistEventBus = imageMetaDataPersistEventBus
		imageMetaDataPersistEventBus.register(this)
		this.imagingMetaDataRepository = imagingMetaDataRepository
		this.jsonSlurper = jsonSlurper
	}

	@Subscribe
	Set<String> persist(final ImageMetaDataPersistEvent imageMetaDataPersistEvent) {
		log.info "imageMetaDataPersistEvent:{} $imageMetaDataPersistEvent"
		Set<String> nodeUris = []
		String imageMataDataJson = imageMetaDataPersistEvent.imageMataDataJson
		List metaList = (List)jsonSlurper.parseText(imageMataDataJson)

		metaList.each{
			Map metaMap = (Map)it
			String metaSting = (String)it
			ImageMetaModel imageMetaModel = new ImageMetaModel(tag:metaMap['tag'],name:metaMap['name'],createTime:metaMap['DateTimeOriginal'],meta:metaSting)
			log.info "imageMetaModel:{} $imageMetaModel"
			String nodeUri
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
