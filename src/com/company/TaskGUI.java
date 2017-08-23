package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskGUI extends JDialog implements ActionListener{
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField dateField;
    private JCheckBox completeSw;

    private JButton okButton;

    private boolean edit;
    private Task task;
    private int width = 400;
    private int height = 400;

    public TaskGUI(Frame owner, String title, ModalityType modalityType, Boolean edit, Task task) {
        super(owner, title, modalityType);
        this.edit = edit;
        this.task = task;
        prepareGUI();
    }

    private void prepareGUI() {
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(width, height));

        initComponents();

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Title: "));
        panel.add(titleField);
        panel.add(new JLabel("Description: "));
        panel.add(descriptionField);
        panel.add(new JLabel("Due Date: "));
        panel.add(new JLabel());
        panel.add(new JLabel("Title: "));
    }

    private void initComponents() {
        titleField = new JTextField(100);
        descriptionField = new JTextField(100);
        dateField = new JTextField(100);
        completeSw = new JCheckBox("Complete");

        if(edit) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
            dateField.setText(task.getDueDate().toString());
            completeSw.setSelected(task.getComplete());
        }

        okButton = new JButton("OK");
        okButton.addActionListener(this);
    }

    private Boolean checkDateFormat(String format) {
        return Boolean.TRUE;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == okButton) {

        }
    }
}
