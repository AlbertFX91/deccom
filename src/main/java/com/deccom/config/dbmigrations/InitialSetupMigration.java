package com.deccom.config.dbmigrations;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.deccom.domain.Authority;
import com.deccom.domain.User;
import com.deccom.domain.core.ControlVariable;
import com.deccom.domain.core.Event;
import com.deccom.domain.core.Status;
import com.deccom.domain.core.extractor.rest.facebook.FacebookFansExtractor;
import com.deccom.domain.core.extractor.rest.twitter.TwitterFollowersExtractor;
import com.deccom.security.AuthoritiesConstants;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.google.common.collect.Lists;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

	@ChangeSet(order = "01", author = "initiator", id = "01-addAuthorities")
	public void addAuthorities(MongoTemplate mongoTemplate) {
		Authority adminAuthority = new Authority();
		adminAuthority.setName(AuthoritiesConstants.ADMIN);
		Authority userAuthority = new Authority();
		userAuthority.setName(AuthoritiesConstants.USER);
		mongoTemplate.save(adminAuthority);
		mongoTemplate.save(userAuthority);
	}

	@ChangeSet(order = "02", author = "initiator", id = "02-addUsers")
	public void addUsers(MongoTemplate mongoTemplate) {
		Authority adminAuthority = new Authority();
		adminAuthority.setName(AuthoritiesConstants.ADMIN);
		Authority userAuthority = new Authority();
		userAuthority.setName(AuthoritiesConstants.USER);

		User systemUser = new User();
		systemUser.setId("user-0");
		systemUser.setLogin("system");
		systemUser.setPassword("$2a$10$mE.qmcV0mFU5NcKh73TZx.z4ueI/.bDWbj0T1BYyqP481kGGarKLG");
		systemUser.setFirstName("");
		systemUser.setLastName("System");
		systemUser.setEmail("system@localhost");
		systemUser.setActivated(true);
		systemUser.setLangKey("en");
		systemUser.setCreatedBy(systemUser.getLogin());
		systemUser.setCreatedDate(Instant.now());
		systemUser.getAuthorities().add(adminAuthority);
		systemUser.getAuthorities().add(userAuthority);
		mongoTemplate.save(systemUser);

		User anonymousUser = new User();
		anonymousUser.setId("user-1");
		anonymousUser.setLogin("anonymoususer");
		anonymousUser.setPassword("$2a$10$j8S5d7Sr7.8VTOYNviDPOeWX8KcYILUVJBsYV83Y5NtECayypx9lO");
		anonymousUser.setFirstName("Anonymous");
		anonymousUser.setLastName("User");
		anonymousUser.setEmail("anonymous@localhost");
		anonymousUser.setActivated(true);
		anonymousUser.setLangKey("en");
		anonymousUser.setCreatedBy(systemUser.getLogin());
		anonymousUser.setCreatedDate(Instant.now());
		mongoTemplate.save(anonymousUser);

		User adminUser = new User();
		adminUser.setId("user-2");
		adminUser.setLogin("admin");
		adminUser.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
		adminUser.setFirstName("admin");
		adminUser.setLastName("Administrator");
		adminUser.setEmail("admin@localhost");
		adminUser.setActivated(true);
		adminUser.setLangKey("en");
		adminUser.setCreatedBy(systemUser.getLogin());
		adminUser.setCreatedDate(Instant.now());
		adminUser.getAuthorities().add(adminAuthority);
		adminUser.getAuthorities().add(userAuthority);
		mongoTemplate.save(adminUser);

		User userUser = new User();
		userUser.setId("user-3");
		userUser.setLogin("user");
		userUser.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
		userUser.setFirstName("");
		userUser.setLastName("User");
		userUser.setEmail("user@localhost");
		userUser.setActivated(true);
		userUser.setLangKey("en");
		userUser.setCreatedBy(systemUser.getLogin());
		userUser.setCreatedDate(Instant.now());
		userUser.getAuthorities().add(userAuthority);
		mongoTemplate.save(userUser);
	}

	/*
	@ChangeSet(order = "03", author = "initiator", id = "03-addAcmes")
	public void addAcmes(MongoTemplate mongoTemplate) {
		Integer numAcmes = 40;
		IntStream.range(0, numAcmes).forEach(index -> {
			Acme acme = new Acme();
			acme.setId("acme-" + index);
			acme.setTitle("Title acme - " + index);
			acme.setDescription("Description acme - " + index);
			acme.setPublication_date(LocalDate.now());
			acme.setRating(index % 10);
			mongoTemplate.save(acme);
		});

	}
	*/

	/*
	@ChangeSet(order = "05", author = "initiator", id = "05-addRESTControlVars")
	public void addRESTControlVars(MongoTemplate mongoTemplate) {
		RESTControlVar r = new RESTControlVar();
		r.setId("restControlVar-1");
		r.setCreationMoment(LocalDateTime.now());
		r.setName("id1");
		r.setQuery("$.[0].id");
		r.setFrequency_sec(30);
		r.setRestConnection(new RESTConnection("https://jsonplaceholder.typicode.com/photos"));
		r.setRestControlVarEntries(Lists.newArrayList(new RESTControlVarEntry("value-1", LocalDateTime.now()),
				new RESTControlVarEntry("value-2", LocalDateTime.now())));

		mongoTemplate.save(r);

		r = new RESTControlVar();
		r.setId("restControlVar-2");
		r.setCreationMoment(LocalDateTime.now());
		r.setName("id2");
		r.setQuery("$.[5].id");
		r.setFrequency_sec(60);
		r.setRestConnection(new RESTConnection("https://jsonplaceholder.typicode.com/photos"));
		r.setRestControlVarEntries(Lists.newArrayList(new RESTControlVarEntry("15", LocalDateTime.now()),
				new RESTControlVarEntry("17", LocalDateTime.now())));

		mongoTemplate.save(r);
	}
	*/

	/*
	@ChangeSet(order = "06", author = "initiator", id = "06-addCoreControlVars")
	public void addCoreControlVars(MongoTemplate mongoTemplate) {
		RESTExtractor rest1 = new RESTExtractor();
		rest1.setJsonPath("$[2].id");
		rest1.setUrl("http://jsonplaceholder.typicode.com/users");

		RESTExtractor rest2 = new RESTExtractor();
		rest2.setJsonPath("$[2].id");
		rest2.setUrl("http://jsonplaceholder.typicode.com/users");

		MySQLExtractor sql3 = new MySQLExtractor();
		sql3.setUsername("developer");
		sql3.setPassword("developer");
		sql3.setQuery("select age from author where idauthor='1' and name='name-1';");
		sql3.setUrl("localhost:3306/deccom");

		SQLExtractor sql4 = new SQLExtractor();
		sql4.setUsername("developer");
		sql4.setPassword("developer");
		sql4.setQuery("select name from author where idauthor='1' and name='name-1';");
		sql4.setUrl("localhost:3306/deccom");
		sql4.setJdbc("mysql");

		ControlVariable c1 = new ControlVariable();
		c1.setExtractor(rest1);
		c1.setCreationMoment(LocalDateTime.now());
		c1.setStatus(Status.RUNNING);
		c1.setFrequency("0/" + 10 + " * * * * *");
		c1.setName("RESTControlVar1");
		c1.setControlVarEntries(Lists.newArrayList());

		ControlVariable c2 = new ControlVariable();
		c2.setExtractor(rest2);
		c2.setCreationMoment(LocalDateTime.now());
		c2.setStatus(Status.BLOCKED);
		c2.setFrequency("0/" + 10 + " * * * * *");
		c2.setName("RESTControlVar2");
		c2.setControlVarEntries(Lists.newArrayList());

		ControlVariable c3 = new ControlVariable();
		c3.setExtractor(sql3);
		c3.setCreationMoment(LocalDateTime.now());
		c3.setStatus(Status.RUNNING);
		c3.setFrequency("0/" + 10 + " * * * * *");
		c3.setName("MySQLControlVar");
		c3.setControlVarEntries(Lists.newArrayList());

		ControlVariable c4 = new ControlVariable();
		c4.setExtractor(sql4);
		c4.setCreationMoment(LocalDateTime.now());
		c4.setStatus(Status.RUNNING);
		c4.setFrequency("0/" + 10 + " * * * * *");
		c4.setName("SQLControlVar");
		c4.setControlVarEntries(Lists.newArrayList());

		mongoTemplate.save(c1);
		mongoTemplate.save(c2);
		mongoTemplate.save(c3);
		mongoTemplate.save(c4);

	}
	*/

	/*
	@ChangeSet(order = "07", author = "initiator", id = "07-addFacebookFansControlVars")
	public void addFacebookFansControlVars(MongoTemplate mongoTemplate) {

		FacebookFansExtractor facebookFansExtractor;

		facebookFansExtractor = new FacebookFansExtractor();

		facebookFansExtractor.setFacebookPageURL("https://www.facebook.com/Deccom-546664955726052");

		ControlVariable controlVariable = new ControlVariable();
		controlVariable.setExtractor(facebookFansExtractor);
		controlVariable.setCreationMoment(LocalDateTime.now());
		controlVariable.setStatus(Status.RUNNING);
		controlVariable.setFrequency("0/" + 10 + " * * * * *");
		controlVariable.setName("FBFansControlVar");
		controlVariable.setControlVarEntries(Lists.newArrayList());

		mongoTemplate.save(controlVariable);

	}
	*/
	/*
	@ChangeSet(order = "08", author = "initiator", id = "08-addEvents")
	public void addEvents(MongoTemplate mongoTemplate) {

		Event event1, event2;
		String event1Name, event2Name;
		LocalDateTime event1CreationMoment, event2CreationMoment;
		LocalDate event1StartingDate, event2StartingDate, event2EndingDate;

		event1 = new Event();
		event2 = new Event();
		event1Name = "event1Name";
		event2Name = "event2Name";
		event1CreationMoment = LocalDateTime.now();
		event2CreationMoment = LocalDateTime.now();
		event1StartingDate = LocalDate.of(2018, 12, 18);
		event2StartingDate = LocalDate.of(2018, 03, 18);
		event2EndingDate = LocalDate.of(2018, 05, 01);

		event1.setName(event1Name);
		event2.setName(event2Name);
		event1.setCreationMoment(event1CreationMoment);
		event2.setCreationMoment(event2CreationMoment);
		event1.setStartingDate(event1StartingDate);
		event2.setStartingDate(event2StartingDate);
		event2.setEndingDate(event2EndingDate);

		mongoTemplate.save(event1);
		mongoTemplate.save(event2);

	}
	*/
	
	@ChangeSet(order = "07", author = "initiator", id = "09-addPoliticiansTwitterAccounts")
	public void addPoliticiansTwitterAccounts(MongoTemplate mongoTemplate) {

		ControlVariable rajoyCV = createTwitterFollowersCV("marianorajoy", "Mariano Rajoy followers");
		ControlVariable sanchezCV = createTwitterFollowersCV("sanchezcastejon", "Pedro Sánchez followers");
		ControlVariable riveraCV = createTwitterFollowersCV("Albert_Rivera", "Albert Rivera followers");
		ControlVariable iglesiasCV = createTwitterFollowersCV("Pablo_Iglesias_", "Pablo Iglesias followers");
		
		mongoTemplate.save(rajoyCV);
		mongoTemplate.save(sanchezCV);
		mongoTemplate.save(riveraCV);
		mongoTemplate.save(iglesiasCV);

	}
	
	@ChangeSet(order = "08", author = "initiator", id = "10-addPoliticalPartyFacebookAccounts")
	public void addPoliticalPartyFacebookAccounts(MongoTemplate mongoTemplate) {

		ControlVariable ppCV = createFacebookFansCV("pp", "PP fans");
		ControlVariable psoeCV = createFacebookFansCV("psoe", "PSOE fans");
		ControlVariable csCV = createFacebookFansCV("Cs.Ciudadanos", "Ciudadanos fans");
		ControlVariable pCV = createFacebookFansCV("ahorapodemos", "PODEMOS fans");
		
		mongoTemplate.save(ppCV);
		mongoTemplate.save(psoeCV);
		mongoTemplate.save(csCV);
		mongoTemplate.save(pCV);

	}
	
	public ControlVariable createTwitterFollowersCV(String username, String cvName) {
		TwitterFollowersExtractor extractor = new TwitterFollowersExtractor();
		ControlVariable res = new ControlVariable();

		extractor.setUsername(username);
		
		res.setExtractor(extractor);
		res.setCreationMoment(LocalDateTime.now());
		res.setStatus(Status.RUNNING);
		res.setFrequency("0/"+30+" * * * * *");
		res.setName(cvName);
		res.setControlVarEntries(Lists.newArrayList());
		
		return res;
	}
	
	public ControlVariable createFacebookFansCV(String page, String cvName) {
		FacebookFansExtractor extractor = new FacebookFansExtractor();
		ControlVariable res = new ControlVariable();

		String url = "https://www.facebook.com/"+page+"/";
		
		extractor.setFacebookPageURL(url);
		
		res.setExtractor(extractor);
		res.setCreationMoment(LocalDateTime.now());
		res.setStatus(Status.RUNNING);
		res.setFrequency("0/"+60+" * * * * *");
		res.setName(cvName);
		res.setControlVarEntries(Lists.newArrayList());
		
		return res;
	}

}
