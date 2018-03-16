package com.deccom.repository.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.deccom.domain.core.ControlVariable;

@Repository
public interface ControlVariableRepository extends MongoRepository<ControlVariable, String> {

	@Query("{}, { controlVarEntries: { $slice: ?0 } }")
	Page<ControlVariable> findAllLimitedNumberOfEntriesQuery(Pageable pageable, Integer numberOfEntries);

}
