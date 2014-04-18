package com.hascode.tutorial.jpa_caching.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cacheable(false)
public class Person implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private String name;

	public Person() {
	}

	public Person(final Long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public final Long getId() {
		return id;
	}

	public final void setId(final Long id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(final String name) {
		this.name = name;
	}
}
