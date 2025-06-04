import org.apache.poi.xdgf.usermodel.section.geometry.RelEllipticalArcTo;

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
		if(enrolledStudents.size() >= maxCapacity) 
		{
			System.out.println("This class is full. Can not add student");
			return false;
		}
		else if(!isStudentEligible(student)) 
		{
			System.out.println("Students are not eligible to take this class.");
			return isStudentEligible(student);			
		}
		else
		{
			enrolledStudents.add(student);
			return isStudentEligible(student);
		}
		
	}

	public boolean removeStudent(int studentId) {
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
