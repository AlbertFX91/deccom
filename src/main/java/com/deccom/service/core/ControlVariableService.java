package com.deccom.service.core;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.deccom.domain.core.ControlVariable;
import com.deccom.domain.core.ControlVariableEntry;
import com.deccom.domain.core.Status;
import com.deccom.domain.core.extractor.ControlVariableExtractor;
import com.deccom.domain.core.wrapper.New_ControlVariable;
import com.deccom.repository.core.ControlVariableRepository;
import com.deccom.service.core.util.ControlVariableServiceException;

@Service
public class ControlVariableService {

	private final Logger log = LoggerFactory.getLogger(ControlVariableService.class);
	private static final String i18nCodeRoot = "operations.controlvariable";

	@Autowired
	private ControlVariableRepository controlVariableRepository;

	@Autowired
	private ControlVariableSchedulingService schedulingService;

	/**
	 * Create a ControlVariable.
	 *
	 * @return the entity
	 */
	public ControlVariable create() {
		ControlVariable result = new ControlVariable();

		result.setCreationMoment(LocalDateTime.now());
		result.setStatus(Status.RUNNING);
		result.setControlVarEntries(new ArrayList<>());

		return result;
	}

	public ControlVariable save(ControlVariable controlVariable) {

		try {

			Integer value;

			value = controlVariable.getExtractor().getData();

			ControlVariable cv = controlVariableRepository.save(controlVariable);

			if (cv.getStatus().equals(Status.RUNNING) && !schedulingService.isRunning(cv))
				schedulingService.newJob(cv);

			return cv;

		} catch (NumberFormatException e) {
			throwException("Data must be an integer number", "nointeger", e);
			return null;
		}

	}

	public ControlVariable findOne(String id) {
		log.debug("Request to get ControlVariable : {}", id);
		return controlVariableRepository.findOne(id);
	}

	public Page<ControlVariable> findAll(Pageable pageable) {
		log.debug("Request to get all Core_Connection");
		return controlVariableRepository.findAll(pageable);
	}

	public List<ControlVariable> findAll() {
		log.debug("Request to get all Core_Connection");
		return controlVariableRepository.findAll();
	}

	public void testLaunchCVS() {
		for (ControlVariable cv : findAll()) {
			System.out.println(cv.getName() + ": " + cv.getExtractor().getData());
		}
	}

	public void run() {
		log.debug("Request to run all Core_Connection");
		List<ControlVariable> controlVars = controlVariableRepository.findAll();
		for (ControlVariable cv : controlVars) {
			executeMonitorize(cv);
		}
	}

	public void executeMonitorize(ControlVariable controlVar) {
		if (controlVar.getStatus() == Status.RUNNING) {
			Integer value;
			ControlVariableExtractor extractor;
			extractor = controlVar.getExtractor();
			try {
				value = extractor.getData();
				addEntry(controlVar, value);
				log.debug("Data extracted [" + controlVar.getName() + "]: " + value);
			} catch (Throwable e) {
				System.out.println("EXTRACTOR ERROR");
				controlVar.setStatus(Status.BLOCKED);
				controlVariableRepository.save(controlVar);
				schedulingService.stopJob(controlVar);
			}
		}
	}

	public ControlVariableEntry addEntry(ControlVariable cv, Integer value) {
		ControlVariableEntry entry = new ControlVariableEntry();
		entry.setCreationMoment(LocalDateTime.now());
		entry.setValue(value);
		cv.getControlVarEntries().add(entry);
		controlVariableRepository.save(cv);
		return entry;
	}

	public ControlVariable pause(String controlVarId) {
		ControlVariable controlVar;

		controlVar = findOne(controlVarId);

		if (controlVar.getStatus().equals(Status.RUNNING)) {
			controlVar.setStatus(Status.PAUSED);
			schedulingService.stopJob(controlVar);
		}

		return controlVariableRepository.save(controlVar);
	}

	public ControlVariable restart(String controlVarId) {
		ControlVariable controlVar;

		controlVar = findOne(controlVarId);

		if (controlVar.getStatus().equals(Status.PAUSED)) {
			controlVar.setStatus(Status.RUNNING);
			schedulingService.newJob(controlVar);
		}

		return controlVariableRepository.save(controlVar);
	}
	
	public ControlVariable convert(New_ControlVariable ncv) {
		ControlVariable res;
		ControlVariableExtractor extractor;

		// 1. Recover Control variable
		res = ncv.getControlVariable();
		// 2. InstanciateExtractor
		extractor = getExtractor(ncv);
		// 3. Inject data in extractor
		injectData(ncv.getExtractorData(), extractor);
		// 4. Check all extractors fields are added
		checkFieldsNotNull(extractor);
		
		res.setExtractor(extractor);
		return res;
	}
	

	
	private ControlVariableExtractor getExtractor(New_ControlVariable ncv) {
		ControlVariableExtractor res = null;
		String extractor = ncv.getExtractorClass();
		
		try {
			Object cls = Class.forName(extractor).newInstance();
			if (cls instanceof ControlVariableExtractor) {
				res = (ControlVariableExtractor) cls;
			}else {
				throwException("the class '"+ncv.getExtractorClass()+"' is not an extrator", "extractorclassnotinstance");
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throwException("the class '"+ncv.getExtractorClass()+"' cannot be instanciated", "extractorclasserror", e);
		} catch (ClassNotFoundException e) {
			throwException("the class '"+ncv.getExtractorClass()+"' cannot be founded", "extractorclassnotfound", e);
		}
		return res;
	}
	
	private void checkFieldsNotNull(ControlVariableExtractor ncv){
		List<String> missingFields = new ArrayList<>();
		for(Field f: ncv.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if(f.get(ncv) == null) {
					missingFields.add(f.getName());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throwException("Error checking fields in extractor", "extractorclassfields");
			}
		}
		if(!missingFields.isEmpty()) {
			throwException("The fields " + missingFields + " are null", "extractorclassfieldsnull");
		}
	}

	private <T> void injectData(Map<String, T> extractorData, ControlVariableExtractor extractor) {
		Class<?> clazz = extractor.getClass();
		for (Entry<String, T> entry: extractorData.entrySet()) {
			String f = entry.getKey();
			T v = entry.getValue();
			try {
				Field field = clazz.getDeclaredField(f);
				field.setAccessible(true);
				field.set(extractor, v);
			} catch (Exception  e) {
				throwException("Error injectin data into extractor " + extractor.getClass().getName(), "extractorclassinjectionerror");
			}
		}
	}
	
	private void throwException(String msg, String i18Code, Exception e) {
		throw new ControlVariableServiceException(msg, i18nCodeRoot + "." + i18Code,
				"ControlVariableService", e);
	}
	
	private void throwException(String msg, String i18Code) {
		throw new ControlVariableServiceException(msg, i18nCodeRoot + "." + i18Code,
				"ControlVariableService");
	}
}