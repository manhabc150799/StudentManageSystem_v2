import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class ClassSection {

	public String classSectionId;

	public Subject subject;
	
	public String semeter;

	public String lecturer;

	public int maxCapacity;

	public List<Student> enrolledStudents;

	public List<Schedule> schedules;

	public ClassSection(String classSectionId, Subject subject, String semeter, String lecturer,
			int maxCapacity, List<Schedule> schedules) {
		this.classSectionId = classSectionId;
		this.subject = subject;
		this.semeter = semeter;
		this.lecturer = lecturer;
		this.maxCapacity = maxCapacity;
		this.enrolledStudents = new ArrayList<Student>();
		this.schedules = schedules;
	}

	public boolean addStudent(Student student) {
		if (enrolledStudents.size() >= maxCapacity) {
			System.out.println("This class is full. Can not add student");
			return false;
		} else if (!isStudentEligible(student)) {
			System.out.println("Students are not eligible to take this class.");
			return false;
		} else if (enrolledStudents.contains(student)) {
			System.out.println("Student already enrolled in this class.");
			return false;
		} else {
			enrolledStudents.add(student);
			student.addEnrolledClass(classSectionId);
			try {
				List<Student> all = new ArrayList<>();
				all.addAll(Main.creditStudents);
				all.addAll(Main.yearBasedStudents);
				ExcelUtil.writeStudentsToExcel(all, Manager.STUDENT_EXCEL_PATH);
				ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
			} catch (IOException ex) {
				System.err.println("Failed to save enrollment: " + ex.getMessage());
			}
			return true;
		}

	}

	public boolean removeStudent(Student student) {
		if (!enrolledStudents.contains(student)) {
			System.out.println("Student not enrolled in this class.");
			return false;
		}

		enrolledStudents.remove(student);
		student.removeEnrolledClass(classSectionId);
		try {
			List<Student> all = new ArrayList<>();
			all.addAll(Main.creditStudents);
			all.addAll(Main.yearBasedStudents);
			ExcelUtil.writeStudentsToExcel(all, Manager.STUDENT_EXCEL_PATH);
			ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
		} catch (IOException ex) {
			System.err.println("Failed to save enrollment: " + ex.getMessage());
		}
		return true;
	}

	public boolean isStudentEligible(Student student) {
		return true;
	}

	public String toString() {
		return null;
	}

    public String getClassSectionId(){
		return classSectionId;
	}
	public String getSemeter()
	{
		return semeter;
	}
	public Subject getSubject(){
		return subject;
	}
	public String getLecturer(){
		return lecturer;
	}
	public int getMaxCapacity(){
		return maxCapacity;
	}
}
