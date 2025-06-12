import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import java.util.List;

public class StudentPanel extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Student student;

    public StudentPanel(Student student) {
        this.student = student;
        setTitle("Student Panel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2));
        infoPanel.add(new JLabel("Full Name:"));
        infoPanel.add(new JLabel(student.getFullName()));
        infoPanel.add(new JLabel("Student ID:"));
        infoPanel.add(new JLabel(student.studentId));
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(student.getEmail()));
        infoPanel.add(new JLabel("Major:"));
        infoPanel.add(new JLabel(student.major));
        infoPanel.add(new JLabel("Average CPA:"));
        infoPanel.add(new JLabel(String.format("%.2f", student.calculateAverageCPAFromClasses())));
        infoPanel.add(new JLabel("Validate Graduation:"));
        infoPanel.add(new JLabel(student.calculateGraduationProgress() + " / 120"));
        infoPanel.add(new JLabel("Eligible:"));
        infoPanel.add(new JLabel(student.validateGraduation() ? "Yes" : "No"));
        infoPanel.add(new JLabel("Role:"));
        infoPanel.add(new JLabel(student.getRole()));

        JButton enrollButton = new JButton("Enroll Class Section");
        JButton viewButton = new JButton("View Class Enrolled");


        JPanel buttonPanel = new JPanel();
        if (!(student instanceof YearBasedStudent)) {
            enrollButton.addActionListener(e -> showEnrollDialog());
            buttonPanel.add(enrollButton);
        }

        viewButton.addActionListener(e -> showEnrolledDialog());
        buttonPanel.add(viewButton);

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void showEnrollDialog() {
        JDialog dialog = new JDialog(this, "Enroll Class Section", true);
        dialog.setSize(500, 300);

        String[] columnNames = {"Class ID", "Subject", "Requirement", "Schedule", "Enrolled/Max"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (ClassSection cs : Manager.classSections) {
            String scheduleText = getScheduleText(cs.schedules);
            String status = cs.enrolledStudents.size() + "/" + cs.maxCapacity;
            String req = cs.requirement == null ? "" : cs.requirement.subjectName;
            model.addRow(new Object[]{cs.classSectionId, cs.subject.subjectName, req, scheduleText, status});
        }
        JTable table = new JTable(model);
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton enrollBtn = new JButton("Enroll");
        enrollBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                ClassSection cs = Manager.classSections.get(row);
                boolean ok = cs.addStudent(student);
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Enrolled successfully");
                    model.setValueAt(cs.enrolledStudents.size() + "/" + cs.maxCapacity, row, 4);
                } else {
                    String msg;
                    if (cs.enrolledStudents.size() >= cs.maxCapacity) {
                        msg = "Class is full";
                    } else if (cs.enrolledStudents.contains(student)) {
                        msg = "Already enrolled";
                    } else if (!cs.isStudentEligible(student)) {
                        msg = "Requirement not met";
                    } else {
                        msg = "Could not enroll";
                    }
                    JOptionPane.showMessageDialog(dialog, msg);
                }

            }
        });
        dialog.add(enrollBtn, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEnrolledDialog() {
        JDialog dialog = new JDialog(this, "Classes Enrolled", true);
        dialog.setSize(400, 300);

        String[] columnNames = {"Class ID", "Subject", "Schedule"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (String id : student.getEnrolledClassIds()) {
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
        if (!(student instanceof YearBasedStudent)) {
            JButton removeBtn = new JButton("Remove");
            removeBtn.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String classId = (String) model.getValueAt(row, 0);
                    ClassSection cs = Manager.classSections.stream()
                            .filter(c -> c.classSectionId.equals(classId))
                            .findFirst()
                            .orElse(null);
                    if (cs != null) {
                        boolean ok = cs.removeStudent(student);
                        JOptionPane.showMessageDialog(dialog,
                                ok ? "Removed successfully" : "Could not remove");
                        if (ok) {
                            model.removeRow(row);
                        }
                    }
                }
            });
            dialog.add(removeBtn, BorderLayout.SOUTH);
        }


        dialog.setVisible(true);
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

