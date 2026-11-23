import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Converter extends JFrame {
    private final JTextField amount = new JTextField(10);
    private final JComboBox<String> target = new JComboBox<>(new String[]{"USD", "EUR"});
    private final JLabel result = new JLabel(" ");

    private static double rate(String currency) {
        return currency.equals("USD") ? 0.012 : 0.011;
    }

    public Converter() {
        super("Currency Converter");
        setLayout(new FlowLayout());

        JButton convertButton = new JButton("Convert");
        add(new JLabel("INR"));
        add(amount);
        add(target);
        add(convertButton);
        add(result);

        ActionListener convertAction = e -> convert();
        convertButton.addActionListener(convertAction);
        amount.addActionListener(convertAction);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
    }

    private void convert() {
        try {
            double inr = Double.parseDouble(amount.getText().trim());
            if (inr < 0) {
                throw new NumberFormatException();
            }
            String curr = (String) target.getSelectedItem();
            String formatted = String.format("%.2f %s", inr * rate(curr), curr);
            result.setText(formatted);
            JOptionPane.showMessageDialog(this, "Converted Amount: " + formatted, "Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            result.setText("Invalid amount");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Converter().setVisible(true));
    }
}
