# Task 2 – From Requirement to Event-Driven Design: Hospital Outpatient Desk

## Task 2.1 – Event Model Table
| Event Source | Event Object | Listener Interface | Action Taken |
| :--- | :--- | :--- | :--- |
| `deptComboBox` (`JComboBox`) | `ItemEvent` / `ActionEvent` | `ItemListener` / `ActionListener` | Filter doctor map by selected department; repopulate `doctorComboBox`; mark `dirty = true`[cite: 51]. |
| `patientIdField` (`JTextField`) | `DocumentEvent` | `DocumentListener` | Mark `bookingInProgress = true` when non-empty[cite: 51]. |
| `doctorComboBox` (`JComboBox`) | `ActionEvent` | `ActionListener` | Load available time slots for chosen doctor[cite: 51]. |
| `slotComboBox` (`JComboBox`) | `ActionEvent` | `ActionListener` | Enable `Book Appointment` button[cite: 51]. |
| `bookButton` (`JButton`) | `ActionEvent` | `ActionListener` | Validate inputs, book slot, show status, reset `bookingInProgress = false`[cite: 51]. |
| `mainFrame` (`JFrame`) | `WindowEvent` | `WindowListener` (`WindowAdapter`) | In `windowClosing()`: if `bookingInProgress` is true, prompt `JOptionPane` confirm dialog[cite: 51, 52]. |

## Task 2.2 – Component Containment Hierarchy
```text
JFrame ("Outpatient Appointment Desk" - BorderLayout)
 ├── JPanel [NORTH] (FlowLayout)
 │    └── JLabel ("Hospital Outpatient Registration & Booking")
 ├── JPanel [CENTER] (GridLayout(4, 2, 8, 8))
 │    ├── JLabel ("Patient ID:")          └── JTextField (patientIdField)
 │    ├── JLabel ("Department:")          └── JComboBox<String> (deptComboBox)
 │    ├── JLabel ("Doctor:")              └── JComboBox<Doctor> (doctorComboBox)
 │    └── JLabel ("Time Slot:")           └── JComboBox<String> (slotComboBox)
 └── JPanel [SOUTH] (BorderLayout)
      ├── JPanel [CENTER] (FlowLayout) -> JButton ("Book Appointment"), JButton ("Reset")
      └── JLabel [SOUTH] (statusLabel - displays confirmation or validation error)
