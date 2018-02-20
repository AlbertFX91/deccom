package com.deccom.web.rest.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.deccom.domain.core.extractor.ControlVariableExtractor;
import com.deccom.domain.core.extractor.Item_ControlVariableExtractor;
import com.deccom.service.core.ControlVariableExtractorService;
import com.deccom.service.core.util.ControlVariableServiceException;
import com.deccom.web.rest.util.HeaderUtil;

@RestController
@RequestMapping("/api")
public class ControlVariableExtractorResource {

	private final Logger log = LoggerFactory.getLogger(ControlVariableExtractorResource.class);

	private static final String ENTITY_NAME = "ControlVariableExtractor";

	@Autowired
	private ControlVariableExtractorService controlVariableExtractorService;

	/**
	 * Get all the extractors.
	 *
	 * @return the list of core_controlvars
	 */
	@GetMapping("/extractor")
	@Timed
	public List<Item_ControlVariableExtractor> findAll() {
		log.debug("Request to get all ControlVariableExtractor");
		return controlVariableExtractorService.getAllListExtractors();
	}

	@ExceptionHandler(ControlVariableServiceException.class)
	public ResponseEntity<String> panic(ControlVariableServiceException oops) {
		return ResponseEntity
				.badRequest()
				.headers(
						HeaderUtil.createFailureAlert(oops.getEntity(),
								oops.getI18nCode(), oops.getMessage()))
				.body("{ \"error\": \"" + oops.getMessage() + "\" }");
	}

}