import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class ClassSection {

	public String classSectionId;

	public Subject subject;

	/** Optional prerequisite subject required before enrollment */
	public Subject requirement;

	public String semeter;

	public int maxLecturers;

	public int maxCapacity;

	public List<String> lecturerIds;


	public List<Student> enrolledStudents;

	public List<Schedule> schedules;

	/** Stores midterm score by student ID */
	public Map<String, Float> midtermScores;

	/** Stores final exam score by student ID */
	public Map<String, Float> finalScores;

	public ClassSection(String classSectionId, Subject subject, String semeter,
						int maxLecturers, int maxCapacity, List<Schedule> schedules,
						Subject requirement) {
		this.classSectionId = classSectionId;
		this.subject = subject;
		this.requirement = requirement;
		this.semeter = semeter;
		this.maxLecturers = maxLecturers;
		this.maxCapacity = maxCapacity;
		this.lecturerIds = new ArrayList<>();
		this.enrolledStudents = new ArrayList<Student>();
		this.schedules = schedules;
		this.midtermScores = new HashMap<>();
		this.finalScores = new HashMap<>();
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
			midtermScores.put(student.studentId, 0f);
			finalScores.put(student.studentId, 0f);
			try {
				List<Student> all = new ArrayList<>();
				all.addAll(Main.creditStudents);
				all.addAll(Main.yearBasedStudents);
				ExcelUtil.writeStudentsToExcel(all, Manager.STUDENT_EXCEL_PATH);
				ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
				ExcelUtil.writeGradesToExcel(this, Manager.GRADE_DIR_PATH);
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
		midtermScores.remove(student.studentId);
		finalScores.remove(student.studentId);
		try {
			List<Student> all = new ArrayList<>();
			all.addAll(Main.creditStudents);
			all.addAll(Main.yearBasedStudents);
			ExcelUtil.writeStudentsToExcel(all, Manager.STUDENT_EXCEL_PATH);
			ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
			ExcelUtil.writeGradesToExcel(this, Manager.GRADE_DIR_PATH);
		} catch (IOException ex) {
			System.err.println("Failed to save enrollment: " + ex.getMessage());
		}
		return true;
	}

	public boolean isStudentEligible(Student student) {
		if (requirement == null) {
			return true;
		}
		for (String id : student.getEnrolledClassIds()) {
			ClassSection cs = Manager.classSections.stream()
					.filter(c -> c.classSectionId.equals(id))
					.findFirst()
					.orElse(null);
			if (cs != null && cs.subject.subjectCode.equalsIgnoreCase(requirement.subjectCode)) {
				if (cs.getMidtermScore(student.studentId) >= 3.0f && cs.getFinalScore(student.studentId) >= 3.0f) {
					return true;
				}
			}
		}
		return false;
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

	public Subject getRequirement() {
		return requirement;
	}
	/**
	 * Returns a comma separated list of lecturer IDs assigned to this class.
	 */
	public String getLecturer() {
		return String.join(",", lecturerIds);
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public int getMaxLecturers() {
		return maxLecturers;
	}

	public List<String> getLecturerIds() {
		return lecturerIds;
	}

	public boolean addLecturer(String lecturerId) {
		if (lecturerIds.contains(lecturerId) || lecturerIds.size() >= maxLecturers) {
			return false;
		}
		lecturerIds.add(lecturerId);
		return true;
	}

	public void removeLecturer(String lecturerId) {
		lecturerIds.remove(lecturerId);
	}

	/** Returns the midterm score for the given student ID, or 0 if missing */
	public float getMidtermScore(String studentId) {
		return midtermScores.getOrDefault(studentId, 0f);
	}

	/** Returns the final exam score for the given student ID, or 0 if missing */
	public float getFinalScore(String studentId) {
		return finalScores.getOrDefault(studentId, 0f);
	}

	/** Sets the midterm score for the specified student */
	public void setMidtermScore(String studentId, float score) {
		midtermScores.put(studentId, score);
	}

	/** Sets the final exam score for the specified student */
	public void setFinalScore(String studentId, float score) {
		finalScores.put(studentId, score);
	}

	/**
	 * Calculates the CPA on a 4.0 scale for the specified student. The
	 * midterm and final exam scores are weighted using the subject's weight
	 * percentages and then converted from a 10 point scale to a 4 point
	 * scale.
	 */
	public float calculateCPA(String studentId) {
		float mid = getMidtermScore(studentId);
		float fin = getFinalScore(studentId);
		float total10;
		if (subject instanceof CreditSubject credit) {
			total10 = (mid * credit.midtermWeight + fin * credit.finalExamWeight) / 100f;
		} else {
			total10 = (mid + fin) / 2f;
		}
		// convert from 10 point scale to 4 point scale
		return total10 * 4f / 10f;
	}
}
