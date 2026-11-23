import javax.swing.*;
import java.awt.*;

public class LibrarySearchScreen extends JFrame {
    public LibrarySearchScreen() {
        super("Campus Library Search");
        setLayout(new BorderLayout(6, 6));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel title = new JLabel("CAMPUS DIGITAL LIBRARY CATALOG");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        titlePanel.add(title);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JTextField queryField = new JTextField(22);
        JComboBox<String> category = new JComboBox<>(new String[]{"All", "Computer Science", "Cybersecurity", "Electronics"});
        JButton searchBtn = new JButton("Search");
        searchRow.add(new JLabel("Keyword:"));
        searchRow.add(queryField);
        searchRow.add(new JLabel("Category:"));
        searchRow.add(category);
        searchRow.add(searchBtn);

        topPanel.add(titlePanel);
        topPanel.add(searchRow);
        add(topPanel, BorderLayout.NORTH);

        JTextArea resultsArea = new JTextArea("Enter a keyword and click Search to view library holdings...");
        resultsArea.setEditable(false);
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        JButton prevBtn = new JButton("Prev");
        JButton nextBtn = new JButton("Next");
        JButton reserveBtn = new JButton("Reserve");
        btnRow.add(prevBtn);
        btnRow.add(nextBtn);
        btnRow.add(reserveBtn);

        JLabel statusLabel = new JLabel(" Status: Ready");
        bottomPanel.add(btnRow, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
            resultsArea.setText("1. Computer Networking: A Top-Down Approach [" + category.getSelectedItem() + "]\n" +
                    "2. Applied Cryptography & Network Security [" + category.getSelectedItem() + "]");
            statusLabel.setText(" Status: Showing 2 matches for '" + queryField.getText() + "'");
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 450);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
