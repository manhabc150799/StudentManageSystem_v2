import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dialog.ModalityType;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.table.DefaultTableModel;


public class Manager extends User {

	private String managerId;

	private List<Student> students;

	public static List<ClassSection> classSections;

	/** Path to the exported Excel file in the user's Downloads directory */
	public static final String CLASS_SECTION_EXCEL_PATH =
			System.getProperty("user.home") + File.separator +
					"Downloads" + File.separator + "ClassSections.xlsx";

	/** Path to export the student list */
	public static final String STUDENT_EXCEL_PATH =
			System.getProperty("user.home") + File.separator +
					"Downloads" + File.separator + "Students.xlsx";


	public Manager(String userId, String email, String password,String fullName, String role, boolean status, String dob,
				   String managerId) {
		super(userId, email, password,fullName, role, status, dob);
		this.managerId = managerId;
		this.students = new ArrayList<>();

		try {
			Manager.classSections = ExcelUtil.readClassSectionsFromExcel(CLASS_SECTION_EXCEL_PATH);
			this.students = ExcelUtil.readStudentsFromExcel(STUDENT_EXCEL_PATH);

			// if there are no students in Excel fallback to defaults and export
			if (this.students.isEmpty()) {
				this.students.addAll(Main.creditStudents);
				this.students.addAll(Main.yearBasedStudents);
			}

			// rebuild enrollment relationships
			for (Student s : this.students) {
				for (String id : s.getEnrolledClassIds()) {
					ClassSection cs = Manager.classSections.stream()
							.filter(c -> c.classSectionId.equals(id))
							.findFirst()
							.orElse(null);
					if (cs != null && !cs.enrolledStudents.contains(s)) {
						cs.enrolledStudents.add(s);
					}
				}
			}

			ExcelUtil.writeStudentsToExcel(this.students, STUDENT_EXCEL_PATH);
		} catch (IOException ex) {
			System.err.println("Failed to load data from Excel: " + ex.getMessage());
			Manager.classSections = new ArrayList<>();
		}
	}

	public void viewListStudent() {
		students.sort(Comparator.comparing(Student::getFullName));
		System.out.printf("%-5s %-10s %-25s %-15s %-20s %-10s %-10s %-12s %-15s\n",
				"STT", "UserID", "Email", "Password", "Full Name", "Role", "Status", "DOB",
				"Student ID", "Major");
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
			String role = (student instanceof CreditStudent) ? "CreditStudent" :
					(student instanceof YearBasedStudent) ? "YearBasedStudent" : student.getRole();
			System.out.printf("%-5d %-10s %-25s %-15s %-20s %-10s %-10s %-12s %-15s\n",
					i + 1,
					student.getUserId(),
					student.getEmail(),
					student.getPassword(),
					student.getFullName(),
					role,
					student.isStatus(),
					student.getDob(),
					student.studentId,
					student.major);
		}
	}

	public void addClassSection() {
		 Scanner scanner = new Scanner(System.in);

	        System.out.print("Enter Class Section ID: ");
	        String classSectionId = scanner.nextLine();
	        System.out.print("Enter Subject Name: ");
	        String subjectName = scanner.nextLine();

	        Subject subject = Main.creditSubjects.stream()
	                .filter(s -> s.subjectName.equalsIgnoreCase(subjectName))
	                .findFirst()
	                .orElseGet(() -> Main.fixedSubjects.stream()
	                        .filter(s -> s.subjectName.equalsIgnoreCase(subjectName))
	                        .findFirst()
	                        .orElse(null));

	        if (subject == null) {
	            System.out.println("Subject not found in the available lists. Please try again.");
	            scanner.close();
	            return;
	        }

	        System.out.print("Enter Semester: ");
	        String semester = scanner.nextLine();
		System.out.print("Enter Max Lecturers: ");
		int maxLecturers = Integer.parseInt(scanner.nextLine());
		System.out.print("Enter Max Students: ");
		int maxStudents = Integer.parseInt(scanner.nextLine());

	        List<Schedule> schedules = new ArrayList<>();
	        System.out.println("Enter Schedule details:");
	        while (true) {
	            System.out.print("Start Time (HH:mm): ");
	            String startTime = scanner.nextLine();
	            System.out.print("End Time (HH:mm): ");
	            String endTime = scanner.nextLine();
	            System.out.print("Day of Week: ");
	            String dayOfWeek = scanner.nextLine();
	            System.out.print("Address: ");
	            String address = scanner.nextLine();

	            schedules.add(new Schedule(startTime, endTime, dayOfWeek, address));

	            System.out.print("Add another schedule? (yes/no): ");
	            if (!scanner.nextLine().equalsIgnoreCase("yes")) break;
	        }

	        boolean duplicate = classSections.stream()
	                .anyMatch(cs -> cs.classSectionId.equalsIgnoreCase(classSectionId));

	        if (duplicate) {
	            System.out.println("Class Section ID already exists. Please try again.");
			} else {
				ClassSection newClassSection = new ClassSection(classSectionId, subject, semester, maxLecturers, maxStudents, schedules);
				classSections.add(newClassSection);
				try {
					ExcelUtil.writeClassSectionsToExcel(classSections, CLASS_SECTION_EXCEL_PATH);
				} catch (IOException ex) {
					System.err.println("Failed to save to Excel: " + ex.getMessage());
				}
				System.out.println("Class Section added successfully.");
			}

	        scanner.close();
	}

	public void syncWithExcel() {
		try {
			Manager.classSections = ExcelUtil.readClassSectionsFromExcel(CLASS_SECTION_EXCEL_PATH);
			this.students = ExcelUtil.readStudentsFromExcel(STUDENT_EXCEL_PATH);
		} catch (IOException ex) {
			System.err.println("Failed to sync with Excel: " + ex.getMessage());
		}
	}




	//Getters and Setters

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

}


class ManagerPanel extends JFrame {
	private static final long serialVersionUID = 1L;
	/** The manager instance backing this UI */
	private final Manager manager;
	private JTable studentTable;
	private DefaultTableModel studentTableModel;

	// Model for the registered class table
	DefaultTableModel classSectionTableModel;

	// Table for displaying registered classes
	JTable classSectionTable;


    public ManagerPanel(Manager manager) {
		this.manager = manager;

		// Sync the latest data from Excel when opening the panel
		this.manager.syncWithExcel();


    	setTitle("Manager Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Menu panel
        JPanel menuPanel = new JPanel();
        JButton viewStudentsButton = new JButton("View Student List");
        JButton viewClassSectionButton = new JButton("Class Section");
        JButton addClassSectionButton = new JButton("Add Class Section");
		JButton addStudentButton = new JButton("Add Student");
        menuPanel.add(viewStudentsButton);
        menuPanel.add(viewClassSectionButton);
        menuPanel.add(addClassSectionButton);
		menuPanel.add(addStudentButton);

        mainPanel.add(menuPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new CardLayout());

        // View Students content
        JPanel studentPanel = new JPanel(new BorderLayout());

        // Khởi tạo JTable và JScrollPane
		String[] columnNames = {"STT", "UserID", "Email", "Password", "Full Name", "Role", "Status", "DOB", "Student ID", "Major"};
        studentTableModel = new DefaultTableModel(columnNames, 0);
		studentTable = new JTable(studentTableModel);

        refreshStudentTable(manager.getStudents());
        studentTable.setModel(studentTableModel);
        studentPanel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshStudentTable(manager.getStudents()));
		// add student button within the student panel for convenience
		JButton addStudentPanelButton = new JButton("Add Student");
		addStudentPanelButton.addActionListener(e -> showAddStudentDialog(manager));

		JPanel studentBottomPanel = new JPanel();
		studentBottomPanel.add(addStudentPanelButton);
		studentBottomPanel.add(refreshButton);

		studentPanel.add(studentBottomPanel, BorderLayout.SOUTH);

        contentPanel.add(studentPanel, "View Student List");

        // View Class Section contents
    	JPanel classSectionPanel = new JPanel(new BorderLayout());


    	//Initital
    	String[] column2Names = {"STT", "Mã lớp", "Mã môn học", "Tên môn học", "Thời khóa biểu", "SL tối đa", "SL Đăng ký", "Giảng viên"};
        classSectionTableModel = new DefaultTableModel(column2Names, 0);
        classSectionTable = new JTable(classSectionTableModel);
        classSectionPanel.add(new JScrollPane(classSectionTable), BorderLayout.CENTER);

        JButton refresh2Button = new JButton("Refresh");
        refresh2Button.addActionListener(e -> refreshClassSectionTable());
        classSectionPanel.add(refresh2Button, BorderLayout.SOUTH);

        contentPanel.add(classSectionPanel, "Class Section");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        viewStudentsButton.addActionListener(e -> {
			// Keep student list synchronized and refreshed whenever opened
			manager.syncWithExcel();
			refreshStudentTable(manager.getStudents());
			((CardLayout) contentPanel.getLayout()).show(contentPanel, "View Student List");
        });
        viewClassSectionButton.addActionListener(e -> {
			// Ensure class sections show latest data each time
			manager.syncWithExcel();
			refreshClassSectionTable();
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Class Section");
          });
        addClassSectionButton.addActionListener(e -> {
          showAddClassSectionDialog(manager);
        });
		addStudentButton.addActionListener(e -> {
			showAddStudentDialog(manager);
		});


        add(mainPanel);
        setVisible(true);
    }


    private void showAddClassSectionDialog(Manager manager) {
    	  // Create a new JDialog for adding a Class Section
    	  JDialog classSectionDialog = new JDialog(this, "Add Class Section", ModalityType.MODELESS);
    	  classSectionDialog.setSize(500, 700);
    	  classSectionDialog.setLayout(new GridLayout(0, 2));

    	  // Input fields
    	  JLabel idLabel = new JLabel("Class Section ID:");
    	  JTextField idField = new JTextField();
    	  JLabel subjectLabel = new JLabel("Subject:");
    	  JComboBox<String> subjectComboBox = new JComboBox<>();
    	  JLabel semesterLabel = new JLabel("Semester:");
    	  JTextField semesterField = new JTextField();
		  JLabel lecturerLabel = new JLabel("Max Lecturers:");
		  JTextField lecturerField = new JTextField();
    	  JLabel maxStudentsLabel = new JLabel("Max Students:");
    	  JTextField maxStudentsField = new JTextField();

    	  JLabel scheduleLabel = new JLabel("Schedule:");
    	  JButton addScheduleButton = new JButton("Add Schedule");
    	  JTextArea scheduleArea = new JTextArea(5, 20);
    	  scheduleArea.setEditable(false);
    	  List<Schedule> schedules = new ArrayList<>();

    	  // Populate subjectComboBox with available subjects
    	  for (Subject subject : getAvailableSubjects()) {
    	      subjectComboBox.addItem(subject.subjectName);
    	  }

    	  // Add Schedule Button Action
    	  addScheduleButton.addActionListener(e -> {
    	      // Create a separate frame for adding a schedule (reuse logic from original code)
    	      JFrame scheduleFrame = new JFrame("Add Schedule");
    	      scheduleFrame.setSize(400, 300);
    	      scheduleFrame.setLayout(new GridLayout(0, 2));
    	      JLabel dayOfWeekLabel = new JLabel("Day of Week:");
    	        JTextField dayOfWeekField = new JTextField();
    	        JLabel startTimeLabel = new JLabel("Start Time (HH:mm):");
    	        JTextField startTimeField = new JTextField();
    	        JLabel endTimeLabel = new JLabel("End Time (HH:mm):");
    	        JTextField endTimeField = new JTextField();
    	        JLabel addressLabel = new JLabel("Address:");
    	        JTextField addressField = new JTextField();

    	        JButton saveScheduleButton = new JButton("Save Schedule");

    	        saveScheduleButton.addActionListener(event -> {
    	            String dayOfWeek = dayOfWeekField.getText();
    	            String startTime = startTimeField.getText();
    	            String endTime = endTimeField.getText();
    	            String address = addressField.getText();

    	            // Validate input (basic validation)
    	            if (dayOfWeek.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || address.isEmpty()) {
    	                JOptionPane.showMessageDialog(scheduleFrame, "All fields are required!");
    	                return;
    	            }

    	            // Add the schedule to the list
    	            Schedule schedule = new Schedule(startTime, endTime, dayOfWeek, address);
    	            schedules.add(schedule);
    	            scheduleArea.append(schedule.toString() + "\n");

    	            // Close the schedule frame
    	            scheduleFrame.dispose();
    	        });

    	        scheduleFrame.add(dayOfWeekLabel);
    	        scheduleFrame.add(dayOfWeekField);
    	        scheduleFrame.add(startTimeLabel);
    	        scheduleFrame.add(startTimeField);
    	        scheduleFrame.add(endTimeLabel);
    	        scheduleFrame.add(endTimeField);
    	        scheduleFrame.add(addressLabel);
    	        scheduleFrame.add(addressField);
    	        scheduleFrame.add(new JLabel());
    	        scheduleFrame.add(saveScheduleButton);
    	      scheduleFrame.setVisible(true);
    	  });

    	  JButton submitButton = new JButton("Submit");

    	  submitButton.addActionListener(e -> {
    	      String classSectionId = idField.getText();
    	      String subjectName = (String) subjectComboBox.getSelectedItem();
    	      String semester = semesterField.getText();
			  int maxLecturers = Integer.parseInt(lecturerField.getText());
			  int maxStudents = Integer.parseInt(maxStudentsField.getText());

    	      Subject selectedSubject = findSubjectByName(subjectName);
    	      if (selectedSubject == null) {
    	          JOptionPane.showMessageDialog(classSectionDialog, "Invalid Subject Name!");
    	          return;
    	      }

    	      boolean duplicate = Manager.classSections.stream()
    	          .anyMatch(cs -> cs.classSectionId.equalsIgnoreCase(classSectionId));

    	      if (duplicate) {
    	          JOptionPane.showMessageDialog(classSectionDialog, "Class Section ID already exists!");
			  } else {
				  ClassSection newClassSection = new ClassSection(
						  classSectionId, selectedSubject, semester, maxLecturers, maxStudents, schedules

				  );
				  Manager.classSections.add(newClassSection);
				  try {
					  ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
				  } catch (IOException ex) {
					  System.err.println("Failed to save to Excel: " + ex.getMessage());
				  }
				  JOptionPane.showMessageDialog(classSectionDialog, "Class Section added successfully!");

				  classSectionDialog.dispose();
			  }
    	  });

    	  // Add components to the dialog
    	  classSectionDialog.add(idLabel);
    	  classSectionDialog.add(idField);
    	  classSectionDialog.add(subjectLabel);
    	  classSectionDialog.add(subjectComboBox);
    	  classSectionDialog.add(semesterLabel);
    	  classSectionDialog.add(semesterField);
    	  classSectionDialog.add(lecturerLabel);
    	  classSectionDialog.add(lecturerField);
    	  classSectionDialog.add(maxStudentsLabel);
    	  classSectionDialog.add(maxStudentsField);
    	  classSectionDialog.add(scheduleLabel);
    	  classSectionDialog.add(addScheduleButton);
    	  classSectionDialog.add(new JScrollPane(scheduleArea));
    	  classSectionDialog.add(submitButton);

		classSectionDialog.setVisible(true);
	}

	private void showAddStudentDialog(Manager manager) {
		JDialog studentDialog = new JDialog(this, "Add Student", ModalityType.MODELESS);
		studentDialog.setSize(400, 400);
		studentDialog.setLayout(new GridLayout(0, 2));

		JLabel userIdLabel = new JLabel("User ID:");
		JTextField userIdField = new JTextField();
		JLabel emailLabel = new JLabel("Email:");
		JTextField emailField = new JTextField();
		JLabel passwordLabel = new JLabel("Password:");
		JTextField passwordField = new JTextField();
		JLabel nameLabel = new JLabel("Full Name:");
		JTextField nameField = new JTextField();
		JLabel typeLabel = new JLabel("Student Type:");
		JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"CreditStudent", "YearBasedStudent"});
		JCheckBox statusCheckBox = new JCheckBox("Active");
		statusCheckBox.setSelected(true);
		JLabel dobLabel = new JLabel("DOB (yyyy-mm-dd):");
		JTextField dobField = new JTextField();
		JLabel studentIdLabel = new JLabel("Student ID:");
		JTextField studentIdField = new JTextField();
		JLabel majorLabel = new JLabel("Major:");
		JTextField majorField = new JTextField();

		JButton submitButton = new JButton("Submit");
		submitButton.addActionListener(e -> {
			String userId = userIdField.getText().trim();
			String email = emailField.getText().trim();
			String password = passwordField.getText().trim();
			String fullName = nameField.getText().trim();
			String type = (String) typeComboBox.getSelectedItem();
			boolean status = statusCheckBox.isSelected();
			String dob = dobField.getText().trim();
			String studentId = studentIdField.getText().trim();
			String major = majorField.getText().trim();

			if (userId.isEmpty() || email.isEmpty() || password.isEmpty() || fullName.isEmpty()
					|| studentId.isEmpty() || major.isEmpty() || dob.isEmpty()) {
				JOptionPane.showMessageDialog(studentDialog, "All fields are required!");
				return;
			}

			Student newStudent;
			if ("YearBasedStudent".equalsIgnoreCase(type)) {
				newStudent = new YearBasedStudent(userId, email, password, fullName,
						type, status, dob, studentId, major);
			} else {
				newStudent = new CreditStudent(userId, email, password, fullName,
						type, status, dob, studentId, major);
			}
			manager.getStudents().add(newStudent);
			try {
				ExcelUtil.writeStudentsToExcel(manager.getStudents(), Manager.STUDENT_EXCEL_PATH);
			} catch (IOException ex) {
				System.err.println("Failed to save to Excel: " + ex.getMessage());
			}
			refreshStudentTable(manager.getStudents());
			JOptionPane.showMessageDialog(studentDialog, "Student added successfully!");
			studentDialog.dispose();
		});

		studentDialog.add(userIdLabel);    studentDialog.add(userIdField);
		studentDialog.add(emailLabel);     studentDialog.add(emailField);
		studentDialog.add(passwordLabel);  studentDialog.add(passwordField);
		studentDialog.add(nameLabel);      studentDialog.add(nameField);
		studentDialog.add(typeLabel);      studentDialog.add(typeComboBox);
		studentDialog.add(new JLabel("Status:")); studentDialog.add(statusCheckBox);
		studentDialog.add(dobLabel);      studentDialog.add(dobField);
		studentDialog.add(studentIdLabel); studentDialog.add(studentIdField);
		studentDialog.add(majorLabel);     studentDialog.add(majorField);
		studentDialog.add(new JLabel());   studentDialog.add(submitButton);
		studentDialog.setVisible(true);
	}


    //Get Schedule Text
    private String getScheduleText(List<Schedule> schedules) {
        StringBuilder scheduleText = new StringBuilder();

        // Optional sorting by day
        if (shouldSortSchedules(schedules)) {
            schedules.sort((schedule1, schedule2) -> schedule1.dayOfWeek.compareTo(schedule2.dayOfWeek));
        }

        for (Schedule schedule : schedules) {
            scheduleText.append(schedule.dayOfWeek)
                    .append(" ")
                    .append(schedule.startTime)
                    .append("-")
                    .append(schedule.endTime)
                    .append(" (")
                    .append(schedule.room)
                    .append("), ");
        }

        // Remove trailing comma and space
        if (scheduleText.length() > 2) {
            scheduleText.setLength(scheduleText.length() - 2);
        }
        return scheduleText.toString();
    }

 // Optional method to decide if schedules should be sorted (modify as needed)
    private boolean shouldSortSchedules(List<Schedule> schedules) {
        // Example: Sort if there are more than 2 schedules
        return schedules.size() > 2;
    }

    //Refresh ClassSection Table
    public void refreshClassSectionTable() {
        try {
            // Clear existing data
			// Ensure we have the latest data from Excel
			manager.syncWithExcel();
            classSectionTableModel.setRowCount(0);

            int count = 1;
            for (ClassSection classSection : Manager.classSections) {
                // Get data from ClassSection object
                String classId = classSection.classSectionId;
                String subjectCode = classSection.subject.subjectCode;
                String subjectName = classSection.subject.subjectName;
                String scheduleText = getScheduleText(classSection.schedules); // Function to format schedule
                int maxCapacity = classSection.maxCapacity;
                int enrolledCount = classSection.enrolledStudents.size();
				String lecturerName = "";
				if (!classSection.lecturerIds.isEmpty()) {
					java.util.List<String> names = new java.util.ArrayList<>();
					for (String id : classSection.lecturerIds) {
						Lecturer lec = Main.lecturers.stream()
								.filter(l -> l.getLecturerId().equalsIgnoreCase(id))
								.findFirst()
								.orElse(null);
						names.add(lec != null ? lec.getFullName() : id);
					}
					lecturerName = String.join(",", names);
				}

                // Add a row to the model
                classSectionTableModel.addRow(new Object[]{
                        count++,
                        classId,
                        subjectCode,
                        subjectName,
                        scheduleText,
                        maxCapacity,
                        enrolledCount,
						lecturerName
                });
            }

            // Update table display
            classSectionTableModel.fireTableDataChanged();
        } catch (Exception ex) {
            System.err.println("Error refreshing class section table: " + ex.getMessage());
        }
    }



    // Refresh Student Table
    public void refreshStudentTable(List<Student> students) {
        try {
            // Kiểm tra xem danh sách sinh viên có null hay không
            if (students == null || students.isEmpty()) {
                // Nếu không có dữ liệu, xóa toàn bộ dữ liệu trên bảng
                studentTableModel.setRowCount(0);
                return;
            }

            // Xóa dữ liệu cũ
            studentTableModel.setRowCount(0);

            // Thêm dữ liệu mới
            int count = 1;
            for (Student student : students) {
				String role = (student instanceof CreditStudent) ? "CreditStudent" :
						(student instanceof YearBasedStudent) ? "YearBasedStudent" : student.getRole();
                studentTableModel.addRow(new Object[]{
                    count++,
						student.getUserId(),
						student.getEmail(),
						student.getPassword(),
                    student.getFullName(),
						role,
						student.isStatus() ? "Active" : "Inactive",
						student.getDob(),
                    student.studentId,
                    student.major,
                });
            }

            studentTable.setModel(studentTableModel);

            // Cập nhật lại giao diện bảng
            studentTableModel.fireTableDataChanged();

			try {
				ExcelUtil.writeStudentsToExcel(students, Manager.STUDENT_EXCEL_PATH);
			} catch (IOException ex) {
				System.err.println("Failed to export students to Excel: " + ex.getMessage());
			}
        } catch (Exception ex) {
            // Xử lý lỗi nếu có
            System.err.println("Error refreshing student table: " + ex.getMessage());
        }
    }

    public List<Subject> getAvailableSubjects() {
        List<Subject> availableSubjects = new ArrayList<>();
        availableSubjects.addAll(Main.creditSubjects);
        availableSubjects.addAll(Main.fixedSubjects);
        return availableSubjects;
    }

    public Subject findSubjectByName(String subjectName) {
        return Main.creditSubjects.stream()
                .filter(s -> s.subjectName.equalsIgnoreCase(subjectName))
                .findFirst()
                .orElseGet(() -> Main.fixedSubjects.stream()
                        .filter(s -> s.subjectName.equalsIgnoreCase(subjectName))
                        .findFirst()
                        .orElse(null));
    }


}

