package gov.pnnl.proven.api.producer;

import java.io.Serializable;

import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;

public class ProvenResponse implements Serializable {
	
	private static final long serialVersionUID = -33185896261738761L;

	public Status status;

	public int code;
	
	public Serializable result;
	
	public Serializable error;
	
	
	@Override
	public String toString() {
		Gson  gson = new Gson();
		return gson.toJson(this);
	}
	

}