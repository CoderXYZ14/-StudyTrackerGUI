/*
 * Author: Shahwaiz Islam
 * Copyright © 2023 https://github.com/CoderXYZ14
 */
import javax.swing.*;
import java.io.*;
import java.util.*;

class User {
    public String username;
    public String password;
    public int MAX_SUBJECTS = 10;
    public static int MAX_USERS = 100;
    public String[] enrolledSubjects = new String[MAX_SUBJECTS];

    public User(String uname, String pword) {
        username = uname;
        password = pword;
    }
    public User() {
    }

    public void addEnrolledSubject(String subject) {
        for (int i = 0; i < MAX_SUBJECTS; i++) {
            if (enrolledSubjects[i] == null || enrolledSubjects[i].isEmpty()) {
                enrolledSubjects[i] = subject;
                break;
            }
        }
    }
}

class UserManager {
    User userS = new User();
    private final User[] users = new User[userS.MAX_USERS];
    private User currentUser;
    private final String userFile = "users.txt";

    public UserManager() {
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            int userCount = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|\\|");
                users[userCount++] = new User(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
            for (int i = 0; i < userS.MAX_USERS && users[i] != null; i++) {
                writer.write("USER NAME : " + users[i].username + "||" + "PASSWORD : " + users[i].password + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registerUser(String uname, String pword) {
        for (int i = 0; i < userS.MAX_USERS; i++) {
            if (users[i] == null || users[i].username.isEmpty()) {
                users[i] = new User(uname, pword);
                saveUsers();
                JOptionPane.showMessageDialog(null, "Registration successful...");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Cannot register, maximum number of users reached.");
    }

    public boolean loginUser(String uname, String pword) {
        for (int i = 0; i < userS.MAX_USERS && users[i] != null && !users[i].username.isEmpty(); i++) {
            if (Objects.equals(users[i].username, uname) && Objects.equals(users[i].password, pword)) {
                JOptionPane.showMessageDialog(null, "Login successful. Welcome, " + uname + "!");
                currentUser = users[i];
                return true;
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid username or password. Login failed.");
        return false;
    }

    public void logoutUser() {
        currentUser = null;
        JOptionPane.showMessageDialog(null, "Logout successful.");
    }

    public boolean isUserLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

class Timer {
    private long startTime;
    private long stopTime;

    public void startTimer() {
        startTime = System.currentTimeMillis();
    }

    public void stopTimer() {
        stopTime = System.currentTimeMillis();
    }

    public int getElapsedTime() {
        return (int) (stopTime - startTime) / 1000;
    }
}

public class Main {
    private static final int MAX_USERS = 100;
    private static final int MAX_SUBJECTS = 10;

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        Timer timer = new Timer();

        while (true) {
            String input = JOptionPane.showInputDialog(
                    "MAIN MENU\n" +
                            "1. Register\n" +
                            "2. Login\n" +
                            "3. Logout\n" +
                            "4. Read Topics\n" +
                            "5. Exit"
            );

            if (input == null) {
                break; // User closed the input dialog
            }

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        registerUser(userManager);
                        break;
                    case 2:
                        loginUser(userManager);
                        break;
                    case 3:
                        userManager.logoutUser();
                        break;
                    case 4:
                        readTopics(userManager, timer);
                        break;
                    case 5:
                        JOptionPane.showMessageDialog(null, "Exiting...");
                        return;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a number.");
            }
        }
    }

    private static void registerUser(UserManager userManager) {
        String uname = JOptionPane.showInputDialog("Enter username:");
        String pword = JOptionPane.showInputDialog("Enter password:");
        userManager.registerUser(uname, pword);
    }

    private static void loginUser(UserManager userManager) {
        String uname = JOptionPane.showInputDialog("Enter username:");
        String pword = JOptionPane.showInputDialog("Enter password:");
        userManager.loginUser(uname, pword);
    }

    private static void readTopics(UserManager userManager, Timer timer) {
        if (userManager == null || !userManager.isUserLoggedIn()) {
            JOptionPane.showMessageDialog(null, "Please login first.");
            return;
        }

        String subject = JOptionPane.showInputDialog("Enter Subject:");
        userManager.getCurrentUser().addEnrolledSubject(subject);

        String topic = JOptionPane.showInputDialog("Enter Topic:");

        JOptionPane.showMessageDialog(null, "Timer Started. Press OK to stop.");

        timer.startTimer();

        JOptionPane.showInputDialog("Press OK to stop");

        timer.stopTimer();

        int elapsedTime = timer.getElapsedTime();
        JOptionPane.showMessageDialog(null, "You spent " + elapsedTime + " seconds on " + subject + ": " + topic + ".");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("history.txt", true))) {
            User user = userManager.getCurrentUser();
            writer.write("User: " + user.username + " || Subject: " + subject + " || Topic: " + topic
                    + " || Time Spent: " + elapsedTime + " seconds\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

