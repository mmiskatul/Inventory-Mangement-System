import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AddProductWindow extends JFrame {
    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private DatabaseConnection connection;
    
    public AddProductWindow(DatabaseConnection connection) {
        this.connection = connection;
        setTitle("Add New Product");
        setSize(300, 200);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        panel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        panel.add(nameField);
        
        panel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        panel.add(quantityField);
        
        panel.add(new JLabel("Price:"));
        priceField = new JTextField();
        panel.add(priceField);
        
        JButton btnAdd = new JButton("Add Product");
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    
                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("Product name cannot be empty");
                    }
                    
                    connection.insertProduct(name, quantity, price);
                    dispose();
                } catch (NumberFormatException ex) {
                    javax.swing.JOptionPane.showMessageDialog(AddProductWindow.this, 
                        "Please enter valid numeric values for quantity and price", 
                        "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    javax.swing.JOptionPane.showMessageDialog(AddProductWindow.this, 
                        ex.getMessage(), "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnAdd);
        
        add(panel);
    }
}