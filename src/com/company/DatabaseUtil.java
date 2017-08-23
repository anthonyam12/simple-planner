package com.company;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by amorast on 8/22/17.
 */
public class DatabaseUtil {
    public static String ID = "ID";
    public static String TITLE = "TITLE";
    public static String DESCRIPTION = "DESCRIPTION";
    public static String DUE_DATE = "DUE_DATE";
    public static String COMPLETE_SW = "COMPLETE_SW";


    private static Connection conn = null;
    public static void connect() {
        try {
            String url = "jdbc:sqlite:task.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.print("Connection failed.");
        }
    }

    public static ArrayList<Task> getAllTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        String query = "SELECT * FROM TASKS;";
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }

            ResultSet rs;
            Statement st = conn.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                java.util.Date dt = df.parse(rs.getString(DUE_DATE));
                tasks.add(new Task(rs.getInt(ID), rs.getString(TITLE), rs.getString(DESCRIPTION), dt,
                        rs.getBoolean(COMPLETE_SW)));
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return tasks;
    }

    public static void deleteAllTasks() {
        String query = "DELETE FROM TASKS WHERE 1 = 1;";
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
            Statement st = conn.createStatement();
            st.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void writeTasks(ArrayList<Task> tasks) {
        try {
            if (conn == null || conn.isClosed()) {
                connect();
            }
            for(Task task : tasks) {
                String query = "INSERT INTO TASKS VALUES({id}, \'{title}\', \'{description}\', \'{due_date}\', {complete});";
                query = query.replace("{id}", task.getId().toString());
                query = query.replace("{title}", task.getTitle());
                query = query.replace("{description}", task.getDescription());
                query = query.replace("{due_date}", task.getDueDate().toString());
                query = query.replace("{complete}", Integer.toString(task.getComplete() ? 1 : 0));
                Statement st = conn.createStatement();
                st.execute(query);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
