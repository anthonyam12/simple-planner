package com.company;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SimplePlanner {
    public static JFrame mainFrame;
    static ArrayList<Task> tasks;
    static Task newTask;
    static Task editTask;

    private int width = 800;
    private int height = 800;

    public static void main(String[] args) {
	    SimplePlanner app = new SimplePlanner();
    }

    public SimplePlanner() {
        init();
        prepareGUI();
    }

    static void setNewTask(Task task) {
        newTask = task;
    }

    static void setUpdateTask(Task task) {
        editTask = task;
    }

    private void init() {
        DatabaseUtil.connect();
        tasks = DatabaseUtil.getAllTasks();
    }

    private void prepareGUI() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame, "Error setting application LookAndFeel.",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }
        Image icon = new ImageIcon("icon.png").getImage();

        mainFrame = new JFrame("Simple Planner");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(new Dimension(width, height));
        mainFrame.setLocation(300, 150);

        MainPanel panel = new MainPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainFrame.add(panel, BorderLayout.CENTER);

        mainFrame.setIconImage(icon);
        mainFrame.pack();
        mainFrame.setVisible(Boolean.TRUE);
    }
}
