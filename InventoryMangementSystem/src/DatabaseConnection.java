import java.sql.*;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "Mishkat0325@#";
    private Connection connection;
    private String dbName;
    
    // Constructor - Establishes connection and creates database/schema if needed
    public DatabaseConnection(String dbName) {
        this.dbName = dbName;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First connect without database to check/create it
            Connection initialConn = DriverManager.getConnection(
                DB_URL + "?useSSL=false", DB_USERNAME, DB_PASSWORD);
            
            if (!databaseExists(initialConn, dbName)) {
                createDatabase(initialConn, dbName);
            }
            initialConn.close();
            
            // Connect to the specific database
            this.connection = DriverManager.getConnection(
                DB_URL + dbName + "?useSSL=false", DB_USERNAME, DB_PASSWORD);
            
            // Create tables if they don't exist
            createSchema();
            
            System.out.println("Successfully connected to MySQL database: " + dbName);
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Check if database exists
    private boolean databaseExists(Connection conn, String dbName) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW DATABASES LIKE '" + dbName + "'");
        boolean exists = rs.next();
        rs.close();
        stmt.close();
        return exists;
    }
    
    // Create new database
    private void createDatabase(Connection conn, String dbName) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE " + dbName);
        stmt.close();
        System.out.println("Created database: " + dbName);
    }
    
    // Create all tables
    public void createSchema() {
        try {
            Statement stmt = connection.createStatement();
            
            // Products table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "quantity INT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
            
            // Categories table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS categories (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL)");
            
            // Product-Category relationship table
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS product_categories (" +
                "product_id INT, " +
                "category_id INT, " +
                "PRIMARY KEY (product_id, category_id), " +
                "FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE)");
            
            stmt.close();
            System.out.println("Database schema created successfully");
        } catch (SQLException e) {
            System.err.println("Error creating schema: " + e.getMessage());
        }
    }
    
    // Close connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    // ========== PRODUCT METHODS ========== //
    
    public void insertProduct(String name, int quantity, double price) {
        String sql = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
            System.out.println("Product added successfully");
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
        }
    }
    
    public void updateProduct(int id, String name, int quantity, double price) {
        String sql = "UPDATE products SET name=?, quantity=?, price=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.setInt(4, id);
            stmt.executeUpdate();
            System.out.println("Product updated successfully");
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
        }
    }
    
    public void deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Product deleted successfully");
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
    }
    
    public void getAllProducts(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data
        String sql = "SELECT * FROM products";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting products: " + e.getMessage());
        }
    }
    
    public void purchaseProduct(int productId, int quantity) {
        String sql = "UPDATE products SET quantity = quantity + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
            System.out.println("Product purchased successfully");
        } catch (SQLException e) {
            System.err.println("Error purchasing product: " + e.getMessage());
        }
    }
    
    // ========== CATEGORY METHODS ========== //
    
    public void insertCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Category added successfully");
        } catch (SQLException e) {
            System.err.println("Error adding category: " + e.getMessage());
        }
    }
    
    public void updateCategory(int id, String name) {
        String sql = "UPDATE categories SET name=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            System.out.println("Category updated successfully");
        } catch (SQLException e) {
            System.err.println("Error updating category: " + e.getMessage());
        }
    }
    
    public void deleteCategory(int id) {
        String sql = "DELETE FROM categories WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Category deleted successfully");
        } catch (SQLException e) {
            System.err.println("Error deleting category: " + e.getMessage());
        }
    }
    
    public void getAllCategories(DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT * FROM categories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
    }
    
    public void getProductsByCategory(int categoryId, DefaultTableModel model) {
        model.setRowCount(0);
        String sql = "SELECT p.* FROM products p " +
                     "JOIN product_categories pc ON p.id = pc.product_id " +
                     "WHERE pc.category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error getting products by category: " + e.getMessage());
        }
    }
    
    // ========== PRODUCT-CATEGORY RELATIONSHIP METHODS ========== //
    
    public void assignCategoryToProduct(int productId, int categoryId) {
        String sql = "INSERT INTO product_categories (product_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
            System.out.println("Category assigned to product successfully");
        } catch (SQLException e) {
            System.err.println("Error assigning category: " + e.getMessage());
        }
    }
    
    public void removeCategoryFromProduct(int productId, int categoryId) {
        String sql = "DELETE FROM product_categories WHERE product_id=? AND category_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
            System.out.println("Category removed from product successfully");
        } catch (SQLException e) {
            System.err.println("Error removing category: " + e.getMessage());
        }
    }
    
    // ========== UTILITY METHODS ========== //
    
    public void updateComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        String sql = "SELECT id, name FROM categories";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                comboBox.addItem(rs.getInt("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading categories to combo box: " + e.getMessage());
        }
    }
    
    public String sellMultipleProducts(List<String> productIds, List<String> quantities) {
        if (productIds.size() != quantities.size()) {
            return "Error: Product IDs and quantities lists must be the same size";
        }
        
        try {
            connection.setAutoCommit(false); // Start transaction
            
            String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                for (int i = 0; i < productIds.size(); i++) {
                    int productId = Integer.parseInt(productIds.get(i).trim());
                    int quantity = Integer.parseInt(quantities.get(i).trim());
                    
                    stmt.setInt(1, quantity);
                    stmt.setInt(2, productId);
                    stmt.executeUpdate();
                }
                connection.commit();
                return "Products sold successfully";
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                return "Transaction rolled back due to error: " + e.getMessage();
            } catch (SQLException ex) {
                return "Error during rollback: " + ex.getMessage();
            }
        } catch (NumberFormatException e) {
            try {
                connection.rollback();
                return "Invalid number format: " + e.getMessage();
            } catch (SQLException ex) {
                return "Error during rollback: " + ex.getMessage();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                return "Error resetting auto-commit: " + e.getMessage();
            }
        }
    }
    
    // Helper method to get connection (for special cases)
    public Connection getConnection() {
        return this.connection;
    }
}