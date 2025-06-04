public abstract class Student extends User {

	protected String studentId;

	protected String major;

	protected list<Subject> curriculum;

	protected list<Object> completedSubjects;

	protected list<Schedule> schedules;

	public abstract void viewResult();

	public abstract void calculateCPA();

	public abstract boolean checkGraduationRequirements();

	public abstract void viewTimeTable();

}
