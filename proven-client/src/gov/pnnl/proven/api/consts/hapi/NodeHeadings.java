package gov.pnnl.proven.api.consts.hapi;


public enum NodeHeadings {
       NODE ("node"),
	   NAME ("name"), 
	   NAMESPACE ("namespace");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private NodeHeadings(String value) {
		   this.value = value;
	   }
	   
}
