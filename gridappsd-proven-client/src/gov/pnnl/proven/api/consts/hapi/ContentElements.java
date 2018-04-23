package gov.pnnl.proven.api.consts.hapi;

public enum ContentElements {
	   _KEY ("_KEY"),
	   STRING ("STRING"),
	   DATETIME ("DATETIME"),
	   INTEGER ("INTEGER"),
	   LONG("LONG"),
	   FLOAT ("FLOAT"),
	   DOUBLE ("DOUBLE"),
	   FILE ("FILE");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private ContentElements(String value) {
		   this.value = value;
	   }
	   
}