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

	@Override
	public void configure() {
		from("direct:imageBatchToLocalFile")
				.routeId("ImageBatchToLocalFileRoute")
				.split(body())
				.parallelProcessing().executorServiceRef("genericThreadPool")
				.to("direct:doImageBatchToLocalFile")
				.end()

		from("direct:doImageBatchToLocalFile")
				.process(new Processor(){
					@Override
					void process(Exchange exchange) throws Exception {
						int folderNumber = exchange.properties['folderNumber']?:0
						String imageOutputFolder = fileOutputPath+"/output"+folderNumber
						println "imageOutputFolder:{} $imageOutputFolder"
						exchange.setProperty("folderNumber", folderNumber++)
						exchange.setProperty("imageOutputFolder", imageOutputFolder)
						Map body = exchange.in.getBody(Map.class)
						exchange.setProperty("imageFileNameList", body.keySet().toList())
					}
				})
				.to("direct:singleImageToLocalFile")

		from("direct:singleImageToLocalFile")
				.split(simple('${body.values()}'))
				.parallelProcessing().executorServiceRef("genericThreadPool")
				.process(new Processor(){
					@Override
					void process(Exchange exchange) throws Exception {
						int index = exchange.properties['index']?:0
						List<String> imageFileNameList = (List<String>)exchange.properties['imageFileNameList']
						def imageName = imageFileNameList[index]
						exchange.setProperty("index", index++)
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
