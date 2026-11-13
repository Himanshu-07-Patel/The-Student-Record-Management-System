import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class StudentForms {

    // ---- Change these 3 lines to match your MySQL ----
    static final String URL  = "jdbc:mysql://localhost:3306/school";
    static final String USER = "root";
    static final String PASS = "your_password";

    // JDBC connection: Java talks to MySQL through this
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Forms (Java + JDBC + MySQL)");
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("1. Insert", insertForm());
        tabs.add("2. View",   viewForm());
        tabs.add("3. Update", updateForm());
        tabs.add("4. Delete", deleteForm());
        tabs.add("5. Search", searchForm());

        frame.add(tabs);
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // ---------- FORM 1: INSERT ----------
    static JPanel insertForm() {
        JPanel p = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField course = new JTextField();
        JButton save = new JButton("Save");

        p.add(new JLabel("ID:"));     p.add(id);
        p.add(new JLabel("Name:"));   p.add(name);
        p.add(new JLabel("Course:")); p.add(course);
        p.add(save);

        save.addActionListener(e -> {
            try (Connection con = getConnection()) {
                String sql = "INSERT INTO student VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, Integer.parseInt(id.getText()));
                ps.setString(2, name.getText());
                ps.setString(3, course.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(p, "Saved!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage());
            }
        });
        return p;
    }

    // ---------- FORM 2: VIEW ALL ----------
    static JPanel viewForm() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        JButton load = new JButton("Show All Students");

        p.add(load, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        load.addActionListener(e -> {
            try (Connection con = getConnection()) {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM student");
                area.setText("");
                while (rs.next()) {
                    area.append(rs.getInt("id") + " | "
                              + rs.getString("name") + " | "
                              + rs.getString("course") + "\n");
                }
            } catch (Exception ex) {
                area.setText("Error: " + ex.getMessage());
            }
        });
        return p;
    }

    // ---------- FORM 3: UPDATE ----------
    static JPanel updateForm() {
        JPanel p = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField id = new JTextField();
        JTextField newName = new JTextField();
        JButton update = new JButton("Update Name");

        p.add(new JLabel("ID to update:")); p.add(id);
        p.add(new JLabel("New Name:"));     p.add(newName);
        p.add(update);

        update.addActionListener(e -> {
            try (Connection con = getConnection()) {
                String sql = "UPDATE student SET name = ? WHERE id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newName.getText());
                ps.setInt(2, Integer.parseInt(id.getText()));
                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(p, rows + " record updated");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage());
            }
        });
        return p;
    }

    // ---------- FORM 4: DELETE ----------
    static JPanel deleteForm() {
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField id = new JTextField();
        JButton delete = new JButton("Delete");

        p.add(new JLabel("ID to delete:")); p.add(id);
        p.add(delete);

        delete.addActionListener(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM student WHERE id = ?");
                ps.setInt(1, Integer.parseInt(id.getText()));
                int rows = ps.executeUpdate();
                JOptionPane.showMessageDialog(p, rows + " record deleted");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(p, "Error: " + ex.getMessage());
            }
        });
        return p;
    }

    // ---------- FORM 5: SEARCH ----------
    static JPanel searchForm() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JTextField name = new JTextField(15);
        JButton search = new JButton("Search by Name");
        JTextArea result = new JTextArea();

        top.add(name);
        top.add(search);
        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(result), BorderLayout.CENTER);

        search.addActionListener(e -> {
            try (Connection con = getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM student WHERE name LIKE ?");
                ps.setString(1, "%" + name.getText() + "%");
                ResultSet rs = ps.executeQuery();
                result.setText("");
                while (rs.next()) {
                    result.append(rs.getInt("id") + " | "
                                + rs.getString("name") + " | "
                                + rs.getString("course") + "\n");
                }
            } catch (Exception ex) {
                result.setText("Error: " + ex.getMessage());
            }
        });
        return p;
    }
}
