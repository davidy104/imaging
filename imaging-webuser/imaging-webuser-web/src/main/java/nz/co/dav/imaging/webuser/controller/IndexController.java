package nz.co.dav.imaging.webuser.controller;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Resource;

import nz.co.dav.imaging.webuser.SearchTerm;
import nz.co.dav.imaging.webuser.ds.ImagingDS;
import nz.co.dav.imaging.webuser.model.ImageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/")
public class IndexController extends BaseController {
	public static final String INDEX_VIEW = "index";
	public static final String MODEL_ATTRIBUTE_IMAG_INFOS = "imageInfos";

	@Resource
	private ImagingDS imagingDs;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(IndexController.class);

	@RequestMapping(method = RequestMethod.GET)
	public String showIndexPage(Model model) {
		LOGGER.info("showIndexPage start:{}");
		model.addAttribute("searchInfo", new SearchTerm());
		return INDEX_VIEW;
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@ModelAttribute("searchInfo") SearchTerm searchTerm,
			Model model) throws Exception {
		LOGGER.info("search start:{}");
		Set<ImageInfo> imageInfoSet = Collections.<ImageInfo> emptySet();
		imageInfoSet = imagingDs.getImagingUrisByTag(searchTerm.getTag(), searchTerm.getScalingType());
		model.addAttribute(MODEL_ATTRIBUTE_IMAG_INFOS, imageInfoSet);
		return INDEX_VIEW;
	}

}
