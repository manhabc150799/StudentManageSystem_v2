public class EnrolledFixedSubject extends EnrolledSubject {

	public FixedSubject subject;

	public String academicYear;

	public double examScore;

	public String status;
	
	

	public EnrolledFixedSubject(ClassSection classSection, int studentId, FixedSubject subject, String academicYear,
			double examScore, String status) {
		super(classSection, studentId);
		this.subject = subject;
		this.academicYear = academicYear;
		this.examScore = examScore;
		this.status = status;
	}
	
	public boolean isPassed() {
		return examScore > 5;
	}

	public String toString() {
		return "EnrolledFixedSubject{" +
	            "subject=" + subject.subjectName +
	            ", academicYear='" + academicYear + '\'' +
	            ", examScore=" + examScore +
	            ", status='" + (isPassed() ? "Passed" : "Failed") + '\'' +
	            '}';
	}
	
}
