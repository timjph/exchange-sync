package com.zerodes.exchangesync;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Pair<L, R> {

	private final L left;
	private final R right;

	public Pair(final L left, final R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() {
		return left;
	}

	public R getRight() {
		return right;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(left)
			.append(right)
			.toHashCode();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object o) {
		if (o == null)
			return false;
		if (!(o instanceof Pair))
			return false;
		final Pair other = (Pair) o;
		return new EqualsBuilder()
			.append(left, other.left)
			.append(right, other.right)
			.isEquals();
	}

}