import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
public class viewstatus extends JFrame {
	private JTextField idField;
    private JTextArea resultArea;
    private JButton checkButton;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/story";
    private static final String USER = "sebin";
    private static final String PASSWORD = "sebin@sql"; 

    public viewstatus() {
        setTitle("View Application Status");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(new JLabel("Enter Application ID:"));
        idField = new JTextField(10);
        topPanel.add(idField);
        checkButton = new JButton("Check Status");
        topPanel.add(checkButton);
        add(topPanel, BorderLayout.NORTH);

        // Text Area for showing result
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Button Action
        checkButton.addActionListener(e -> checkStatus());

        setVisible(true);
    }

    private void checkStatus() {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Application ID.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);

            // Connect to database
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT name, story_line, appointment_date, appointment_time FROM submissions WHERE id = ?";
                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setInt(1, id);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String storyLine = rs.getString("story_line");
                            Date date = rs.getDate("appointment_date");
                            Time time = rs.getTime("appointment_time");

                            StringBuilder status = new StringBuilder();
                            status.append("Name: ").append(name).append("\n");
                            status.append("Story Line: ").append(storyLine).append("\n\n");

                            if (date != null && time != null) {
                                status.append("Appointment Date: ").append(date).append("\n");
                                status.append("Appointment Time: ").append(time).append("\n");
                                status.append("\n Status: Appointment Scheduled");
                            } else {
                                status.append("Status: Pending — Appointment not yet assigned.");
                            }

                            resultArea.setText(status.toString());
                        } else {
                            resultArea.setText(" No record found for Application ID: " + id);
                        }
                    }
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format! Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(viewstatus::new);
    }
}


