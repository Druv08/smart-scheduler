package com.druv.scheduler.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.druv.scheduler.Database;

public class MockFrontend extends JFrame {
    private final DefaultTableModel usersModel;
    private final DefaultTableModel roomsModel;

    public MockFrontend() {
        super("Smart Scheduler - Mock Frontend");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Users Tab
        usersModel = new DefaultTableModel(new Object[]{"ID", "Username", "Role"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable usersTable = new JTable(usersModel);
        JPanel usersPanel = new JPanel(new BorderLayout());
        usersPanel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        usersPanel.add(createAddUserPanel(), BorderLayout.SOUTH);
        tabs.addTab("Users", usersPanel);

        // Rooms Tab
        roomsModel = new DefaultTableModel(new Object[]{"ID", "Room Name", "Capacity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable roomsTable = new JTable(roomsModel);
        JPanel roomsPanel = new JPanel(new BorderLayout());
        roomsPanel.add(new JScrollPane(roomsTable), BorderLayout.CENTER);
        roomsPanel.add(createAddRoomPanel(), BorderLayout.SOUTH);
        tabs.addTab("Rooms", roomsPanel);

        add(tabs);

        reloadUsers();
        reloadRooms();
    }

    private JPanel createAddUserPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField usernameField = new JTextField(12);
        JPasswordField passwordField = new JPasswordField(12);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"student", "faculty", "admin"});
        JButton addButton = new JButton("Add User");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        panel.add(addButton);

        addButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleBox.getSelectedItem();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            final String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.executeUpdate();
                usernameField.setText("");
                passwordField.setText("");
                reloadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JPanel createAddRoomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField nameField = new JTextField(14);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
        JButton addButton = new JButton("Add Room");

        panel.add(new JLabel("Room name:"));
        panel.add(nameField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacitySpinner);
        panel.add(addButton);

        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            int capacity = (Integer) capacitySpinner.getValue();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room name required", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            final String sql = "INSERT INTO rooms (room_name, capacity) VALUES (?, ?)";
            try (Connection conn = Database.connect();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, capacity);
                ps.executeUpdate();
                nameField.setText("");
                reloadRooms();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void reloadUsers() {
        usersModel.setRowCount(0);
        final String sql = "SELECT id, username, role FROM users ORDER BY id";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usersModel.addRow(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("role")});
            }
        } catch (SQLException e) {
            // surface to UI
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadRooms() {
        roomsModel.setRowCount(0);
        final String sql = "SELECT id, room_name, capacity FROM rooms ORDER BY id";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roomsModel.addRow(new Object[]{rs.getInt("id"), rs.getString("room_name"), rs.getInt("capacity")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


