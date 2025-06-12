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
        header.createCell(4).setCellValue("Lecturers");
        header.createCell(5).setCellValue("Max Lecturers");
        header.createCell(6).setCellValue("Max Capacity");
        header.createCell(7).setCellValue("Schedule");
        header.createCell(8).setCellValue("Current Enrollment");
        header.createCell(9).setCellValue("Requirement");
        header.createCell(10).setCellValue("YearBasedAuto");

        int rowNum = 1;
        for (ClassSection cs : classSections) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(cs.classSectionId);
            row.createCell(1).setCellValue(cs.subject.subjectCode);
            row.createCell(2).setCellValue(cs.subject.subjectName);
            row.createCell(3).setCellValue(cs.semeter);
            row.createCell(4).setCellValue(String.join(",", cs.lecturerIds));
            row.createCell(5).setCellValue(cs.maxLecturers);
            row.createCell(6).setCellValue(cs.maxCapacity);
            row.createCell(7).setCellValue(getScheduleText(cs.schedules));
            row.createCell(8).setCellValue(cs.enrolledStudents.size());
            row.createCell(9).setCellValue(cs.requirement == null ? "" : cs.requirement.subjectCode);
            row.createCell(10).setCellValue(cs.yearBasedAuto);
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
                String lecturerStr = row.getCell(4).getStringCellValue();
                int maxLecturers;
                int maxCapacity;
                String scheduleText;
                String requirementCode = "";
                if (row.getLastCellNum() >= 8) {
                    maxLecturers = (int) row.getCell(5).getNumericCellValue();
                    maxCapacity = (int) row.getCell(6).getNumericCellValue();
                    scheduleText = row.getCell(7).getStringCellValue();
                    if (row.getLastCellNum() > 9 && row.getCell(9) != null) {
                        requirementCode = row.getCell(9).getStringCellValue();
                    }
                } else {
                    maxLecturers = 1;
                    maxCapacity = (int) row.getCell(5).getNumericCellValue();
                    scheduleText = row.getCell(6).getStringCellValue();
                }

                Subject subject = findSubject(subjectCode, subjectName);
                Subject requirement = requirementCode.isEmpty() ? null : findSubject(requirementCode, requirementCode);
                List<Schedule> schedules = parseScheduleText(scheduleText);

                ClassSection cs = new ClassSection(classSectionId, subject, semester,
                        maxLecturers, maxCapacity, schedules, requirement);
                if (row.getLastCellNum() > 10 && row.getCell(10) != null) {
                    cs.yearBasedAuto = row.getCell(10).getBooleanCellValue();
                }
                if (lecturerStr != null && !lecturerStr.isEmpty()) {
                    for (String id : lecturerStr.split(",")) {
                        cs.lecturerIds.add(id.trim());
                    }
                }
                sections.add(cs);
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
        header.createCell(5).setCellValue("Status");
        header.createCell(6).setCellValue("DOB");
        header.createCell(7).setCellValue("Student ID");
        header.createCell(8).setCellValue("Major");
        header.createCell(9).setCellValue("Enrolled Classes");

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
            row.createCell(5).setCellValue(s.isStatus());
            row.createCell(6).setCellValue(s.getDob());
            row.createCell(7).setCellValue(s.studentId);
            row.createCell(8).setCellValue(s.major);
            row.createCell(9).setCellValue(String.join(",", s.getEnrolledClassIds()));
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
                boolean status = row.getCell(5).getBooleanCellValue();
                String dob = row.getCell(6).getStringCellValue();
                String studentId = row.getCell(7).getStringCellValue();
                String major = row.getCell(8).getStringCellValue();
                String enrolled = row.getLastCellNum() > 9 && row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "";

                Student student;
                if ("YearBasedStudent".equalsIgnoreCase(role)) {
                    student = new YearBasedStudent(userId, email, password, fullName,
                            role, status, dob, studentId, major);
                } else {
                    student = new CreditStudent(userId, email, password, fullName,
                            role, status, dob, studentId, major);
                }
                if (!enrolled.isEmpty()) {
                    for (String id : enrolled.split(",")) {
                        ((Student) student).addEnrolledClass(id.trim());
                    }
                }

                students.add(student);
            }
        }

        return students;
    }

    /**
     * Writes lecturer information to an Excel file.
     */
    public static void writeLecturersToExcel(List<Lecturer> lecturers, String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lecturers");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("User ID");
        header.createCell(1).setCellValue("Email");
        header.createCell(2).setCellValue("Password");
        header.createCell(3).setCellValue("Full Name");
        header.createCell(4).setCellValue("Role");
        header.createCell(5).setCellValue("Status");
        header.createCell(6).setCellValue("DOB");
        header.createCell(7).setCellValue("Lecturer ID");
        header.createCell(8).setCellValue("Department");
        header.createCell(9).setCellValue("Assigned Classes");

        int rowNum = 1;
        for (Lecturer l : lecturers) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(l.getUserId());
            row.createCell(1).setCellValue(l.getEmail());
            row.createCell(2).setCellValue(l.getPassword());
            row.createCell(3).setCellValue(l.getFullName());
            row.createCell(4).setCellValue(l.getRole());
            row.createCell(5).setCellValue(l.isStatus());
            row.createCell(6).setCellValue(l.getDob());
            row.createCell(7).setCellValue(l.getLecturerId());
            row.createCell(8).setCellValue(l.getDepartment());
            row.createCell(9).setCellValue(String.join(",", l.getAssignedClassIds()));
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    /**
     * Reads lecturer information from an Excel file. Returns an empty list if the file does not exist.
     */
    public static List<Lecturer> readLecturersFromExcel(String filePath) throws IOException {
        List<Lecturer> lecturers = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return lecturers;
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
                boolean status = row.getCell(5).getBooleanCellValue();
                String dob = row.getCell(6).getStringCellValue();
                String lecturerId = row.getCell(7).getStringCellValue();
                String department = row.getCell(8).getStringCellValue();
                String assigned = row.getLastCellNum() > 9 && row.getCell(9) != null ? row.getCell(9).getStringCellValue() : "";

                Lecturer lecturer = new Lecturer(userId, email, password, fullName, role, status, dob, lecturerId, department);
                if (!assigned.isEmpty()) {
                    for (String id : assigned.split(",")) {
                        lecturer.addAssignedClass(id.trim());
                    }
                }
                lecturers.add(lecturer);
            }
        }

        return lecturers;
    }

    /** Writes grades for the given class section to an Excel file within the specified directory. */
    public static void writeGradesToExcel(ClassSection cs, String dirPath) throws IOException {
        File dir = new File(dirPath);
        dir.mkdirs();
        File file = new File(dir, cs.classSectionId + "_grades.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Grades");
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Student ID");
        header.createCell(1).setCellValue("Midterm");
        header.createCell(2).setCellValue("Final");
        header.createCell(3).setCellValue("CPA");

        int rowNum = 1;
        for (Student s : cs.enrolledStudents) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(s.studentId);
            row.createCell(1).setCellValue(cs.getMidtermScore(s.studentId));
            row.createCell(2).setCellValue(cs.getFinalScore(s.studentId));
            row.createCell(3).setCellValue(cs.calculateCPA(s.studentId));
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    /** Loads grades from Excel into the provided class section if a file exists. */
    public static void readGradesFromExcel(ClassSection cs, String dirPath) throws IOException {
        File file = new File(dirPath, cs.classSectionId + "_grades.xlsx");
        if (!file.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String sid = row.getCell(0).getStringCellValue();
                float mid = (float) row.getCell(1).getNumericCellValue();
                float fin = (float) row.getCell(2).getNumericCellValue();
                cs.setMidtermScore(sid, mid);
                cs.setFinalScore(sid, fin);
            }
        }
    }
}