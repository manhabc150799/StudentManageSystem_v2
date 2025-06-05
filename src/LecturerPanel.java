import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class LecturerPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Lecturer lecturer;

    public LecturerPanel(Lecturer lecturer) {
        this.lecturer = lecturer;
        setTitle("Lecturer Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2));
        infoPanel.add(new JLabel("Full Name:"));
        infoPanel.add(new JLabel(lecturer.getFullName()));
        infoPanel.add(new JLabel("Lecturer ID:"));
        infoPanel.add(new JLabel(lecturer.getLecturerId()));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(lecturer.getEmail()));
        infoPanel.add(new JLabel("Department:"));
        infoPanel.add(new JLabel(lecturer.getDepartment()));
        infoPanel.add(new JLabel("Role:"));
        infoPanel.add(new JLabel(lecturer.getRole()));

        JButton assignButton = new JButton("Assign Class Section");
        JButton viewButton = new JButton("View Assigned Classes");
        JButton gradeButton = new JButton("Grade Students");

        assignButton.addActionListener(e -> showAssignDialog());
        viewButton.addActionListener(e -> showAssignedDialog());
        gradeButton.addActionListener(e -> showGradeDialog());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(assignButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(gradeButton);

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void showAssignDialog() {
        JDialog dialog = new JDialog(this, "Assign Class Section", true);
        dialog.setSize(500, 300);

        String[] columnNames = {"Class ID", "Subject", "Schedule", "Lecturers"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (ClassSection cs : Manager.classSections) {
            String scheduleText = getScheduleText(cs.schedules);
            model.addRow(new Object[]{cs.classSectionId, cs.subject.subjectName, scheduleText,
                    String.join(",", cs.lecturerIds)});
        }
        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton assignBtn = new JButton("Assign");
        assignBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                ClassSection cs = Manager.classSections.get(row);
                if (cs.lecturerIds.contains(lecturer.getLecturerId())) {
                    JOptionPane.showMessageDialog(dialog, "You are already assigned to this class.");
                } else if (cs.lecturerIds.size() >= cs.maxLecturers) {
                    JOptionPane.showMessageDialog(dialog, "Maximum lecturers reached.");
                } else {
                    cs.addLecturer(lecturer.getLecturerId());
                    lecturer.addAssignedClass(cs.classSectionId);
                    model.setValueAt(String.join(",", cs.lecturerIds), row, 3);
                    try {
                        ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
                        ExcelUtil.writeLecturersToExcel(Main.lecturers, Main.LECTURER_EXCEL_PATH);
                    } catch (Exception ex) {
                        System.err.println("Failed to save assignment: " + ex.getMessage());
                    }
                    JOptionPane.showMessageDialog(dialog, "Assigned successfully");
                }
            }
        });
        dialog.add(assignBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    private void showAssignedDialog() {
        JDialog dialog = new JDialog(this, "Assigned Classes", true);
        dialog.setSize(400, 300);

        String[] columnNames = {"Class ID", "Subject", "Schedule"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (String id : lecturer.getAssignedClassIds()) {
            ClassSection cs = Manager.classSections.stream()
                    .filter(c -> c.classSectionId.equals(id))
                    .findFirst()
                    .orElse(null);
            if (cs != null) {
                String scheduleText = getScheduleText(cs.schedules);
                model.addRow(new Object[]{cs.classSectionId, cs.subject.subjectName, scheduleText});
            }
        }
        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table));

        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String classId = (String) model.getValueAt(row, 0);
                ClassSection cs = Manager.classSections.stream()
                        .filter(c -> c.classSectionId.equals(classId))
                        .findFirst()
                        .orElse(null);
                if (cs != null && cs.lecturerIds.contains(lecturer.getLecturerId())) {
                    cs.removeLecturer(lecturer.getLecturerId());
                    lecturer.removeAssignedClass(classId);
                    model.removeRow(row);
                    try {
                        ExcelUtil.writeClassSectionsToExcel(Manager.classSections, Manager.CLASS_SECTION_EXCEL_PATH);
                        ExcelUtil.writeLecturersToExcel(Main.lecturers, Main.LECTURER_EXCEL_PATH);
                    } catch (Exception ex) {
                        System.err.println("Failed to save assignment: " + ex.getMessage());
                    }
                }
            }
        });
        dialog.add(removeBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    /** Dialog for entering grades */
    private void showGradeDialog() {
        JDialog dialog = new JDialog(this, "Enter Grades", true);
        dialog.setSize(600, 400);

        JComboBox<String> classCombo = new JComboBox<>();
        for (String id : lecturer.getAssignedClassIds()) {
            classCombo.addItem(id);
        }

        String[] columns = {"Student ID", "Full Name", "Midterm", "Final", "CPA"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        classCombo.addActionListener(e -> refreshGradeTable(model, (String) classCombo.getSelectedItem()));

        if (classCombo.getItemCount() > 0) {
            refreshGradeTable(model, (String) classCombo.getItemAt(0));
        }

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            String classId = (String) classCombo.getSelectedItem();
            ClassSection cs = Manager.classSections.stream()
                    .filter(c -> c.classSectionId.equals(classId))
                    .findFirst()
                    .orElse(null);
            if (cs != null) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    String sid = (String) model.getValueAt(i, 0);
                    float mid = Float.parseFloat(model.getValueAt(i, 2).toString());
                    float fin = Float.parseFloat(model.getValueAt(i, 3).toString());
                    cs.setMidtermScore(sid, mid);
                    cs.setFinalScore(sid, fin);
                    model.setValueAt(cs.calculateCPA(sid), i, 4);
                }
                try {
                    ExcelUtil.writeGradesToExcel(cs, Manager.GRADE_DIR_PATH);
                } catch (IOException ex) {
                    System.err.println("Failed to save grades: " + ex.getMessage());
                }
                JOptionPane.showMessageDialog(dialog, "Scores saved");
            }
        });

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Class:"), BorderLayout.WEST);
        top.add(classCombo, BorderLayout.CENTER);

        dialog.add(top, BorderLayout.NORTH);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void refreshGradeTable(DefaultTableModel model, String classId) {
        model.setRowCount(0);
        ClassSection cs = Manager.classSections.stream()
                .filter(c -> c.classSectionId.equals(classId))
                .findFirst()
                .orElse(null);
        if (cs != null) {
            for (Student s : cs.enrolledStudents) {
                model.addRow(new Object[]{s.studentId, s.getFullName(),
                        cs.getMidtermScore(s.studentId), cs.getFinalScore(s.studentId),
                        cs.calculateCPA(s.studentId)});
            }
        }
    }

    private String getScheduleText(List<Schedule> schedules) {
        StringBuilder text = new StringBuilder();
        for (Schedule s : schedules) {
            text.append(s.dayOfWeek)
                    .append(" ")
                    .append(s.startTime)
                    .append("-")
                    .append(s.endTime)
                    .append(" (")
                    .append(s.room)
                    .append("), ");
        }
        if (text.length() > 2) {
            text.setLength(text.length() - 2);
        }
        return text.toString();
    }
}
