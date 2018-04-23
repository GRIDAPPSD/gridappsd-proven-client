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
 * An <code>ContextFileNotFoundException</code> is an <code>Exception</code>
 * thrown during a call to <code>ProvenanceProducer.Registry.getInstance</code> if
 * the default context file cannot be found.
 *
 * @see gov.pnnl.proven.api.HarvesterProducer
 */
public class ContextFileNotFoundException extends RuntimeException {

	
	private static final long serialVersionUID = 7371266823496639415L;

	/**
     * Constructs an <code>ContextFileNotFoundException</code> with no specified
     * detail message.
     */
    public ContextFileNotFoundException() {}

    /**
     * Constructs an <code>ContextFileNotFoundException</code> with the specified
     * detail message.
     */
    public ContextFileNotFoundException(String s)
    {
        super(s);
    }
}
