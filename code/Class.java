package playhub;

public class Class {

	private String classId;
	private String name;
	private String sportType;
	private Session associatedSession;

	public Class() {
	}

	public Class(String classId, String name, String sportType) {
		this.classId = classId;
		this.name = name;
		this.sportType = sportType;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSportType() {
		return sportType;
	}

	public void setSportType(String sportType) {
		this.sportType = sportType;
	}

	public Session getAssociatedSession() {
		return associatedSession;
	}

	public void setAssociatedSession(Session associatedSession) {
		this.associatedSession = associatedSession;
	}

}
