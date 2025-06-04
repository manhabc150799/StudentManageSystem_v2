import java.util.ArrayList;
import java.util.List;

public class YearBasedStudent extends Student {

	private List<EnrolledFixedSubject> EnrolledFixedSubjects;

	
	public YearBasedStudent(String userId, String email, String password, String fullName, String role, boolean status, String dob,
			String studentId, String major) {
		super(userId, email, password, fullName, role, status, dob, studentId, major);
		EnrolledFixedSubjects = new ArrayList<EnrolledFixedSubject>();
	}

	public void viewRepeatSubject() {
		
	}

	public String toString() {
		return null;
	}

	@Override
	public void viewResult() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double calculateCPA() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkGraduationRequirements() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void viewTimeTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enrollClassSection() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Getter and Setter

	public List<EnrolledFixedSubject> getEnrolledFixedSubjects() {
		return EnrolledFixedSubjects;
	}

	public void setEnrolledFixedSubjects(List<EnrolledFixedSubject> enrolledFixedSubjects) {
		EnrolledFixedSubjects = enrolledFixedSubjects;
	}
	
	

}
