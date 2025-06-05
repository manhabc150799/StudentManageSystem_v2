import java.util.ArrayList;
import java.util.List;

public abstract class Student extends User {
	
	protected String studentId;

	protected String major;

	protected List<Subject> curriculum;

	protected List<Subject> completedSubjects;

	protected List<Schedule> schedules;

	/** List of class section IDs the student has enrolled in */
	protected List<String> enrolledClassIds;


	public Student(String userId, String email, String password, String fullName, String role, boolean status, String dob,
				   String studentId, String major) {
		super(userId, email, password, fullName, role, status, dob);
		this.studentId = studentId;
		this.major = major;
		this.completedSubjects = new ArrayList<Subject>();
		this.schedules = new ArrayList<Schedule>();
		this.enrolledClassIds = new ArrayList<>();
	}

	/**
	 * Adds the given class section ID to the list of enrolled classes if not already present.
	 */
	public void addEnrolledClass(String classSectionId) {
		if (!enrolledClassIds.contains(classSectionId)) {
			enrolledClassIds.add(classSectionId);
		}
	}

	/**
	 * Removes the given class section ID from the student's enrollment list
	 * if present.
	 */
	public void removeEnrolledClass(String classSectionId) {
		enrolledClassIds.remove(classSectionId);
	}



	/**
	 * Adds the given class section ID to the list of enrolled classes if not already present.
	 */


	public List<String> getEnrolledClassIds() {
		return enrolledClassIds;
	}


	public abstract void viewResult();

	public abstract double calculateCPA();

	public abstract boolean checkGraduationRequirements();

	public abstract void viewTimeTable();

	public abstract boolean enrollClassSection();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Student other = (Student) obj;
		return studentId != null && studentId.equals(other.studentId);
	}

	@Override
	public int hashCode() {
		return studentId != null ? studentId.hashCode() : 0;
	}
	

}
