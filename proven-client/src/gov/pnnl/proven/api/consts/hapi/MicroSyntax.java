package gov.pnnl.proven.api.consts.hapi;

public enum MicroSyntax {
       KEY_TERM_INDICATOR ("_KEY"),
	   LINK_NODE ("_NODE"),
	   NAMESPACE_DELIMITER(":"),
	   MICRO_DELIMITER ("\\?"),
	   MICRO_ASSIGNMENT_OPERATOR ("=");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private MicroSyntax(String value) {
		   this.value = value;
	   }
	   
}