# ShopSphere – Java Online Shopping Cart and Order Processing System

A Java-only desktop shopping app created with **Java Swing** for a second-year Java Programming course-end project.

## Features
- Product catalogue with sample products
- Product category and maximum-price filters
- Add and remove cart items; quantity increases on repeated adds
- Running bill / total
- Save and restore shopping cart using object serialization
- Place demo orders and generate receipt files
- View saved order history

## Java concepts used
| Concept | Usage |
|---|---|
| `HashMap<Integer, Product>` | Product catalogue and quick product lookup by ID |
| `ArrayList<CartItem>` | Stores cart items |
| Lambda expression | Product filtering and sorting |
| Custom `@FunctionalInterface` | `ProductFilter` interface for category/price filtering |
| Serializable / Object streams | Save and restore cart |
| Java Swing | Desktop graphical interface |
| `Files` / `Path` | Order receipts and history |

**List vs Map:** The cart uses `ArrayList` because it is easy to iterate and display items in order; searching its entries by product ID takes O(n). Product lookup uses `HashMap`, where lookup by key is O(1) on average. These data structures serve different operations.

## Requirements
- JDK 17 or newer (recommended: JDK 21)
- VS Code with Extension Pack for Java, or any Java IDE

## Run in Windows Command Prompt or VS Code terminal
From the project folder:

```bash
javac -encoding UTF-8 -d out src/ShopSphere.java
java -cp out ShopSphere
```

No external libraries and no database required.

## Suggested demo flow
1. Select a product row and click **Add Selected Product to Cart**.
2. Add the product again to increase quantity.
3. Choose a category and/or maximum price, then **Apply Filter**.
4. Click **Save Cart**, then **Clear Cart**, then **Load Cart**.
5. Click **Place Order**, enter a name, and view the receipt.
6. Click **Order History** to view the saved receipt.

`cart.dat` and the `orders/` directory are created automatically while running and are excluded from Git by `.gitignore`.

## Project report
Include title page (two students, faculty, repository URL), project description, important code snippets with package/class explanations, real output screenshots, and conclusion.

## Notes
This is a **demo** shopping/order system: it does not accept payments, create real deliveries or manage actual stock. No customer passwords or personal records are required.
