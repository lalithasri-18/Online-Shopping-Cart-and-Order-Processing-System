# Online Shopping Cart and Order Processing System – Report Outline

## 1. Title Page
- Institution: [College]
- Subject: Java Programming
- Project title: ShopSphere – Online Shopping Cart and Order Processing System
- Students (team of two): [Name, roll number] and [Name, roll number]
- Faculty: [Name]
- GitHub: [Paste repository URL after uploading]

## 2. Description of Project
- Objective: Java-only desktop application to browse products, manage a cart, filter by category/price, and process demo orders.
- Features: product catalogue, filtering, cart add/remove, total billing, serialization save/load, order receipts/history.
- Technologies: Java, Swing, ArrayList, HashMap, lambda expressions, functional interfaces, serialization, Files/Path.

## 3. Code Snippets and Packages Used
Include short excerpts from `ShopSphere.java` for:
- `HashMap<Integer, Product>` product catalogue
- `ArrayList<CartItem>` shopping cart
- Custom `ProductFilter` functional interface and lambda
- `ObjectOutputStream` / `ObjectInputStream` serialization
- `Files.writeString()` receipt generation

Packages: `javax.swing`, `javax.swing.table`, `java.awt`, `java.io`, `java.nio.file`, `java.time`, `java.util`.

Explain ArrayList vs HashMap: cart is sequential (O(n) ID search); HashMap provides average O(1) product lookup by ID.

## 4. Results – Actual Screenshots
- [Insert screenshot of product catalogue]
- [Insert screenshot of filtered products]
- [Insert screenshot of shopping cart with total]
- [Insert screenshot of successful save/load]
- [Insert screenshot of order confirmation]
- [Insert screenshot of order history]

Only insert screenshots obtained by running the program.

## 5. Conclusion
ShopSphere demonstrates object-oriented Java programming and Java collections in a functional shopping workflow. Lambda expressions and a functional interface enable filtering, while serialization restores the cart. Java file I/O saves order receipts. The application meets the required Java concepts without external frameworks or databases.
