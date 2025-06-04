import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ClassSectionTableModel extends AbstractTableModel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[] columnNames = {"Class Section ID", "Subject", "Semester", "Lecturer", "Max Capacity"};
    private List<ClassSection> classSections;

    public ClassSectionTableModel(List<ClassSection> classSections) {
        this.classSections = classSections;
    }

    @Override
    public int getRowCount() {
        return classSections.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ClassSection classSection = classSections.get(rowIndex);
        switch (columnIndex) {
            case 0: return classSection.getClassSectionId();
            case 1: return classSection.getSubject().subjectName; 
            case 2: return classSection.getSemeter();
            case 3: return classSection.getLecturer();
            case 4: return classSection.getMaxCapacity();
            default: return null;
        }
    }
}