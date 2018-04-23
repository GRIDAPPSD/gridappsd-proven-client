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
 * An <code>InvalidHapiHeadingValueException</code> is an <code>SendMessageException</code> thrown if message contains term's with a null value.
 * when a unrecognized header is provided in a HAPI delimited file..
 *
 * @see gov.pnnl.proven.api.HarvesterProducer
 */
public class InvalidHapiHeadingValueException extends Exception{

	private static final long serialVersionUID = -4701893672873424227L;

	/**
	 * Constructs an <code>NullTermValueException</code> with no specified detail message.
	 */
	public InvalidHapiHeadingValueException() {
	}

	/**
	 * Constructs an <code>NullTermValueException</code> with the specified detail message.
	 */
	public InvalidHapiHeadingValueException(String s) {
		super(s);
	}
}
