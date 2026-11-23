import javax.swing.*;
import java.awt.*;
import java.sql.*;

class FeeService {
    public String processPayment(String regNo, String rawAmount) throws Exception {
        if (regNo == null || regNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Register number is required.");
        }
        double amount;
        try {
            amount = Double.parseDouble(rawAmount.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Enter a valid numeric amount.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        String sql = "INSERT INTO fees (reg_no, amount) VALUES (?, ?)";
        try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/college", "root", "pwd");
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo.trim());
            ps.setDouble(2, amount);
            ps.executeUpdate();
            return "Receipt #RCP-" + (System.currentTimeMillis() % 10000) + " | Paid Rs." + amount + " for " + regNo.trim();
        }
    }
}

public class FeePaymentApp extends JFrame {
    private final JTextField regNoField = new JTextField(15);
    private final JTextField amountField = new JTextField(15);
    private final JLabel statusLabel = new JLabel("Enter details and click Pay.");
    private final FeeService feeService = new FeeService();
    private String lastReceipt = "No payment recorded in this session.";

    public FeePaymentApp() {
        super("Student Fee Payment");
        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(2, 2, 6, 6));
        form.add(new JLabel("Register Number:"));
        form.add(regNoField);
        form.add(new JLabel("Amount (INR):"));
        form.add(amountField);

        JButton payBtn = new JButton("Pay");
        JButton clearBtn = new JButton("Clear");
        JButton receiptBtn = new JButton("Receipt");
        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(payBtn);
        buttons.add(clearBtn);
        buttons.add(receiptBtn);

        add(form, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        payBtn.addActionListener(e -> {
            payBtn.setEnabled(false);
            statusLabel.setText("Processing payment...");
            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() throws Exception {
                    return feeService.processPayment(regNoField.getText(), amountField.getText());
                }

                @Override
                protected void done() {
                    payBtn.setEnabled(true);
                    try {
                        lastReceipt = get();
                        statusLabel.setText("SUCCESS: " + lastReceipt);
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        statusLabel.setText("ERROR: " + cause.getMessage());
                    }
                }
            }.execute();
        });

        clearBtn.addActionListener(e -> {
            regNoField.setText("");
            amountField.setText("");
            statusLabel.setText("Cleared.");
        });

        receiptBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, lastReceipt, "Fee Receipt", JOptionPane.INFORMATION_MESSAGE));

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FeePaymentApp().setVisible(true));
    }
}
