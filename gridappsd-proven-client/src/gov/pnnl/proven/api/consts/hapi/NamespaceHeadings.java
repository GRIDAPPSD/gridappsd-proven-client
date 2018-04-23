package gov.pnnl.proven.api.consts.hapi;


public enum NamespaceHeadings {

	   PREFIX ("prefix"),
	   NAME ("name");   
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private NamespaceHeadings(String value) {
		   this.value = value;
	   }
	   
}
