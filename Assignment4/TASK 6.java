import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventRegistrationApp extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/campus_events";
    private static final String USER = "root", PASS = "password";

    private final JTextField regField = new JTextField(10);
    private final JTextField nameField = new JTextField(12);
    private final JTextField emailField = new JTextField(14);
    private final JComboBox<String> eventBox = new JComboBox<>(new String[]{"Hackathon", "RoboWars", "CodeDebug", "UI/UX Sprint"});
    private final JComboBox<String> filterBox = new JComboBox<>(new String[]{"ALL", "Hackathon", "RoboWars", "CodeDebug", "UI/UX Sprint"});
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Reg No", "Name", "Email", "Event"}, 0);
    private final JLabel statusLabel = new JLabel("Ready.");

    public EventRegistrationApp() {
        super("Campus Event Registration System");
        setLayout(new BorderLayout(8, 8));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export CSV");
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exportItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton regBtn = new JButton("Register");
        form.add(new JLabel("RegNo:"));
        form.add(regField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(eventBox);
        form.add(regBtn);
        add(form, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton exportBtn = new JButton("Export List");
        filterPanel.add(new JLabel("Filter Event:"));
        filterPanel.add(filterBox);
        filterPanel.add(exportBtn);
        south.add(filterPanel, BorderLayout.WEST);
        south.add(statusLabel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        regBtn.addActionListener(e -> registerStudent());
        filterBox.addActionListener(e -> loadTableAsync((String) filterBox.getSelectedItem()));
        exportBtn.addActionListener(e -> exportCsv());
        exportItem.addActionListener(e -> exportCsv());
        exitItem.addActionListener(e -> dispose());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(880, 460);
        loadTableAsync("ALL");
    }

    private void registerStudent() {
        String reg = regField.getText().trim(), name = nameField.getText().trim(), email = emailField.getText().trim();
        String event = (String) eventBox.getSelectedItem();

        if (reg.isEmpty() || name.isEmpty() || !email.contains("@")) {
            statusLabel.setText("ERROR: Invalid input fields.");
            return;
        }

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try (Connection c = DriverManager.getConnection(URL, USER, PASS)) {
                    try (PreparedStatement chk = c.prepareStatement("SELECT 1 FROM registrations WHERE reg_no = ? AND event_name = ?")) {
                        chk.setString(1, reg);
                        chk.setString(2, event);
                        if (chk.executeQuery().next()) return "DUPLICATE";
                    }
                    try (PreparedStatement ins = c.prepareStatement("INSERT INTO registrations(reg_no,student_name,email,event_name) VALUES(?,?,?,?)")) {
                        ins.setString(1, reg);
                        ins.setString(2, name);
                        ins.setString(3, email);
                        ins.setString(4, event);
                        ins.executeUpdate();
                    }
                    return "OK";
                }
            }

            @Override
            protected void done() {
                try {
                    String res = get();
                    if ("DUPLICATE".equals(res)) {
                        statusLabel.setText("ERROR: Duplicate registration for " + event);
                    } else {
                        statusLabel.setText("SUCCESS: Registered " + reg);
                        loadTableAsync((String) filterBox.getSelectedItem());
                    }
                } catch (Exception ex) {
                    statusLabel.setText("DB Error: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void loadTableAsync(String filter) {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                String sql = "ALL".equals(filter)
                        ? "SELECT id,reg_no,student_name,email,event_name FROM registrations"
                        : "SELECT id,reg_no,student_name,email,event_name FROM registrations WHERE event_name=?";
                try (Connection c = DriverManager.getConnection(URL, USER, PASS);
                     PreparedStatement ps = c.prepareStatement(sql)) {
                    if (!"ALL".equals(filter)) ps.setString(1, filter);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        rows.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    tableModel.setRowCount(0);
                    for (Object[] r : get()) tableModel.addRow(r);
                    statusLabel.setText("Loaded " + tableModel.getRowCount() + " records.");
                } catch (Exception ex) {
                    statusLabel.setText("Load failed.");
                }
            }
        }.execute();
    }

    private void exportCsv() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("registrations.csv"))) {
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                pw.println(tableModel.getValueAt(r, 0) + "," +
                        tableModel.getValueAt(r, 1) + "," +
                        tableModel.getValueAt(r, 2) + "," +
                        tableModel.getValueAt(r, 3) + "," +
                        tableModel.getValueAt(r, 4));
            }
            statusLabel.setText("Exported to registrations.csv");
        } catch (Exception ex) {
            statusLabel.setText("Export error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
