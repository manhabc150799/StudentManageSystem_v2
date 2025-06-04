import java.util.List;

public abstract class Subject {

	protected String subjectCode;

	protected String subjectName;

	protected int creditHours;

	protected String subjectType;

	protected List<Subject> prerequisiteSubjects;

	protected String responsibleDepartment;

	protected String description;

	public Subject(String subjectCode, String subjectName, int creditHours, String subjectType,
			List<Subject> prerequisiteSubjects, String responsibleDepartment, String description) {
		this.subjectCode = subjectCode;
		this.subjectName = subjectName;
		this.creditHours = creditHours;
		this.subjectType = subjectType;
		this.prerequisiteSubjects = prerequisiteSubjects;
		this.responsibleDepartment = responsibleDepartment;
		this.description = description;
	}
	
}
