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
 * An <code>SendMessageException</code> is an <code>Exception</code>
 * thrown during a call to <code>ProvenanceProducer.SendMessage</code> if
 * message send failed.
 *
 * @see gov.pnnl.proven.api.HarvesterProducer
 */
public class SendMessageException extends Exception {
	
	private static final long serialVersionUID = -4701893672873424227L;

	/**
     * Constructs an <code>SendMessageException</code> with no specified
     * detail message.
     */
    public SendMessageException() {}

    /**
     * Constructs an <code>SendMessageException</code> with the specified
     * detail message.
     */
    public SendMessageException(String s)
    {
        super(s);
    }
}
