package gov.pnnl.proven.api.producer;

import java.io.Serializable;

public class ProvenResponse implements Serializable {
	
	private static final long serialVersionUID = -33185896261738761L;

	public Serializable data;
	
	public Serializable error;
	
	public boolean responsecomplete;
	

}
