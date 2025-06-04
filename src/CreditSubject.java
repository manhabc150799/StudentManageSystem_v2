import java.util.List;

public class CreditSubject extends Subject {

	public int suggestSemester;

	public int midtermWeight;

	public int finalExamWeight;
	
	public CreditSubject(String subjectCode, String subjectName, int creditHours, String subjectType,
			List<Subject> prerequisiteSubjects, String responsibleDepartment, String description, int suggestSemester,
			int midtermWeight, int finalExamWeight) {
		super(subjectCode, subjectName, creditHours, subjectType, prerequisiteSubjects, responsibleDepartment,
				description);
		this.suggestSemester = suggestSemester;
		this.midtermWeight = midtermWeight;
		this.finalExamWeight = finalExamWeight;
	}

	public String toString() {
		return "CreditSubject{" +
	            "subjectCode='" + subjectCode + '\'' +
	            ", subjectName='" + subjectName + '\'' +
	            ", creditHours=" + creditHours +
	            ", subjectType='" + subjectType + '\'' +
	            ", suggestSemester=" + suggestSemester +
	            ", midtermWeight=" + midtermWeight +
	            ", finalExamWeight=" + finalExamWeight +
	            '}';
	}
	
}
