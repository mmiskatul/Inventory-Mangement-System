import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;

public class InventoryManagement {
    DatabaseConnection connection = new DatabaseConnection("InventoryDB");
    private JFrame frame;
    private JTable purchaseTable;
    private DefaultTableModel producTableModel = new DefaultTableModel(new Object[] {"ID","Name","Quantity","Price","Created","Updated"}, 0);
    private DefaultTableModel categoriesTableModel = new DefaultTableModel(new Object[] {"ID","Name"}, 0);
    private DefaultTableModel inventoryTableModel = new DefaultTableModel(
            new Object[] {"ID", "Name", "Quantity", "Price", "Created", "Updated"}, 0);
    private JTextField idField;
    private JTextField quantityField;
    private JTable inventoryTable;
    private JTextField productIDField;
    private JTextField categIDField;
    private JTextField pidField;
    private JTextField quantitiesField;
    

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InventoryManagement window = new InventoryManagement();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public InventoryManagement() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 700, 550);
        frame.setTitle("Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(0, 0, 700, 520);
        frame.getContentPane().add(tabbedPane);
        
        JPanel purchasePanel = new JPanel();
        tabbedPane.addTab("Purchase", null, purchasePanel, null);
        purchasePanel.setLayout(null);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(new Color(143, 240, 164));
        buttonsPanel.setBounds(0, 425, 700, 70);
        purchasePanel.add(buttonsPanel);
        buttonsPanel.setLayout(new GridLayout(1, 0, 5, 5));
        
        JButton btnDeleteCategory = new JButton("Delete Category");
        btnDeleteCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = JOptionPane.showInputDialog(btnDeleteCategory, "Enter the category Id to Delete:");
                if (id != null && !id.trim().isEmpty()) {
                    try {
                        connection.deleteCategory(Integer.valueOf(id.trim()));
                        connection.getAllCategories(categoriesTableModel);
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(btnDeleteCategory, "Invalid ID");
                    }
                }
            }
        });
        buttonsPanel.add(btnDeleteCategory);
        
        JButton btnDeleteProduct = new JButton("Delete Product");
        btnDeleteProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = JOptionPane.showInputDialog(btnDeleteProduct, "Enter the Product Id to Delete:");
                if (id != null && !id.trim().isEmpty()) {
                    try {
                        connection.deleteProduct(Integer.valueOf(id.trim()));
                        connection.getAllProducts(producTableModel);
                    } catch (NumberFormatException e1) {
                        JOptionPane.showMessageDialog(btnDeleteProduct, "Invalid ID");
                    }
                }
            }
        });
        buttonsPanel.add(btnDeleteProduct);
        
        JButton btnAddNewCategory = new JButton("Add New Category");
        btnAddNewCategory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(btnAddNewCategory, "Enter the category name to add:");
                if (name != null && !name.trim().isEmpty()) {
                    connection.insertCategory(name.trim());
                    connection.getAllCategories(categoriesTableModel);
                }
            }
        });
        buttonsPanel.add(btnAddNewCategory);
        
        JButton btnAddNewProduct = new JButton("Add New Product");
        btnAddNewProduct.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddProductWindow window = new AddProductWindow(connection);
                window.setVisible(true);
                // Refresh product list after window is closed
                connection.getAllProducts(producTableModel);
            }
        });
        buttonsPanel.add(btnAddNewProduct);
        
        JToggleButton toggleBtn = new JToggleButton("Products");
        toggleBtn.setBounds(0, 0, 167, 25);
        toggleBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (toggleBtn.isSelected()) {
                    toggleBtn.setText("Categories");
                    purchaseTable.setModel(categoriesTableModel);
                    connection.getAllCategories(categoriesTableModel);
                } else {
                    toggleBtn.setText("Products");
                    purchaseTable.setModel(producTableModel);
                    connection.getAllProducts(producTableModel);
                }
            }
        });
        purchasePanel.add(toggleBtn);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 25, 700, 300);
        purchasePanel.add(scrollPane);
        
        purchaseTable = new JTable(producTableModel);
        scrollPane.setViewportView(purchaseTable);
        
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(10, 393, 70, 25);
        purchasePanel.add(lblId);
        
        idField = new JTextField();
        idField.setBounds(78, 393, 114, 25);
        purchasePanel.add(idField);
        idField.setColumns(10);
        
        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(210, 393, 70, 25);
        purchasePanel.add(lblQuantity);
        
        quantityField = new JTextField();
        quantityField.setColumns(10);
        quantityField.setBounds(298, 393, 114, 25);
        purchasePanel.add(quantityField);
        
        JButton btnPurchase = new JButton("Purchase");
        btnPurchase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(idField.getText().trim());
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    connection.purchaseProduct(productId, quantity);
                    connection.getAllProducts(producTableModel);
                    JOptionPane.showMessageDialog(btnPurchase, "Product purchased successfully!");
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(btnPurchase, "Please enter valid values");
                }
            }
        });
        btnPurchase.setBounds(438, 393, 117, 25);
        purchasePanel.add(btnPurchase);
        
        JLabel lblProduct = new JLabel("Product Id:");
        lblProduct.setBounds(12, 337, 100, 25);
        purchasePanel.add(lblProduct);
        
        productIDField = new JTextField();
        productIDField.setColumns(10);
        productIDField.setBounds(107, 337, 114, 25);
        purchasePanel.add(productIDField);
        
        JLabel lblCategoryId = new JLabel("Category Id:");
        lblCategoryId.setBounds(222, 337, 100, 25);
        purchasePanel.add(lblCategoryId);
        
        categIDField = new JTextField();
        categIDField.setColumns(10);
        categIDField.setBounds(323, 337, 114, 25);
        purchasePanel.add(categIDField);
        
        JButton btnSet = new JButton("Set");
        btnSet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int productId = Integer.parseInt(productIDField.getText().trim());
                    int categoryId = Integer.parseInt(categIDField.getText().trim());
                    connection.assignCategoryToProduct(productId, categoryId);
                    JOptionPane.showMessageDialog(btnSet, "Category assigned to product successfully!");
                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(btnSet, "Invalid ID");
                }
            }
        });
        btnSet.setBounds(453, 337, 70, 25);
        purchasePanel.add(btnSet);
        
        JPanel inventoryPanel = new JPanel();
        tabbedPane.addTab("Inventory & Sales", null, inventoryPanel, null);
        inventoryPanel.setLayout(null);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(0, 25, 700, 350);
        inventoryPanel.add(scrollPane_1);
        
        inventoryTable = new JTable(inventoryTableModel);
        scrollPane_1.setViewportView(inventoryTable);
        
        JLabel lblSelectCategory = new JLabel("Select Category");
        lblSelectCategory.setBounds(0, 0, 125, 15);
        inventoryPanel.add(lblSelectCategory);
        
        JComboBox<String> categoryComboBox = new JComboBox<>();
        categoryComboBox.setBounds(125, 0, 125, 24);
        inventoryPanel.add(categoryComboBox);
        connection.updateComboBox(categoryComboBox);
        
        JLabel lblEnterPidSeparated = new JLabel("Enter PIDs separated by \",\"");
        lblEnterPidSeparated.setBounds(10, 387, 200, 15);
        inventoryPanel.add(lblEnterPidSeparated);
        
        pidField = new JTextField();
        pidField.setBounds(263, 387, 420, 25);
        inventoryPanel.add(pidField);
        pidField.setColumns(10);
        
        JLabel lblEnterQuantitiesSeparated = new JLabel("Enter Quantities separated by \",\"");
        lblEnterQuantitiesSeparated.setBounds(0, 414, 240, 15);
        inventoryPanel.add(lblEnterQuantitiesSeparated);
        
        quantitiesField = new JTextField();
        quantitiesField.setColumns(10);
        quantitiesField.setBounds(263, 414, 420, 25);
        inventoryPanel.add(quantitiesField);
        
        JButton btnSellProducts = new JButton("Sell Products");
        btnSellProducts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String pids = pidField.getText().trim();
                String quantities = quantitiesField.getText().trim();
                
                if (pids.isEmpty() || quantities.isEmpty()) {
                    JOptionPane.showMessageDialog(btnSellProducts, "Please enter both product IDs and quantities");
                    return;
                }
                
                ArrayList<String> pidArrayList = new ArrayList<>(Arrays.asList(pids.split(",")));
                ArrayList<String> quantArrayList = new ArrayList<>(Arrays.asList(quantities.split(",")));
                
                String result = connection.sellMultipleProducts(pidArrayList, quantArrayList);
                JOptionPane.showMessageDialog(btnSellProducts, result);
                
                // Refresh the inventory table
                if (categoryComboBox.getSelectedItem() != null) {
                    String selected = categoryComboBox.getSelectedItem().toString();
                    int categoryId = Integer.parseInt(selected.split(" - ")[0]);
                    connection.getProductsByCategory(categoryId, inventoryTableModel);
                }
            }
        });
        btnSellProducts.setBounds(254, 451, 130, 25);
        inventoryPanel.add(btnSellProducts);
        
        categoryComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (categoryComboBox.getSelectedItem() != null) {
                    String selected = categoryComboBox.getSelectedItem().toString();
                    int categoryId = Integer.parseInt(selected.split(" - ")[0]);
                    connection.getProductsByCategory(categoryId, inventoryTableModel);
                }
            }
        });
    }
}