import javax.swing.*;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;



public class Main {
	public static ArrayList<Subject> creditSubjects = new ArrayList<Subject>();
	public static ArrayList<Subject> fixedSubjects = new ArrayList<Subject>();
	
	public static ArrayList<CreditStudent> creditStudents = new ArrayList<CreditStudent>();
    public static ArrayList<YearBasedStudent> yearBasedStudents = new ArrayList<YearBasedStudent>();
    
    public static ArrayList<Subject> readcreditSubjectsFromExcel(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
               XSSFWorkbook workbook = new XSSFWorkbook(fis)) { // Sửa: Sử dụng Workbook thay vì XSSFWorkbook
               XSSFSheet sheet = workbook.getSheetAt(0); 

               for (Row row : sheet) { 
                   if (row.getRowNum() == 0) {
                       continue; // Bỏ qua hàng tiêu đề
                   }

                   String subjectCode = row.getCell(0).getStringCellValue();
                   String subjectName = row.getCell(1).getStringCellValue();
                   int creditHours = (int) row.getCell(2).getNumericCellValue();
                   String subjectType = row.getCell(3).getStringCellValue();
                   String prerequisitecreditSubjectsString = row.getCell(4).getStringCellValue();
                   String responsibleDepartment = row.getCell(5).getStringCellValue();
                   String description = row.getCell(6).getStringCellValue();
                   int suggestSemester = (int) row.getCell(7).getNumericCellValue();
                   int midtermWeight = (int) row.getCell(8).getNumericCellValue();
                   int finalWeight = (int) row.getCell(9).getNumericCellValue();

                   // Xử lý danh sách môn học tiên quyết
                   ArrayList<Subject> prerequisitecreditSubjects = new ArrayList<Subject>();
                   if (!prerequisitecreditSubjectsString.equalsIgnoreCase("Không")) {	
                       String[] prerequisiteCodes = prerequisitecreditSubjectsString.split(",");
                       for (String code : prerequisiteCodes) {
                           // Tìm môn học có mã trùng khớp
                           Subject prerequisiteSubject = creditSubjects.stream()
                                   .filter(subject -> subject.subjectCode.equals(code))
                                   .findFirst()
                                   .orElse(null);

                           if (prerequisiteSubject != null) {
                               prerequisitecreditSubjects.add(prerequisiteSubject);
                           } else {
                               // Xử lý trường hợp không tìm thấy môn học (ví dụ: in ra thông báo lỗi)
                               System.out.println("Không tìm thấy môn học có mã: " + code);
                           }
                       }
                   }

                   CreditSubject subject = new CreditSubject(
                           subjectCode, subjectName, creditHours, subjectType,
                           prerequisitecreditSubjects, responsibleDepartment, description,
                           suggestSemester, midtermWeight, finalWeight
                   );
                   creditSubjects.add(subject);
               }
           }

           return creditSubjects;
       }
    
    public static void main(String[] args) throws IOException {
        
        ArrayList<User> users = new ArrayList<>();
        users.add(new Manager("M01", "1", "1", "Admin", "Manager", true, "1980-01-01", "MG123"));
        users.add(new Lecturer("L01", "lecturer@example.com", "password", "Lecturer B", "Lecturer", true, "1975-03-21","LT01", "KCNTT"));
        
        
        readcreditSubjectsFromExcel("C:\\Users\\LENOVO\\Downloads\\Subject.xlsx");
        ExcelUtil.readClassSectionsFromExcel("C:\\Users\\LENOVO\\Downloads\\ClassSections.xlsx");
        // Load students from Excel and add them to the system
        java.util.List<Student> students = ExcelUtil.readStudentsFromExcel(Manager.STUDENT_EXCEL_PATH);
        for (Student s : students) {
            users.add(s);
            if (s instanceof CreditStudent) {
                creditStudents.add((CreditStudent) s);
            } else if (s instanceof YearBasedStudent) {
                yearBasedStudents.add((YearBasedStudent) s);
            }
        }
        for(Subject sub : creditSubjects) {
        	    System.out.println(sub.toString());
        }
        
     // Gọi phương thức hiển thị giao diện đăng nhập
        showLoginScreen(users);
    }

    // Hàm hiển thị giao diện đăng nhập
    private static void showLoginScreen(ArrayList<User> users) {
        // Tạo JFrame cho màn hình đăng nhập
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(400, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLayout(new GridLayout(3, 2, 10, 10));

        // Tạo các thành phần giao diện
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JLabel messageLabel = new JLabel("", SwingConstants.CENTER);

        // Thêm các thành phần vào JFrame
        loginFrame.add(emailLabel);
        loginFrame.add(emailField);
        loginFrame.add(passwordLabel);
        loginFrame.add(passwordField);
        loginFrame.add(loginButton);
        loginFrame.add(messageLabel);

        // Thiết lập hành động khi nhấn nút "Login"
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                User user = findUserByEmail(users, email);
                if (user != null && user.authenticate(password)) {
                    JOptionPane.showMessageDialog(loginFrame, "Đăng nhập thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loginFrame.dispose(); 
                    // Đóng màn hình đăng nhập
                    // Hiển thị màn hình chính
                    switch (user.getRole().toLowerCase()) {
                        case "manager":
                            SwingUtilities.invokeLater(() -> new ManagerPanel((Manager) user));
                            break;
                        case "lecturer":
                            SwingUtilities.invokeLater(() -> showMainScreen());
                            break;
                        case "student":
                        case "creditstudent":
                            SwingUtilities.invokeLater(() -> new StudentPanel((Student) user));
                            break;
                        case "yearbasedstudent":
                            SwingUtilities.invokeLater(() -> new StudentPanel((Student) user));
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Role không hợp lệ!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    messageLabel.setText("Sai email hoặc mật khẩu!");
                    messageLabel.setForeground(Color.RED);
                }
            }
        });

        // Hiển thị JFrame
        loginFrame.setVisible(true);
    }

    // Hàm hiển thị màn hình chính
    private static void showMainScreen() {
        // Tạo JFrame cho màn hình chính
        JFrame mainFrame = new JFrame("Main Screen");
        mainFrame.setSize(400, 200);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());

        // Thêm nhãn "Coming soon..." vào màn hình chính
        JLabel comingSoonLabel = new JLabel("Coming soon...", SwingConstants.CENTER);
        comingSoonLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainFrame.add(comingSoonLabel, BorderLayout.CENTER);

        // Hiển thị JFrame
        mainFrame.setVisible(true);
    }

    // Hàm tìm người dùng theo email
    private static User findUserByEmail(ArrayList<User> users, String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return user;
            }
        }
        return null;
    }
}
