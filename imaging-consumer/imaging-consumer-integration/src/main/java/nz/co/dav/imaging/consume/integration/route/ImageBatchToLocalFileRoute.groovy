package nz.co.dav.imaging.consume.integration.route

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.RouteBuilder

import com.google.inject.Inject
import com.google.inject.name.Named

class ImageBatchToLocalFileRoute extends RouteBuilder {

	@Inject
	@Named("FILE.OUTPUT_PATH")
	String fileOutputPath

	int nameIndex =0

	int folderNumber=0

	@Override
	public void configure() {
		from("direct:imageBatchToLocalFile")
				.routeId("ImageBatchToLocalFileRoute")
				.split(body())
				.to("direct:doImageBatchToLocalFile")
				.end()

		from("direct:doImageBatchToLocalFile")
				.process(new Processor(){
					@Override
					void process(Exchange exchange) throws Exception {
						String imageOutputFolder = fileOutputPath+"/output"+folderNumber++
						println "imageOutputFolder:{} $imageOutputFolder"
						exchange.setProperty("imageOutputFolder", imageOutputFolder)
						Map body = exchange.in.getBody(Map.class)
						exchange.setProperty("imageFileNameList", body.keySet().toList())
					}
				})
				.to("direct:singleImageToLocalFile")

		from("direct:singleImageToLocalFile")
				.split(simple('${body.values()}'))
				.process(new Processor(){
					@Override
					void process(Exchange exchange) throws Exception {
						println "nameIndex:{} $nameIndex"
						List<String> imageFileNameList = (List<String>)exchange.properties['imageFileNameList']
						def imageName = imageFileNameList[nameIndex++]
						exchange.setProperty("imageName", imageName)
						println "imageName:{} $imageName"
					}
				})
				.process(new Processor(){
					@Override
					void process(Exchange exchange) throws Exception {
						def output = exchange.properties['imageOutputFolder']
						def imageName =	exchange.properties['imageName']
						String uri = "file:"+output+"?fileName="+imageName
						final ProducerTemplate producer = exchange.getContext().createProducerTemplate()
						producer.requestBody(uri, exchange.in.getBody())
					}
				})
				.end()

	}
}
