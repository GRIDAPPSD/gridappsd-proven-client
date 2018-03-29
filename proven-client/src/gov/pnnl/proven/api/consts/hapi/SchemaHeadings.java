package gov.pnnl.proven.api.consts.hapi;


public enum SchemaHeadings {

	   FIELD ("field"), 
	   DATATYPE ("dataType"),
	   CONSTRAINT ("constraint"),
	   FORMAT ("format"),
	   _PARAMETERS ("_PARAMETERS"),
	   _TABLE ("_TABLE");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private SchemaHeadings(String value) {
		   this.value = value;
	   }
	   
}
