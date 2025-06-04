import java.util.ArrayList;
import java.util.List;

public abstract class Student extends User {
	
	protected String studentId;

	protected String major;

	protected List<Subject> curriculum;

	protected List<Subject> completedSubjects;

	protected List<Schedule> schedules;

	
	public Student(String userId, String email, String password, String fullName, String role, boolean status, String dob,
			String studentId, String major) {
		super(userId, email, password, fullName, role, status, dob);
		this.studentId = studentId;
		this.major = major;
		this.completedSubjects = new ArrayList<Subject>();
		this.schedules = new ArrayList<Schedule>();
	}


	public abstract void viewResult();

	public abstract double calculateCPA();

	public abstract boolean checkGraduationRequirements();

	public abstract void viewTimeTable();

	public abstract boolean enrollClassSection();
	

}
