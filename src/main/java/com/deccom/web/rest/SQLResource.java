package com.deccom.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.deccom.domain.Author;
import com.deccom.domain.SQLControlVar;
import com.deccom.domain.SQLDataRecover;
import com.deccom.domain.SQLQuery;
import com.deccom.service.SQLControlVarService;
import com.deccom.service.SQLService;
import com.deccom.service.impl.util.SQLResponse;
import com.deccom.service.impl.util.SQLServiceException;
import com.deccom.web.rest.util.HeaderUtil;

/**
 * REST controller for managing Acme.
 */
@RestController
@RequestMapping("/api")
public class SQLResource {

	private final Logger log = LoggerFactory.getLogger(SQLResource.class);

	private final SQLService sqlService;
	private final SQLControlVarService sqlControlVarService;

	public SQLResource(SQLService sqlService, SQLControlVarService sqlControlVarService) {
		this.sqlService = sqlService;
		this.sqlControlVarService = sqlControlVarService;
	}

	@GetMapping("/sql/nomapping")
	@Timed
	public ResponseEntity<List<Map<String, String>>> callSQLNoMapping() {
		log.debug("REST request to test DB");
		List<Map<String, String>> res = sqlService.callNoMapping();
		return ResponseEntity.ok().body(res);
	}

	@GetMapping("/sql/mapping")
	@Timed
	public ResponseEntity<List<Author>> callSQLMapping() {
		log.debug("REST request to test DB");
		List<Author> res = sqlService.callMapping();
		return ResponseEntity.ok().body(res);
	}

	/**
	 * POST /db/query : make a connection and a query to a database
	 *
	 * @param query
	 *            the connection and query data
	 * @return the ResponseEntity with status 200 (OK) and the query's data
	 */
	@PostMapping("/sql/query")
	@Timed
	public ResponseEntity<SQLResponse> query(@Valid @RequestBody SQLQuery query) {
		log.debug("REST request to query a DB");
		SQLResponse res = sqlService.query(query);
		return ResponseEntity.ok().body(res);
	}

	/**
	 * GET /db/oracle/query : make a connection and a query to a oracle sql database
	 *
	 * @param query
	 *            the SQL sentence
	 * @return the ResponseEntity with status 200 (OK) and the query's data
	 */
	@GetMapping("/sql/oracle/query")
	@Timed
	public ResponseEntity<List<Map<String, String>>> query(@RequestParam String query) {
		log.debug("REST request to query a Oracle SQL DB");
		log.debug(query);
		List<Map<String, String>> res = sqlService.OracleSQLQuery(query);
		return ResponseEntity.ok().body(res);
	}
	
	/**
	 * POST /db/datarecover : create a new control var
	 *
	 * @param dataRecover
	 *            the data necessary to create a new data recover
	 * @return the ResponseEntity with status 200 (OK) and the controlvar's id
	 * @throws URISyntaxException 
	 */
	@PostMapping("/sql/datarecover")
	@Timed
	public ResponseEntity<SQLControlVar> sqlDataRecover(@Valid @RequestBody SQLDataRecover dataRecover) throws URISyntaxException {
		log.debug("REST request to create a control var for SQL");
		
		SQLControlVar aux = sqlControlVarService.create(dataRecover);
		SQLControlVar result = sqlControlVarService.save(aux);
		
		return ResponseEntity.created(new URI("/api/sql/datarecover" + result.getId()))
	            .headers(HeaderUtil.createEntityCreationAlert("sql", result.getId().toString()))
	            .body(result);
	}
	
	/**
	 * GET /db/oracle/query : make a connection and a query to a oracle sql database
	 *
	 * @param query
	 *            the SQL sentence
	 * @return the ResponseEntity with status 200 (OK) and the query's data
	 */
	@GetMapping("/sql/atomic")
	@Timed
	public ResponseEntity<String> atomic() {
		log.debug("REST request to atomic");
		sqlControlVarService.monitorize();
		return ResponseEntity.ok().body("OK");
	}

	@ExceptionHandler(SQLServiceException.class)
	public ResponseEntity<String> panic(SQLServiceException oops) {
		return ResponseEntity.badRequest()
				.headers(HeaderUtil.createFailureAlert(oops.getEntity(), oops.getI18nCode(), oops.getMessage()))
				.body(null);
	}
}