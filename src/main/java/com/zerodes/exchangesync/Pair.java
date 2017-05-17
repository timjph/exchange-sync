package com.zerodes.exchangesync;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Holds a pair of values.
 *
 * @param <L> type of the left parameter
 * @param <R> type of the right parameter
 */
public class Pair<L, R> {

	private final L left;
	private final R right;

	/**
	 * Constructor for instantiating a Pair.
	 *
	 * @param left the left object
	 * @param right the right object
	 */
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
		if (o == null) {
			return false;
		}
		if (!(o instanceof Pair)) {
			return false;
		}
		final Pair other = (Pair) o;
		return new EqualsBuilder()
			.append(left, other.left)
			.append(right, other.right)
			.isEquals();
	}

}
