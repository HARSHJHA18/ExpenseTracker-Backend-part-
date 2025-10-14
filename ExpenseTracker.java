import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

class Expense {
    private int id;
    private String category;
    private double amount;
    private String note;
    private LocalDate date;

    public Expense(int id, String category, double amount, String note, LocalDate date) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.note = note;
        this.date = date;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getNote() { return note; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return id + " | " + date + " | " + category + " | ₹" + amount + " | " + note;
    }
}

class ExpenseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/expense_db";
    private static final String USER = "root";  // 🔸 your MySQL username
    private static final String PASS = "password";      // 🔸 your MySQL password

    public ExpenseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found!");
        }
    }

    // Add expense
    public void addExpense(String category, double amount, String note) {
        String sql = "INSERT INTO expenses (category, amount, note, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ps.setDouble(2, amount);
            ps.setString(3, note);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();

            System.out.println("✅ Expense added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding expense: " + e.getMessage());
        }
    }

    // View all expenses
    public void showExpenses() {
        String sql = "SELECT * FROM expenses ORDER BY date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            boolean empty = true;
            System.out.println("\n📋 All Expenses:");
            while (rs.next()) {
                empty = false;
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getDate("date") + " | " +
                                rs.getString("category") + " | ₹" +
                                rs.getDouble("amount") + " | " +
                                rs.getString("note")
                );
            }
            if (empty) System.out.println("No expenses found!");

        } catch (SQLException e) {
            System.out.println("Error showing expenses: " + e.getMessage());
        }
    }

    // Category summary
    public void showCategorySummary() {
        String sql = "SELECT category, SUM(amount) AS total FROM expenses GROUP BY category";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n💰 Expense Summary by Category:");
            while (rs.next()) {
                System.out.println(rs.getString("category") + ": ₹" + rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error summarizing: " + e.getMessage());
        }
    }

    // Update expense by ID
    public void updateExpense(int id, String category, double amount, String note) {
        String sql = "UPDATE expenses SET category=?, amount=?, note=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            ps.setDouble(2, amount);
            ps.setString(3, note);
            ps.setInt(4, id);

            int rows = ps.executeUpdate();
            if (rows > 0)
                System.out.println("✅ Expense updated successfully!");
            else
                System.out.println("❌ Expense ID not found!");

        } catch (SQLException e) {
            System.out.println("Error updating expense: " + e.getMessage());
        }
    }

    // Delete expense by ID
    public void deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("🗑️ Expense deleted successfully!");
            else
                System.out.println("❌ Expense ID not found!");

        } catch (SQLException e) {
            System.out.println("Error deleting expense: " + e.getMessage());
        }
    }
}

public class ExpenseTracker {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();

        while (true) {
            System.out.println("\n====== EXPENSE TRACKER (MySQL + CRUD) ======");
            System.out.println("1. Add Expense");
            System.out.println("2. View All Expenses");
            System.out.println("3. Category Summary");
            System.out.println("4. Update Expense");
            System.out.println("5. Delete Expense");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter category: ");
                    String cat = sc.nextLine();
                    System.out.print("Enter amount: ₹");
                    double amt = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("Enter note: ");
                    String note = sc.nextLine();
                    manager.addExpense(cat, amt, note);
                    break;

                case 2:
                    manager.showExpenses();
                    break;

                case 3:
                    manager.showCategorySummary();
                    break;

                case 4:
                    manager.showExpenses();
                    System.out.print("Enter Expense ID to update: ");
                    int uid = sc.nextInt();
                    sc.nextLine();
                    System.out.print("New category: ");
                    String newCat = sc.nextLine();
                    System.out.print("New amount: ₹");
                    double newAmt = sc.nextDouble();
                    sc.nextLine();
                    System.out.print("New note: ");
                    String newNote = sc.nextLine();
                    manager.updateExpense(uid, newCat, newAmt, newNote);
                    break;

                case 5:
                    manager.showExpenses();
                    System.out.print("Enter Expense ID to delete: ");
                    int did = sc.nextInt();
                    manager.deleteExpense(did);
                    break;

                case 6:
                    System.out.println("👋 Exiting... Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }
}
