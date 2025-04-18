import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ExpenseSplitterSwingApp {
    private Connection conn;
    private JComboBox<String> tripComboBox;
    private Map<String, Integer> tripMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseSplitterSwingApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        try {
            conn = DatabaseManager.getConnection();
        } catch (SQLException e) {
            showMessage("Database Error", e.getMessage());
            return;
        }

        JFrame frame = new JFrame("Expense Splitter App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        applyDarkTheme();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add User", createStyledPanel(createAddUserPanel()));
        tabbedPane.addTab("Manage Trips", createStyledPanel(createTripPanel()));
        tabbedPane.addTab("Add Expense", createStyledPanel(createAddExpensePanel()));
        tabbedPane.addTab("Record Payment", createStyledPanel(createPaymentPanel()));
        tabbedPane.addTab("View Balances", createStyledPanel(createViewBalancesPanel()));
        tabbedPane.addTab("View Users", createStyledPanel(createViewUsersPanel())); // New tab for users

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);
    }

    private JPanel createStyledPanel(JPanel inner) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(new EmptyBorder(20, 20, 20, 20));
        outer.add(inner, BorderLayout.NORTH);
        outer.setBackground(Color.DARK_GRAY);
        return outer;
    }

    private void applyDarkTheme() {
        UIManager.put("Panel.background", Color.DARK_GRAY);
        UIManager.put("OptionPane.background", Color.BLACK);
        UIManager.put("OptionPane.messageForeground", Color.WHITE);           // text color
        UIManager.put("Label.foreground", Color.WHITE);                        // text color
        UIManager.put("TextField.background", Color.BLACK);                    // background
        UIManager.put("TextField.foreground", Color.WHITE);                    // text color
        UIManager.put("TextField.font", new Font("Dialog", Font.BOLD, 12));   // bold text
        UIManager.put("Button.background", Color.BLACK);
        UIManager.put("Button.foreground", Color.WHITE);                       // text color
        UIManager.put("Button.font", new Font("Dialog", Font.BOLD, 12));      // bold text
        UIManager.put("TextArea.background", Color.BLACK);                     // background
        UIManager.put("TextArea.foreground", Color.WHITE);                     // text color
        UIManager.put("TextArea.font", new Font("Dialog", Font.BOLD, 12));    // bold text
    }

    private JTextField styledTextField() {
        JTextField tf = new JTextField();
        tf.setForeground(Color.WHITE);                       // text color
        tf.setBackground(Color.BLACK);                       // background
        tf.setCaretColor(Color.WHITE);                       // caret color
        tf.setFont(new Font("Dialog", Font.BOLD, 12));       // bold text
        return tf;
    }

    private JPanel createTripPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setOpaque(false);
        JTextField tripNameField = styledTextField();
        JButton addTripBtn = new JButton("Add Trip");

        addTripBtn.addActionListener(e -> {
            try {
                String name = tripNameField.getText();
                String sql = "INSERT INTO trips (name) VALUES (?)";
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, name);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int tripId = rs.getInt(1);
                tripMap.put(name, tripId);
                tripComboBox.addItem(name);
                showMessage("Success", "Trip added.");
            } catch (SQLException ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(new JLabel("Trip Name:"));
        panel.add(tripNameField);
        panel.add(addTripBtn);

        return panel;
    }

    private JPanel createAddUserPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setOpaque(false);
        JTextField nameField = styledTextField();
        JTextField emailField = styledTextField();
        JButton addButton = new JButton("Add User");

        addButton.addActionListener(e -> {
            try {
                String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, nameField.getText());
                ps.setString(2, emailField.getText());
                ps.executeUpdate();
                showMessage("Success", "User  added.");
            } catch (SQLException ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(addButton);

        return panel;
    }

    private JPanel createAddExpensePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setOpaque(false);
        JTextField descField = styledTextField();
        JTextField amountField = styledTextField();
        JTextField payerField = styledTextField();
        JTextField participantsField = styledTextField();
        tripComboBox = new JComboBox<>();
        loadTrips();
        JButton addExpenseBtn = new JButton("Add Expense");

        addExpenseBtn.addActionListener(e -> {
            try {
                String desc = descField.getText();
                double amount = Double.parseDouble(amountField.getText());
                int payer = Integer.parseInt(payerField.getText());
                String[] ids = participantsField.getText().split(",");
                ArrayList<Integer> participants = new ArrayList<>();
                for (String id : ids) participants.add(Integer.parseInt(id.trim()));
                int tripId = tripMap.getOrDefault((String) tripComboBox.getSelectedItem(), 0);
                double share = amount / participants.size();

                String expenseSql = "INSERT INTO expenses (description, total, paid_by, trip_id) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(expenseSql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, desc);
                ps.setDouble(2, amount);
                ps.setInt(3, payer);
                ps.setInt(4, tripId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                int expenseId = rs.getInt(1);

                for (int uid : participants) {
                    String partSql = "INSERT INTO participants (expense_id, user_id, share) VALUES (?, ?, ?)";
                    PreparedStatement psPart = conn.prepareStatement(partSql);
                    psPart.setInt(1, expenseId);
                    psPart.setInt(2, uid);
                    psPart.setDouble(3, share);
                    psPart.executeUpdate();
                    if (uid != payer) updateBalance(uid, payer, share);
                }

                showMessage("Success", "Expense recorded.");
            } catch (Exception ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(new JLabel("Trip:"));
        panel.add(tripComboBox);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Payer ID:"));
        panel.add(payerField);
        panel.add(new JLabel("Participant IDs:"));
        panel.add(participantsField);
        panel.add(addExpenseBtn);

        return panel;
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setOpaque(false);
        JTextField fromField = styledTextField();
        JTextField toField = styledTextField();
        JTextField amountField = styledTextField();
        JButton payBtn = new JButton("Record Payment");

        payBtn.addActionListener(e -> {
            try {
                int from = Integer.parseInt(fromField.getText());
                int to = Integer.parseInt(toField.getText());
                double amt = Double.parseDouble(amountField.getText());

                String checkSql = "SELECT amount FROM balances WHERE from_user = ? AND to_user = ?";
                PreparedStatement ps = conn.prepareStatement(checkSql);
                ps.setInt(1, from);
                ps.setInt(2, to);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    double current = rs.getDouble("amount");
                    double newBal = current - amt;
                    if (newBal <= 0) {
                        String delete = "DELETE FROM balances WHERE from_user = ? AND to_user = ?";
                        PreparedStatement pd = conn.prepareStatement(delete);
                        pd.setInt(1, from);
                        pd.setInt(2, to);
                        pd.executeUpdate();
                    } else {
                        String update = "UPDATE balances SET amount = ? WHERE from_user = ? AND to_user = ?";
                        PreparedStatement pu = conn.prepareStatement(update);
                        pu.setDouble(1, newBal);
                        pu.setInt(2, from);
                        pu.setInt(3, to);
                        pu.executeUpdate();
                    }
                    showMessage("Success", "Payment recorded.");
                } else {
                    showMessage("Info", "No such balance exists.");
                }
            } catch (SQLException ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(new JLabel("From User ID:"));
        panel.add(fromField);
        panel.add(new JLabel("To User ID:"));
        panel.add(toField);
        panel.add(new JLabel("Amount Paid:"));
        panel.add(amountField);
        panel.add(payBtn);

        return panel;
    }

    private JPanel createViewBalancesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(Color.GRAY);
        resultArea.setForeground(Color.WHITE);
        resultArea.setCaretColor(Color.WHITE);
        JButton refresh = new JButton("Refresh Balances");

        refresh.addActionListener(e -> {
            try {
                String sql = "SELECT u1.name AS from_user, u2.name AS to_user, b.amount " +
                        "FROM balances b " +
                        "JOIN users u1 ON b.from_user = u1.user_id " +
                        "JOIN users u2 ON b.to_user = u2.user_id";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append(rs.getString("from_user"))
                    .append(" owes ")
                    .append(rs.getString("to_user"))
                    .append(": ₹")
                    .append(String.format("%.2f", rs.getDouble("amount")))
                    .append("\n");
                }
                resultArea.setText(sb.toString());
            } catch (SQLException ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(refresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createViewUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JTextArea userArea = new JTextArea();
        userArea.setEditable(false);
        userArea.setBackground(Color.GRAY);
        userArea.setForeground(Color.WHITE);
        userArea.setCaretColor(Color.WHITE);
        JButton loadUsersBtn = new JButton("Load Users");

        loadUsersBtn.addActionListener(e -> {
            try {
                String sql = "SELECT name, email FROM users";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("Name: ").append(rs.getString("name"))
                    .append(", Email: ").append(rs.getString("email"))
                    .append("\n");
                }
                userArea.setText(sb.toString());
            } catch (SQLException ex) {
                showMessage("Error", ex.getMessage());
            }
        });

        panel.add(loadUsersBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(userArea), BorderLayout.CENTER);
        return panel;
    }

    private void loadTrips() {
        try {
            String sql = "SELECT trip_id, name FROM trips";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("trip_id");
                tripMap.put(name, id);
                tripComboBox.addItem(name);
            }
        } catch (SQLException e) {
            showMessage("Error", "Failed to load trips.");
        }
    }

    private void updateBalance(int from, int to, double amount) throws SQLException {
        String checkSql = "SELECT amount FROM balances WHERE from_user = ? AND to_user = ?";
        PreparedStatement ps = conn.prepareStatement(checkSql);
        ps.setInt(1, from);
        ps.setInt(2, to);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            double current = rs.getDouble("amount");
            String updateSql = "UPDATE balances SET amount = ? WHERE from_user = ? AND to_user = ?";
            PreparedStatement psUpdate = conn.prepareStatement(updateSql);
            psUpdate.setDouble(1, current + amount);
            psUpdate.setInt(2, from);
            psUpdate.setInt(3, to);
            psUpdate.executeUpdate();
        } else {
            String insertSql = "INSERT INTO balances (from_user, to_user, amount) VALUES (?, ?, ?)";
            PreparedStatement psInsert = conn.prepareStatement(insertSql);
            psInsert.setInt(1, from);
            psInsert.setInt(2, to);
            psInsert.setDouble(3, amount);
            psInsert.executeUpdate();
        }
    }

    private void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}