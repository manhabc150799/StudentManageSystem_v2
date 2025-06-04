import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ExcelUtil {
    public static void writeClassSectionsToExcel(List<ClassSection> classSections, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ClassSections");

        // Header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Class Section ID");
        header.createCell(1).setCellValue("Subject Code");
        header.createCell(2).setCellValue("Subject Name");
        header.createCell(3).setCellValue("Semester");
        header.createCell(4).setCellValue("Lecturer");
        header.createCell(5).setCellValue("Max Capacity");
        header.createCell(6).setCellValue("Schedule");
        header.createCell(7).setCellValue("Current Enrollment");

        int rowNum = 1;
        for (ClassSection cs : classSections) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cs.classSectionId);
            row.createCell(1).setCellValue(cs.subject.subjectCode);
            row.createCell(2).setCellValue(cs.subject.subjectName);
            row.createCell(3).setCellValue(cs.semeter);
            row.createCell(4).setCellValue(cs.lecturer);
            row.createCell(5).setCellValue(cs.maxCapacity);
            row.createCell(6).setCellValue(getScheduleText(cs.schedules));
            row.createCell(7).setCellValue(cs.enrolledStudents.size());
        }

        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private static String getScheduleText(List<Schedule> schedules) {
        StringBuilder scheduleText = new StringBuilder();
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
        if (scheduleText.length() > 2) {
            scheduleText.setLength(scheduleText.length() - 2);
        }
        return scheduleText.toString();
    }

    /**
     * Reads class section information from an Excel file. If the file does not
     * exist an empty list is returned.
     */
    public static List<ClassSection> readClassSectionsFromExcel(String filePath) throws IOException {
        List<ClassSection> sections = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return sections;
        }

        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String classSectionId = row.getCell(0).getStringCellValue();
                String subjectCode = row.getCell(1).getStringCellValue();
                String subjectName = row.getCell(2).getStringCellValue();
                String semester = row.getCell(3).getStringCellValue();
                String lecturer = row.getCell(4).getStringCellValue();
                int maxCapacity = (int) row.getCell(5).getNumericCellValue();
                String scheduleText = row.getCell(6).getStringCellValue();

                Subject subject = findSubject(subjectCode, subjectName);
                List<Schedule> schedules = parseScheduleText(scheduleText);

                sections.add(new ClassSection(classSectionId, subject, semester, lecturer, maxCapacity, schedules));
            }
        }
        return sections;
    }

    private static Subject findSubject(String code, String name) {
        return Stream.concat(Main.creditSubjects.stream(), Main.fixedSubjects.stream())
                .filter(s -> s.subjectCode.equalsIgnoreCase(code) || s.subjectName.equalsIgnoreCase(name))
                .findFirst()
                .orElse(new CreditSubject(code, name, 0, "", new ArrayList<>(), "", "", 0, 0, 0));
    }

    private static List<Schedule> parseScheduleText(String text) {
        List<Schedule> list = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return list;
        }
        String[] parts = text.split(",\s*");
        for (String part : parts) {
            try {
                String day = part.substring(0, part.indexOf(' '));
                String rest = part.substring(part.indexOf(' ') + 1);
                String start = rest.substring(0, rest.indexOf('-'));
                String rest2 = rest.substring(rest.indexOf('-') + 1);
                String end = rest2.substring(0, rest2.indexOf(' '));
                String room = rest2.substring(rest2.indexOf('(') + 1, rest2.indexOf(')'));
                list.add(new Schedule(day, start, end, room));
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    /**
     * Writes the provided student list to an Excel file.
     */
    public static void writeStudentsToExcel(List<Student> students, String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("User ID");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Password");
        header.createCell(3).setCellValue("Full Name");
        header.createCell(4).setCellValue("Role");
        header.createCell(5).setCellValue("Student ID");
        header.createCell(6).setCellValue("Major");

        int rowNum = 1;
        for (Student s : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.getUserId());
            row.createCell(1).setCellValue(s.getEmail());
            row.createCell(2).setCellValue(s.getPassword());
            row.createCell(3).setCellValue(s.getFullName());
            String role = (s instanceof CreditStudent) ? "CreditStudent" :
                    (s instanceof YearBasedStudent) ? "YearBasedStudent" : s.getRole();
            row.createCell(4).setCellValue(role);
            row.createCell(5).setCellValue(s.studentId);
            row.createCell(6).setCellValue(s.major);
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    /**
     * Reads student information from an Excel file previously written by
     * {@link #writeStudentsToExcel(List, String)}. If the file is missing,
     * an empty list is returned.
     */
    public static List<Student> readStudentsFromExcel(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return students;
        }

        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String userId = row.getCell(0).getStringCellValue();
                String email = row.getCell(1).getStringCellValue();
                String password = row.getCell(2).getStringCellValue();
                String fullName = row.getCell(3).getStringCellValue();
                String role = row.getCell(4).getStringCellValue();
                String studentId = row.getCell(5).getStringCellValue();
                String major = row.getCell(6).getStringCellValue();

                boolean status = true;

                if ("YearBasedStudent".equalsIgnoreCase(role)) {
                    students.add(new YearBasedStudent(userId, email, password, fullName,
                            role, status, "", studentId, major));
                } else {
                    students.add(new CreditStudent(userId, email, password, fullName,
                            role, status, "", studentId, major));
                }
            }
        }

        return students;
    }
}