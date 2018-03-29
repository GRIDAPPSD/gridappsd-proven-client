package gov.pnnl.proven.api.consts.hapi;


public enum MetricHeadings {

	   ISMETADATA ("ismeta"),
	   METRIC ("metric");   

	   private final String value;
	   
	   public String getValue() {
		   return value;
	   }
	   private MetricHeadings(String value) {
		   this.value = value;
	   }
	   
}
