package com.company;

import com.sun.org.apache.xpath.internal.operations.Bool;
import jdk.nashorn.internal.scripts.JD;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainPanel extends JPanel implements ActionListener, ListSelectionListener{
    private JTable table;
    private DefaultTableModel model;
    RowFilter<TableModel, Integer> filter;
    TableRowSorter<TableModel> rowSorter;
    private String[] columns = {"Title", "Description", "Due Date", "Complete"};

    private JPanel buttonPanel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JCheckBox filterCompleteSw;
    private JTextArea detailTextArea;

    ArrayList<Task> hiddenTasks = new ArrayList<>();

    public MainPanel() {
        init();
        buildGUI();
    }

    private void init() {
        setLayout(new BorderLayout());
    }

    private void buildGUI() {
        model = new DefaultTableModel(columns, 0) {
            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }
        };
        if(SimplePlanner.tasks != null && !SimplePlanner.tasks.isEmpty()) {
            for (Task task : SimplePlanner.tasks) {
                if (!task.getComplete()) {
                    model.addRow(new Object[]{task.getTitle(), task.getDescription(), task.getDueDate(), task.getComplete()});
                } else {
                    hiddenTasks.add(task);
                }
            }
        }

        table = new JTable(model);
        table.setFillsViewportHeight(Boolean.TRUE);
        JScrollPane tableScroll = new JScrollPane(table);
        add(tableScroll, BorderLayout.CENTER);

        buttonPanel = getButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        rowSorter = new TableRowSorter<>(table.getModel());
        filter = new RowFilter<TableModel, Integer>() {
            private final Integer CHECKBOX_COLUMN = 3;
            @Override
            public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
                int modelRow = entry.getIdentifier();
                Boolean checked = (Boolean)entry.getModel().getValueAt(modelRow, CHECKBOX_COLUMN);
                return checked;
            }
        };
        table.setAutoCreateRowSorter(Boolean.TRUE);
        // CHANGE color based on urgency
        table.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
        table.getSelectionModel().addListSelectionListener(this);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel getButtonPanel() {
        JPanel retPanel = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel();

        addButton = new JButton("Add");
        addButton.addActionListener(this);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        saveButton = new JButton("Save Changes");
        saveButton.addActionListener(this);

        buttons.add(addButton);
        buttons.add(deleteButton);
        buttons.add(saveButton);

        retPanel.add(buttons, BorderLayout.EAST);

        filterCompleteSw = new JCheckBox("Filter Complete");
        filterCompleteSw.setSelected(Boolean.TRUE);
        filterCompleteSw.addActionListener(this);
        retPanel.add(filterCompleteSw, BorderLayout.WEST);

        detailTextArea = new JTextArea(20, 50);
        detailTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        detailTextArea.setLineWrap(Boolean.TRUE);
        detailTextArea.setWrapStyleWord(Boolean.TRUE);
        detailTextArea.setEditable(Boolean.FALSE);
        JScrollPane detailPane = new JScrollPane(detailTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        retPanel.add(detailPane, BorderLayout.NORTH);
        retPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return retPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == addButton) {
            addTask();
        } else if (actionEvent.getSource() == saveButton) {
            save();
        } else if (actionEvent.getSource() == deleteButton) {
            deleteTask();
        } else if (actionEvent.getSource() == filterCompleteSw) {
            filterComplete();
        }
    }

    private void addTask() {
        // add row
        model.addRow(new Object[]{"", "", new Date(), Boolean.FALSE});
    }

    private void save() {
        // check for all dates being legitimate
        if (!validDueDates()) {
            JOptionPane.showMessageDialog(SimplePlanner.mainFrame, "Invalid date found.", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (valuesEmpty()) {
            JOptionPane.showMessageDialog(SimplePlanner.mainFrame,
                    "Enter valid title and description for all fields (non-empty).", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ArrayList<Task> writeTasks = new ArrayList<>();
        // fuck it, just delete all tasks in database and write new tasks
        // build new task list from table data
        DatabaseUtil.deleteAllTasks();
        for(int i = 0; i < model.getRowCount(); i++) {
            Task task = new Task(i, model.getValueAt(i, 0).toString(), model.getValueAt(i, 1).toString(),
                    new Date(model.getValueAt(i, 2).toString()), Boolean.parseBoolean(model.getValueAt(i, 3).toString()));
            writeTasks.add(task);
        }
        int j = 0;
        for(Task task : hiddenTasks) {
            task.setId(model.getRowCount() + j);
            j++;
        }
        writeTasks.addAll(hiddenTasks);
        DatabaseUtil.writeTasks(writeTasks);

        // in the future, just keep track of changed tasks and update, add, delete them here.
        // potentially finish the TaskGUI class to add and edit tasks and write to database
        // on closing that dialog.
    }

    private boolean valuesEmpty() {
        for(int i = 0; i < model.getRowCount(); i++) {
            if(model.getValueAt(i, 0).toString().isEmpty()|| model.getValueAt(i, 1).toString().isEmpty()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private boolean validDueDates() {
        for(int i = 0; i < model.getRowCount(); i++) {
            try {
                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                df.parse(model.getValueAt(i, 2).toString());
            } catch (ParseException e) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private void deleteTask() {
        model.removeRow(table.getSelectedRow());
    }

    private void filterComplete() {
        if(filterCompleteSw.isSelected()) {
            // loop through model and remove checked rows
            ArrayList<Integer> rowsToRemove = new ArrayList<>();
            for(int i = 0; i < model.getRowCount(); i++) {
                Boolean checked = Boolean.parseBoolean(model.getValueAt(i, 3).toString());
                if(checked && filterCompleteSw.isSelected()) {
                    Task task = new Task(i, model.getValueAt(i, 0).toString(), model.getValueAt(i, 1).toString(),
                            new Date(model.getValueAt(i, 2).toString()), Boolean.parseBoolean(model.getValueAt(i, 3).toString()));
                    hiddenTasks.add(task);
                    rowsToRemove.add(i);
                }
            }
            for(Integer index : rowsToRemove) {
                model.removeRow(index);
            }
        } else {
            // clear hidden list and add all hidden items to the model
            for(Task task : hiddenTasks) {
                model.addRow(new Object[]{task.getTitle(), task.getDescription(), task.getDueDate(), task.getComplete()});
            }
            hiddenTasks.clear();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        int row = table.getSelectedRow();
        if(row >= 0) {
            detailTextArea.setText(
                    "TITLE:\n\n" + model.getValueAt(row, 0).toString() +
                            "\n\n\nDESCRIPTION: \n\n" + model.getValueAt(row, 1).toString()
            );
        }
    }

    class CustomRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable jTable, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(jTable, value, isSelected, hasFocus, row, col);
            c.setBackground(new Color(1f, 1f, 1f));

            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
            Date event = new Date();
            try {
                event = df.parse(model.getValueAt(row, 2).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date today = new Date();
            long diffInMillies = event.getTime() - today.getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);

            if(diffInDays < 0) {
                return c;
            } else if (diffInDays <= 7 && diffInDays > 3) {
                c.setBackground(new Color(.9f, .9f, .1f));
            } else if (diffInDays <= 3) {
                c.setBackground(new Color(.9f, .1f, .1f));
            }
            return c;
        }
    }
}
