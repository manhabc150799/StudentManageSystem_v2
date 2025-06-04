import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreditStudent extends Student {

	private List<EnrolledCreditSubject> enrolledCreditSubjects;
	
	public CreditStudent(String userId, String email, String password, String fullName, String role, boolean status, String dob,
			String studentId, String major) {
		super(userId, email, password, fullName, role, status, dob, studentId, major);
		this.enrolledCreditSubjects = new ArrayList<EnrolledCreditSubject>();
	}

	

	public String toString() {
		int completedCredits = enrolledCreditSubjects.stream()
                .filter(EnrolledCreditSubject::isPassed)
                .mapToInt(subject -> subject.subject.creditHours)
                .sum();
        double cpa = calculateCPA();
        return String.format("%s - %s - Credit Student - Completed Credits: %d - CPA: %.2f",
                getFullName(), this.studentId, completedCredits, cpa);
	}
	
	//Getters and Setters

	public List<EnrolledCreditSubject> getEnrolledCreditSubjects() {
		return enrolledCreditSubjects;
	}

	public void setEnrolledCreditSubjects(List<EnrolledCreditSubject> _enrolledCreditSubjects) {
		enrolledCreditSubjects = _enrolledCreditSubjects;
	}

	@Override
	public void viewResult() {
		System.out.println("Choose an option:");
        System.out.println("1. View Personal Transcript");
        System.out.println("2. View Completed Subjects");

        int choice = InputUtil.getInt("Enter your choice: ");

        if (choice == 1) {
            System.out.println("Personal Transcript:");
            enrolledCreditSubjects.stream()
                    .sorted(Comparator.comparingInt(subject -> subject.semeter))
                    .forEach(System.out::println);
        } else if (choice == 2) {
            System.out.println("Completed Subjects:");
            enrolledCreditSubjects.stream()
                    .filter(EnrolledCreditSubject::isPassed)
                    .forEach(System.out::println);
        } else {
            System.out.println("Invalid choice.");
        }
	}

	@Override
	public double calculateCPA() {
		double totalScore = 0;
        int totalCredits = 0;

        for (EnrolledCreditSubject subject : enrolledCreditSubjects) {
            if (subject.isPassed()) {
                totalScore += subject.calculateTotalScore() * subject.subject.creditHours;
                totalCredits += subject.subject.creditHours;
            }
        }

        return totalCredits == 0 ? 0 : totalScore / totalCredits;
	}

	@Override
	public boolean checkGraduationRequirements() {
		int totalCredits = enrolledCreditSubjects.stream()
                .filter(EnrolledCreditSubject::isPassed)
                .mapToInt(subject -> subject.subject.creditHours)
                .sum();

        boolean hasNoFGrade = enrolledCreditSubjects.stream()
                .noneMatch(subject -> subject.grade.equals("F"));

        double cpa = calculateCPA();

        return totalCredits >= 132 && hasNoFGrade && cpa >= 2.0;
	}

	@Override
	public void viewTimeTable() {
		System.out.println("Current Semester Timetable:");
        enrolledCreditSubjects.stream()
                .filter(subject -> subject.semeter == getCurrentSemester())
                .forEach(subject -> System.out.println(subject.classSection));
	}

	@Override
	public boolean enrollClassSection() {
		return false;
	}
	
	
	private int getCurrentSemester() {
        // Placeholder for determining the current semester.
        return 1;
    }
}
