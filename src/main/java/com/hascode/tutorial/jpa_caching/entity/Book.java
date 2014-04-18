package com.hascode.tutorial.jpa_caching.entity;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Cacheable(true)
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String title;

	public Book() {
	}

	public Book(final Long id, final String title) {
		this.id = id;
		this.title = title;
	}

	public final Long getId() {
		return id;
	}

	public final void setId(final Long id) {
		this.id = id;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(final String title) {
		this.title = title;
	}
}
