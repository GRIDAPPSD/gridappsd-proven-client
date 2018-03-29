package gov.pnnl.proven.api.consts.hapi;


public enum GeneralSyntax {

	   DELIMITER (",");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private GeneralSyntax(String value) {
		   this.value = value;
	   }
	   
}
