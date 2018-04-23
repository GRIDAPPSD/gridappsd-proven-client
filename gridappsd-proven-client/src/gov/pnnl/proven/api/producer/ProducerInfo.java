package gov.pnnl.proven.api.producer;

/**
 * Information provided by client API user, describing their client environment. Contains
 * information that may be added to Provenance Messages.
 * 
 * @author raju332
 *
 */
@Deprecated
public class ProducerInfo {

	private String applicationName;
	private String applicationVersion;
	private String organization;
	private String project;
	private String timeZone;

	ProducerInfo(String applicationName, String applicationVersion) {
		this.applicationName = applicationName;
		this.applicationVersion = applicationVersion;
	}

	public ProducerInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

}