/*
 * Copyright (c) 1996, 1998, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package gov.pnnl.proven.api.exception;

/**
 * An <code>NullTermValueException</code> is an <code>SendMessageException</code> thrown during a
 * call to <code>ProvenanceProducer.SendMessage</code> if message contains term's with a null value.
 * All provenance message terms must have non-null values before they can be sent to a provenance
 * exchange.
 *
 * @see gov.pnnl.proven.api.HarvesterProducer
 */
@Deprecated
public class NullTermValueException extends SendMessageException {

	private static final long serialVersionUID = -4701893672873424227L;

	/**
	 * Constructs an <code>NullTermValueException</code> with no specified detail message.
	 */
	public NullTermValueException() {
	}

	/**
	 * Constructs an <code>NullTermValueException</code> with the specified detail message.
	 */
	public NullTermValueException(String s) {
		super(s);
	}
}
