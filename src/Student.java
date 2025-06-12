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
	/**
	 * Calculates the average CPA for all class sections this student has
	 * enrolled in using the grades stored in {@link ClassSection}.
	 *
	 * @return the average CPA on a 4.0 scale, or 0 if the student has not
	 *         enrolled in any class sections
	 */
	public double calculateAverageCPAFromClasses() {
		double total = 0.0;
		int count = 0;
		for (String id : enrolledClassIds) {
			ClassSection cs = Manager.classSections.stream()
					.filter(c -> c.classSectionId.equals(id))
					.findFirst()
					.orElse(null);
			if (cs != null) {
				total += cs.calculateCPA(studentId);
				count++;
			}
		}
		return count == 0 ? 0.0 : total / count;
	}

	/**
	 * Calculates the graduation progress of the student. Each passed class
	 * contributes three points towards the required 120. A class is
	 * considered passed when both the midterm and final scores are at least
	 * 3.0. Failed classes contribute zero points.
	 *
	 * @return total graduation points accumulated
	 */
	public int calculateGraduationProgress() {
		int passed = 0;
		for (String id : enrolledClassIds) {
			ClassSection cs = Manager.classSections.stream()
					.filter(c -> c.classSectionId.equals(id))
					.findFirst()
					.orElse(null);
			if (cs != null) {
				if (cs.getMidtermScore(studentId) >= 3.0f && cs.getFinalScore(studentId) >= 3.0f) {
					passed++;
				}
			}
		}
		return passed * 3;
	}

	/**
	 * Determines whether the student has accumulated enough passed classes
	 * to graduate. A total of 120 points (40 passed classes) is required.
	 *
	 * @return {@code true} if the student meets the requirement, otherwise
	 *         {@code false}
	 */
	public boolean validateGraduation() {
		return calculateGraduationProgress() >= 120;
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
