import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ShopSphere - Java-only Shopping Cart and Order Processing System.
 * Uses ArrayList, HashMap, lambda expressions, a functional interface,
 * Java Swing, serialization, and file I/O.
 */
public class ShopSphere extends JFrame {
    private static final long serialVersionUID = 1L;

    private static class Product implements Serializable {
        private static final long serialVersionUID = 1L;
        final int id;
        final String name, category;
        final double price;

        Product(int id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }
    }

    private static class CartItem implements Serializable {
        private static final long serialVersionUID = 1L;
        final Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double subtotal() {
            return product.price * quantity;
        }
    }

    @FunctionalInterface
    private interface ProductFilter {
        boolean matches(Product product);
    }

    // HashMap gives quick product lookup by ID.
    private final HashMap<Integer, Product> products = new HashMap<>();
    // ArrayList keeps the cart's selected products and quantities.
    private ArrayList<CartItem> cart = new ArrayList<>();

    private final DefaultTableModel productModel = readonlyModel(
            "ID", "Product", "Category", "Price (Rs.)");
    private final DefaultTableModel cartModel = readonlyModel(
            "ID", "Product", "Qty", "Subtotal (Rs.)");
    private final JTable productTable = new JTable(productModel);
    private final JTable cartTable = new JTable(cartModel);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{
            "All", "Electronics", "Fashion", "Books", "Home"});
    private final JTextField maxPriceField = new JTextField(7);
    private final JLabel totalLabel = new JLabel("Total: Rs. 0.00");

    private static DefaultTableModel readonlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public ShopSphere() {
        super("ShopSphere | Java Shopping Cart");
        seedProducts();
        buildUI();
        refreshProducts();
        refreshCart();
    }

    private void seedProducts() {
        addProduct(101, "Wireless Mouse", "Electronics", 799);
        addProduct(102, "Keyboard", "Electronics", 1299);
        addProduct(103, "Headphones", "Electronics", 1699);
        addProduct(104, "Notebook", "Books", 120);
        addProduct(105, "Java Basics Book", "Books", 550);
        addProduct(106, "T-Shirt", "Fashion", 699);
        addProduct(107, "Backpack", "Fashion", 1399);
        addProduct(108, "Water Bottle", "Home", 349);
        addProduct(109, "Desk Lamp", "Home", 899);
        addProduct(110, "Phone Stand", "Electronics", 299);
    }

    private void addProduct(int id, String name, String category, double price) {
        products.put(id, new Product(id, name, category, price));
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 610);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        JLabel heading = new JLabel("ShopSphere  |  Online Shopping & Order Processing", SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 22));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 8, 10, 8));
        add(heading, BorderLayout.NORTH);

        JPanel left = new JPanel(new BorderLayout(8, 8));
        left.setBorder(BorderFactory.createTitledBorder("Product Catalogue"));
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Category:"));
        filters.add(categoryBox);
        filters.add(new JLabel("Max Rs.:"));
        filters.add(maxPriceField);
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(e -> refreshProducts());
        filters.add(filterButton);
        left.add(filters, BorderLayout.NORTH);
        left.add(new JScrollPane(productTable), BorderLayout.CENTER);
        JButton addButton = new JButton("Add Selected Product to Cart");
        addButton.addActionListener(e -> addToCart());
        left.add(addButton, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBorder(BorderFactory.createTitledBorder("Your Shopping Cart"));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        right.add(totalLabel, BorderLayout.NORTH);
        right.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 3, 6, 6));
        actions.add(button("Remove Item", () -> removeItem()));
        actions.add(button("Save Cart", () -> saveCart()));
        actions.add(button("Load Cart", () -> loadCart()));
        actions.add(button("Place Order", () -> placeOrder()));
        actions.add(button("Order History", () -> showOrderHistory()));
        actions.add(button("Clear Cart", () -> clearCart()));
        right.add(actions, BorderLayout.SOUTH);

        JPanel columns = new JPanel(new GridLayout(1, 2, 12, 0));
        columns.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        columns.add(left);
        columns.add(right);
        add(columns, BorderLayout.CENTER);
    }

    private JButton button(String label, Runnable onClick) {
        JButton result = new JButton(label);
        result.addActionListener(e -> onClick.run());
        return result;
    }

    private void refreshProducts() {
        String category = (String) categoryBox.getSelectedItem();
        double maxPrice = Double.MAX_VALUE;
        String enteredPrice = maxPriceField.getText().trim();
        if (!enteredPrice.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(enteredPrice);
                if (!Double.isFinite(maxPrice) || maxPrice < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid non-negative maximum price.");
                return;
            }
        }

        final double selectedMax = maxPrice;
        // Lambda implements our custom functional interface.
        ProductFilter filter = p ->
                ("All".equals(category) || p.category.equals(category))
                && p.price <= selectedMax;

        productModel.setRowCount(0);
        ArrayList<Product> sorted = new ArrayList<>(products.values());
        sorted.sort(Comparator.comparingInt(p -> p.id));
        for (Product p : sorted) {
            if (filter.matches(p)) {
                productModel.addRow(new Object[]{p.id, p.name, p.category,
                        String.format(Locale.US, "%.2f", p.price)});
            }
        }
    }

    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }
        int id = (Integer) productModel.getValueAt(selectedRow, 0);
        Product chosen = products.get(id); // HashMap lookup
        for (CartItem item : cart) {
            if (item.product.id == id) {
                item.quantity++;
                refreshCart();
                return;
            }
        }
        cart.add(new CartItem(chosen, 1)); // ArrayList insertion
        refreshCart();
    }

    private void removeItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a cart item to remove.");
            return;
        }
        cart.remove(selectedRow);
        refreshCart();
    }

    private double total() {
        double result = 0;
        for (CartItem item : cart) {
            result += item.subtotal();
        }
        return result;
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        for (CartItem item : cart) {
            cartModel.addRow(new Object[]{item.product.id, item.product.name,
                    item.quantity, String.format(Locale.US, "%.2f", item.subtotal())});
        }
        totalLabel.setText(String.format(Locale.US, "Total: Rs. %.2f", total()));
    }

    private void clearCart() {
        cart.clear();
        refreshCart();
    }

    private void saveCart() {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("cart.dat"))) {
            out.writeObject(cart);
            JOptionPane.showMessageDialog(this, "Cart saved to cart.dat");
        } catch (IOException ex) {
            showError("Unable to save cart", ex);
        }
    }

    private void loadCart() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("cart.dat"))) {
            Object loaded = in.readObject();
            if (!(loaded instanceof ArrayList<?>)) {
                throw new IOException("Invalid cart file");
            }
            ArrayList<?> savedList = (ArrayList<?>) loaded;
            ArrayList<CartItem> restored = new ArrayList<>();
            for (Object obj : savedList) {
                if (!(obj instanceof CartItem)) {
                    throw new IOException("Invalid cart item");
                }
                CartItem item = (CartItem) obj;
                if (item.product == null || item.quantity <= 0 ||
                        !products.containsKey(item.product.id)) {
                    throw new IOException("Invalid product or quantity in cart file");
                }
                // Keep the live catalogue price/product information.
                restored.add(new CartItem(products.get(item.product.id), item.quantity));
            }
            cart = restored;
            refreshCart();
            JOptionPane.showMessageDialog(this, "Cart restored successfully.");
        } catch (IOException | ClassNotFoundException ex) {
            showError("Unable to load cart (save one first)", ex);
        }
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty.");
            return;
        }
        String customer = JOptionPane.showInputDialog(this, "Enter customer name:");
        if (customer == null) return;
        customer = customer.trim();
        if (customer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer name cannot be empty.");
            return;
        }

        String orderId = "ORD-" + System.currentTimeMillis();
        StringBuilder receipt = new StringBuilder();
        receipt.append("SHOPSPHERE - ORDER CONFIRMATION\n")
                .append("Order ID: ").append(orderId).append('\n')
                .append("Customer: ").append(customer).append('\n')
                .append("Date: ").append(LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"))).append("\n\n");
        for (CartItem item : cart) {
            receipt.append(item.product.name).append("  x").append(item.quantity)
                    .append("   Rs. ").append(String.format(Locale.US, "%.2f", item.subtotal()))
                    .append('\n');
        }
        receipt.append("\nTOTAL: Rs. ")
                .append(String.format(Locale.US, "%.2f", total()))
                .append("\nStatus: Order placed (demo only)\n");

        try {
            Files.createDirectories(Path.of("orders"));
            Files.writeString(Path.of("orders", orderId + ".txt"),
                    receipt.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(this, new JScrollPane(
                    new JTextArea(receipt.toString(), 13, 36)),
                    "Order Placed Successfully", JOptionPane.INFORMATION_MESSAGE);
            clearCart();
            Files.deleteIfExists(Path.of("cart.dat")); // No stale cart after checkout
        } catch (IOException ex) {
            showError("Unable to save order", ex);
        }
    }

    private void showOrderHistory() {
        Path folder = Path.of("orders");
        if (!Files.isDirectory(folder)) {
            JOptionPane.showMessageDialog(this, "No orders yet. Place an order first.");
            return;
        }
        try (java.util.stream.Stream<Path> files = Files.list(folder)) {
            java.util.List<Path> records = files
                    .filter(p -> p.getFileName().toString().endsWith(".txt"))
                    .sorted(Comparator.reverseOrder()).toList();
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No orders yet.");
                return;
            }
            String[] names = new String[records.size()];
            for (int i = 0; i < records.size(); i++) {
                names[i] = records.get(i).getFileName().toString();
            }
            String chosen = (String) JOptionPane.showInputDialog(this,
                    "Choose an order:", "Order History",
                    JOptionPane.PLAIN_MESSAGE, null, names, names[0]);
            if (chosen != null) {
                String content = Files.readString(folder.resolve(chosen), StandardCharsets.UTF_8);
                JTextArea area = new JTextArea(content, 14, 38);
                area.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(area),
                        chosen, JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            showError("Unable to read order history", ex);
        }
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this, message + ": " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShopSphere().setVisible(true));
    }
}
