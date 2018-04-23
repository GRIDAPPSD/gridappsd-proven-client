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
 * An <code>CreateMessageException</code> is an <code>Exception</code>
 * thrown during a call to <code>ProvenanceProducer.CreateMessage</code> if
 * message creation failed, usually due to an invalid message name.
 *
 * @see gov.pnnl.proven.api.HarvesterProducer
 */
public class CreateMessageException extends Exception {
	
	private static final long serialVersionUID = -4701893672873424227L;

	/**
     * Constructs an <code>CreateMessageException</code> with no specified
     * detail message.
     */
    public CreateMessageException() {}

    /**
     * Constructs an <code>CreateMessageException</code> with the specified
     * detail message.
     */
    public CreateMessageException(String s)
    {
        super(s);
    }
}
