import java.util.List;

public class FixedSubject extends Subject {

	public int suggestAcademicYear;
	
	

	public FixedSubject(String subjectCode, String subjectName, int creditHours, String subjectType,
			List<Subject> prerequisiteSubjects, String responsibleDepartment, String description,
			int suggestAcademicYear) {
		super(subjectCode, subjectName, creditHours, subjectType, prerequisiteSubjects, responsibleDepartment,
				description);
		this.suggestAcademicYear = suggestAcademicYear;
	}
	
	
	public String toString() {
		return null;
	}

}
