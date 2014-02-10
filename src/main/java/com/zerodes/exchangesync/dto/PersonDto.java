package com.zerodes.exchangesync.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PersonDto {
	private String name;
	private String email;
	private boolean optional;
	
	public void setName(final String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setEmail(final String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setOptional(final boolean optional) {
		this.optional = optional;
	}
	public boolean isOptional() {
		return optional;
	}
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(name)
			.append(email)
			.append(optional)
			.toHashCode();
	}
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PersonDto)) {
			return false;
		}
		final PersonDto other = (PersonDto) obj;
		return new EqualsBuilder()
			.append(name, other.name)
			.append(email, other.email)
			.append(optional, other.optional)
			.isEquals();
	}
}
