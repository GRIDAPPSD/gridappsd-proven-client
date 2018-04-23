package gov.pnnl.proven.api.consts.hapi;


public enum MessageHeadings {

	   NAME ("NAME"),
	   DESCRIPTION ("DESCRIPTION"),
	   KEYWORDS ("KEYWORDS"),
	   METRICS ("METRICS"),
	   DOMAIN ("DOMAIN"),
	   PRODUCER ("PRODUCER"),
	   PRODUCER_VERSION ("PRODUCER_VERSION");
	   
	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private MessageHeadings(String value) {
		   this.value = value;
	   }
	   
}
