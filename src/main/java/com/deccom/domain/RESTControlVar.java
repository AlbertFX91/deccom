package com.deccom.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A RESTControlVar.
 */
@Document(collection = "restControlVar")
public class RESTControlVar implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private String id;

	@NotNull
	@Field("name")
	private String name;

	@NotNull
	@Field("query")
	private String query;

	@NotNull
	@Field("creationMoment")
	private LocalDateTime creationMoment;


	@NotNull
	@Field("frequency_sec")
	private Integer frequency_sec;
	
	@NotNull
	@Field("restConnection")
	private RESTConnection restConnection;

	@NotNull
	@Field("restControlVarEntries")
	private List<RESTControlVarEntry> restControlVarEntries;

	public RESTControlVar(String name, String query,
			LocalDateTime creationMoment, Integer frequency_sec,
			RESTConnection restConnection, List<RESTControlVarEntry> restControlVarEntries) {

		super();
		this.name = name;
		this.query = query;
		this.creationMoment = creationMoment;
		this.frequency_sec = frequency_sec;
		this.restConnection = restConnection;
		this.restControlVarEntries = restControlVarEntries;

	}

	public RESTControlVar() {

		super();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public LocalDateTime getCreationMoment() {
		return creationMoment;
	}

	public void setCreationMoment(LocalDateTime creationMoment) {
		this.creationMoment = creationMoment;
	}
	
	public Integer getFrequency_sec() {
		return frequency_sec;
	}

	public void setFrequency_sec(Integer frequency_sec) {
		this.frequency_sec = frequency_sec;
	}

	public RESTConnection getRestConnection() {
		return restConnection;
	}

	public void setRestConnection(RESTConnection restConnection) {
		this.restConnection = restConnection;
	}

	public List<RESTControlVarEntry> getRestControlVarEntries() {
		return restControlVarEntries;
	}

	public void setRestControlVarEntries(
			List<RESTControlVarEntry> restControlVarEntries) {
		this.restControlVarEntries = restControlVarEntries;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RESTControlVar other = (RESTControlVar) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getId());
	}

	@Override
	public String toString() {
		return "RESTControlVar [id=" + id + ", name=" + name + ", query="
				+ query + ", creationMoment=" + creationMoment
				+ ", frequency_sec=" + frequency_sec
				+ ", restConnection=" + restConnection + "]";
	}

}
