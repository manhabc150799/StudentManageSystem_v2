import java.util.ArrayList;
import java.util.List;

public class Lecturer extends User {

	private String lecturerId;

	private String department;

	private List<ClassSection> assignedClasses;

	/** IDs of class sections this lecturer is assigned to */
	private List<String> assignedClassIds;

	private List<Schedule> schedules;
	
	

	public Lecturer(String userId, String email, String password, String fullName, String role, boolean status, String dob,
					String lecturerId, String department) {
		super(userId, email, password, fullName, role, status, dob);
		this.lecturerId = lecturerId;
		this.department = department;
		this.assignedClasses = new ArrayList<ClassSection>();
		this.assignedClassIds = new ArrayList<>();
	}

	public void enterScore() {
		
	}
	public void viewTeachingSchedule() {

	}

	/**
	 * Adds the given class section ID to the list of assignments
	 * if not already present.
	 */
	public void addAssignedClass(String classSectionId) {
		if (!assignedClassIds.contains(classSectionId)) {
			assignedClassIds.add(classSectionId);
		}
	}

	/**
	 * Removes the given class section ID from the lecturer's assignments.
	 */
	public void removeAssignedClass(String classSectionId) {
		assignedClassIds.remove(classSectionId);
	}

	public List<String> getAssignedClassIds() {
		return assignedClassIds;
	}

	//Getters and Setters
	
	public String getLecturerId() {
		return lecturerId;
	}

	public void setLecturerId(String lecturerId) {
		this.lecturerId = lecturerId;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public List<ClassSection> getAssignedClasses() {
		return assignedClasses;
	}

	public void setAssignedClasses(List<ClassSection> assignedClasses) {
		this.assignedClasses = assignedClasses;
	}

	public List<Schedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(List<Schedule> schedules) {
		this.schedules = schedules;
	}
	
}
