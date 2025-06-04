public class Schedule {

	public String dayOfWeek;

	public String startTime;

	public String endTime;

	public String room;

	
	public Schedule(String dayOfWeek, String startTime, String endTime, String room) {
		this.dayOfWeek = dayOfWeek;
		this.startTime = startTime;
		this.endTime = endTime;
		this.room = room;
	}

	public String toString() {
		return "Schedule{" +
			      "dayOfWeek='" + dayOfWeek + '\'' +
			      ", startTime='" + startTime + '\'' +
			      ", endTime='" + endTime + '\'' +
			      ", room='" + room + '\'' +
			      '}';
	}

	public boolean isConflict() {
		return false;
	}
}
