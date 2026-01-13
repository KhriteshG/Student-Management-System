import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// This is the Student class to store each student's info
class Student {
    int number;
    String surname, otherNames;

    public Student(int number, String surname, String otherNames) {
        this.number = number;
        this.surname = surname;
        this.otherNames = otherNames;
    }
}

// Node class for linked list
class Node {
    Student data;
    Node next;

    public Node(Student data) {
        this.data = data;
        this.next = null;
    }
}

// Custom Linked List to store students
class StudentLinkedList {
    private Node head;

    // add a student to the list
    public void addStudent(Student student) {
        if (head == null) {
            head = new Node(student); // if list is empty, new head
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next; // go to end
            }
            current.next = new Node(student); // add new student
        }
    }

    // update existing student info
    public boolean updateStudent(int number, String surname, String otherNames) {
        Node current = head;
        while (current != null) {
            if (current.data.number == number) {
                current.data.surname = surname;
                current.data.otherNames = otherNames;
                return true; // updated
            }
            current = current.next;
        }
        return false; // not found
    }

    // delete a student by number
    public boolean deleteStudent(int number) {
        if (head == null) return false;
        if (head.data.number == number) {
            head = head.next;
            return true;
        }
        Node current = head;
        while (current.next != null) {
            if (current.next.data.number == number) {
                current.next = current.next.next;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // sort list based on the selected field
    public void sortListBy(String field) {
        if (head == null || head.next == null) return;
        boolean swapped;
        do {
            swapped = false;
            Node current = head;
            while (current.next != null) {
                boolean shouldSwap = false;
                switch (field) {
                    case "surname":
                        shouldSwap = current.data.surname.compareToIgnoreCase(current.next.data.surname) > 0;
                        break;
                    case "number":
                        shouldSwap = current.data.number > current.next.data.number;
                        break;
                    case "otherNames":
                        shouldSwap = current.data.otherNames.compareToIgnoreCase(current.next.data.otherNames) > 0;
                        break;
                }
                if (shouldSwap) {
                    Student temp = current.data;
                    current.data = current.next.data;
                    current.next.data = temp;
                    swapped = true;
                }
                current = current.next;
            }
        } while (swapped);
    }

    // return all students as list
    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    // search by number, surname, or other names
    public ArrayList<Student> searchStudent(String keyword) {
        ArrayList<Student> result = new ArrayList<>();
        Node current = head;
        while (current != null) {
            if (String.valueOf(current.data.number).contains(keyword) ||
                    current.data.surname.toLowerCase().contains(keyword.toLowerCase()) ||
                    current.data.otherNames.toLowerCase().contains(keyword.toLowerCase())) {
                result.add(current.data);
            }
            current = current.next;
        }
        return result;
    }
}

// main application window
public class StudentManagerApp extends JFrame {
    private StudentLinkedList studentList = new StudentLinkedList();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField numberField, surnameField, otherNamesField, searchField;

    public StudentManagerApp() {
        setTitle("Student Manager App");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // trying to make it look nice
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("Table.rowHeight", 24);
            UIManager.put("Label.font", new Font("SansSerif", Font.BOLD, 13));
            UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
            UIManager.put("TitledBorder.font", new Font("SansSerif", Font.BOLD, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Info"));

        numberField = new JTextField();
        surnameField = new JTextField();
        otherNamesField = new JTextField();
        searchField = new JTextField();

        inputPanel.add(new JLabel("Number:"));
        inputPanel.add(numberField);
        inputPanel.add(new JLabel("Surname:"));
        inputPanel.add(surnameField);
        inputPanel.add(new JLabel("Other Names:"));
        inputPanel.add(otherNamesField);
        inputPanel.add(new JLabel("Search:"));
        inputPanel.add(searchField);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Number", "Surname", "Other Names"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // not editable
            }
        };

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton sortBtn = new JButton("Sort");
        JButton listBtn = new JButton("List All");
        JButton clearBtn = new JButton("Clear");

        JComboBox<String> sortCombo = new JComboBox<>(new String[]{"Sort by Surname", "Sort by Number", "Sort by Other Names"});

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(sortCombo);
        buttonPanel.add(sortBtn);
        buttonPanel.add(listBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        Runnable clearInputFields = () -> {
            numberField.setText("");
            surnameField.setText("");
            otherNamesField.setText("");
            searchField.setText("");
            table.clearSelection();
        };

        // add button action
        addBtn.addActionListener(e -> {
            try {
                String numberText = numberField.getText().trim();
                if (!numberText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Student number must be a positive integer");
                    return;
                }
                int num = Integer.parseInt(numberText);
                String surname = surnameField.getText().trim();
                String otherNames = otherNamesField.getText().trim();

                if (surname.isEmpty() || otherNames.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Surname and Other Names cannot be empty");
                    return;
                }

                if (hasDuplicate(num, surname, otherNames, false)) {
                    JOptionPane.showMessageDialog(this, "Duplicate student number or name exists");
                    return;
                }

                System.out.println("Adding: " + surname); // for debug
                studentList.addStudent(new Student(num, surname, otherNames));
                refreshTable(studentList.getAllStudents());
                clearInputFields.run();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding student");
            }
        });

        // update button action
        updateBtn.addActionListener(e -> {
            try {
                String numberText = numberField.getText().trim();
                if (!numberText.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Student number must be a positive integer");
                    return;
                }
                int num = Integer.parseInt(numberText);
                String surname = surnameField.getText().trim();
                String otherNames = otherNamesField.getText().trim();

                if (surname.isEmpty() || otherNames.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Surname and Other Names cannot be empty");
                    return;
                }

                ArrayList<Student> all = studentList.getAllStudents();
                for (Student s : all) {
                    if (s.number != num) {
                        if (s.number == num) {
                            JOptionPane.showMessageDialog(this, "Duplicate student number exists");
                            return;
                        }
                        if (s.surname.equalsIgnoreCase(surname) && s.otherNames.equalsIgnoreCase(otherNames)) {
                            JOptionPane.showMessageDialog(this, "Duplicate student name exists");
                            return;
                        }
                    }
                }

                if (studentList.updateStudent(num, surname, otherNames)) {
                    refreshTable(studentList.getAllStudents());
                    clearInputFields.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Student not found");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating student");
            }
        });

        // delete button action
        deleteBtn.addActionListener(e -> {
            try {
                int num = Integer.parseInt(numberField.getText().trim());
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete student number " + num + "?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (studentList.deleteStudent(num)) {
                        refreshTable(studentList.getAllStudents());
                        clearInputFields.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "Student not found");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        });

        // sort button
        sortBtn.addActionListener(e -> {
            String criteria = (String) sortCombo.getSelectedItem();
            switch (criteria) {
                case "Sort by Surname":
                    studentList.sortListBy("surname");
                    break;
                case "Sort by Number":
                    studentList.sortListBy("number");
                    break;
                case "Sort by Other Names":
                    studentList.sortListBy("otherNames");
                    break;
            }
            refreshTable(studentList.getAllStudents());
        });

        listBtn.addActionListener(e -> {
            refreshTable(studentList.getAllStudents());
            searchField.setText("");
        });

        clearBtn.addActionListener(e -> clearInputFields.run());

        // real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void insertUpdate(DocumentEvent e) { search(); }

            private void search() {
                String keyword = searchField.getText().trim();
                refreshTable(keyword.isEmpty() ? studentList.getAllStudents() : studentList.searchStudent(keyword));
            }
        });

        // pressing Enter also triggers search
        searchField.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            refreshTable(keyword.isEmpty() ? studentList.getAllStudents() : studentList.searchStudent(keyword));
        });

        // click row to fill form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                numberField.setText(table.getValueAt(row, 0).toString());
                surnameField.setText(table.getValueAt(row, 1).toString());
                otherNamesField.setText(table.getValueAt(row, 2).toString());
            }
        });
    }

    // check for duplicate entry
    private boolean hasDuplicate(int number, String surname, String otherNames, boolean isUpdate) {
        ArrayList<Student> all = studentList.getAllStudents();
        for (Student s : all) {
            if (!isUpdate && s.number == number) return true;
            if (s.surname.equalsIgnoreCase(surname) && s.otherNames.equalsIgnoreCase(otherNames)) return true;
        }
        return false;
    }

    // update table rows
    private void refreshTable(ArrayList<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[]{s.number, s.surname, s.otherNames});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagerApp().setVisible(true));
    }
}

