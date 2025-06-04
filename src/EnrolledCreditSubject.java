public class EnrolledCreditSubject extends EnrolledSubject {

	public CreditSubject subject;

	public int semeter;

	public float midtermScore;

	public float finalScore;

	public String grade;

	public String status;
	
	

	public EnrolledCreditSubject(ClassSection classSection, int studentId, CreditSubject subject, int semeter,
			float midtermScore, float finalScore, String grade, String status) {
		super(classSection, studentId);
		this.subject = subject;
		this.semeter = semeter;
		this.midtermScore = midtermScore;
		this.finalScore = finalScore;
		this.grade = grade;
		this.status = status;
	}

	// Tính điểm tổng kết
    public float calculateTotalScore() {
        float midtermWeight = subject.midtermWeight;
        float finalExamWeight = subject.finalExamWeight;
        float totalScore = (midtermScore * midtermWeight + finalScore * finalExamWeight) / 100;
        return Math.round(totalScore * 10) / 10.0f; // Làm tròn đến số thập phân thứ nhất
    }

    // Tính điểm chữ
    public String calculateGrade() {
        if (!isPassed()) {
            return "F";
        }

        float totalScore = calculateTotalScore();
        if (totalScore < 4.0) {
            return "F";
        } else if (totalScore < 5.0) {
            return "D";
        } else if (totalScore < 5.5) {
            return "D+";
        } else if (totalScore < 6.5) {
            return "C";
        } else if (totalScore < 7.0) {
            return "C+";
        } else if (totalScore < 8.0) {
            return "B";
        } else if (totalScore < 8.5) {
            return "B+";
        } else if (totalScore < 9.5) {
            return "A";
        } else {
            return "A+";
        }
    }

    // Kiểm tra điều kiện qua môn
    public boolean isPassed() {
        return calculateTotalScore() > 4.0 && midtermScore >= 3.0 && finalScore >= 3.0;
    }

    // Override phương thức toString()
    public String toString() {
        return "EnrolledCreditSubject{" +
                "subject=" + subject.subjectName +
                ", semester=" + semeter +
                ", midtermScore=" + midtermScore +
                ", finalScore=" + finalScore +
                ", grade='" + calculateGrade() + '\'' +
                ", status='" + (isPassed() ? "Passed" : "Failed") + '\'' +
                '}';
    }
		

}
