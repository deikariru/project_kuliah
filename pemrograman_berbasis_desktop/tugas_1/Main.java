import java.util.Scanner;

public class Main {

    // ========================
    // RESTAURANT MENU DATA
    // ========================
    static Menu[] menuList = {
        // Food (min 4)
        new Menu("Nasi Padang",   25000, "food"),
        new Menu("Nasi Goreng",   20000, "food"),
        new Menu("Mie Ayam",      18000, "food"),
        new Menu("Ayam Bakar",    30000, "food"),
        new Menu("Soto Ayam",     22000, "food"),
        // Beverage (min 4)
        new Menu("Es Teh Manis",   5000, "beverage"),
        new Menu("Jus Alpukat",   15000, "beverage"),
        new Menu("Es Jeruk",       8000, "beverage"),
        new Menu("Air Mineral",    4000, "beverage"),
        new Menu("Kopi Susu",     12000, "beverage")
    };

    // Order arrays (max 4 items)
    static String[] orderName     = new String[4];
    static int[]    orderQty      = new int[4];
    static int      totalOrderedItems = 0;

    // ========================
    // MAIN METHOD
    // ========================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        displayMenu();
        takeOrder(sc);
        printReceipt();

        sc.close();
    }

    // ========================
    // 1. DISPLAY MENU
    // ========================
    static void displayMenu() {
        System.out.println("============================================");
        System.out.println("        WELCOME TO RESTO NUSANTARA         ");
        System.out.println("============================================");

        // Collect all unique categories from menuList dynamically
        String[] uniqueCategories = new String[menuList.length];
        int categoryCount = 0;

        int i = 0;
        while (i < menuList.length) {
            String cat = menuList[i].getCategory();
            boolean alreadyExists = false;

            int j = 0;
            while (j < categoryCount) {
                if (uniqueCategories[j].equals(cat)) {
                    alreadyExists = true;
                }
                j++;
            }

            if (!alreadyExists) {
                uniqueCategories[categoryCount] = cat;
                categoryCount++;
            }
            i++;
        }

        // Display menu per category, numbering continues across categories
        int startNumber = 1;
        int k = 0;
        while (k < categoryCount) {
            System.out.println("\n--- " + uniqueCategories[k].toUpperCase() + " MENU ---");
            System.out.printf("%-5s %-20s %s%n", "No.", "Item Name", "Price");
            System.out.println("--------------------------------------------");

            startNumber = displayCategory(uniqueCategories[k], startNumber);
            k++;
        }

        System.out.println("============================================");
    }

    // Displays menu items for a given category with continuous numbering.
    // Accepts startNumber and returns the next available number,
    // so numbering can continue into the next category.
    static int displayCategory(String category, int startNumber) {
        int no = startNumber;
        int i = 0;
        while (i < menuList.length) {
            if (menuList[i].getCategory().equals(category)) {
                System.out.printf("%-5d %-20s Rp %,.0f%n",
                    no, menuList[i].getName(), menuList[i].getPrice());
                no++;
            }
            i++;
        }
        return no; // return next available number
    }

    // ========================
    // 2. TAKE ORDER
    // ========================
    static void takeOrder(Scanner sc) {
        System.out.println("\nPlease enter your order (max 4 items).");
        System.out.println("Format: Item Name = Quantity   (e.g: Nasi Padang = 2)");
        System.out.println("Type 'done' when you are finished ordering.\n");

        int slot = 0;
        while (slot < 4) {
            System.out.print("Order " + (slot + 1) + ": ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            }

            // Parse input "Item Name = Quantity"
            if (!input.contains("=")) {
                System.out.println("  [!] Invalid format. Use: Item Name = Quantity");
                continue;
            }

            String[] parts = input.split("=");
            if (parts.length != 2) {
                System.out.println("  [!] Invalid format. Use: Item Name = Quantity");
                continue;
            }

            String nameInput = parts[0].trim();
            String qtyString = parts[1].trim();

            // Validate quantity is a positive number
            int qty;
            try {
                qty = Integer.parseInt(qtyString);
                if (qty <= 0) {
                    System.out.println("  [!] Quantity must be greater than 0.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("  [!] Quantity must be a number.");
                continue;
            }

            // Check whether item exists in menu list
            boolean found = findMenu(nameInput) != null;
            if (!found) {
                System.out.println("  [!] Item '" + nameInput + "' not found. Please check the menu.");
                continue;
            }

            orderName[slot] = nameInput;
            orderQty[slot]  = qty;
            slot++;
        }

        totalOrderedItems = slot;

        if (totalOrderedItems == 0) {
            System.out.println("\nNo orders were placed. Program ended.");
            System.exit(0);
        }
    }

    // Helper: find menu item by name (case-insensitive)
    static Menu findMenu(String name) {
        int i = 0;
        while (i < menuList.length) {
            if (menuList[i].getName().equalsIgnoreCase(name)) {
                return menuList[i];
            }
            i++;
        }
        return null;
    }

    // ========================
    // 3. CALCULATE TOTAL COST
    // ========================
    static double calculateSubtotal() {
        double subtotal = 0;
        int i = 0;
        while (i < totalOrderedItems) {
            Menu m = findMenu(orderName[i]);
            if (m != null) {
                subtotal += m.getPrice() * orderQty[i];
            }
            i++;
        }
        return subtotal;
    }

    // Calculate total BOGO discount value:
    // Each beverage ordered with qty >= 2 gets floor(qty/2) items free.
    // Example: Es Teh qty=3 → 1 free. Jus Alpukat qty=4 → 2 free.
    static double calculateBogoValue() {
        double totalBogo = 0;
        int i = 0;
        while (i < totalOrderedItems) {
            Menu m = findMenu(orderName[i]);
            if (m != null && m.getCategory().equals("beverage")) {
                int qty = orderQty[i];
                if (qty >= 2) {
                    int freeQty = qty / 2;
                    totalBogo += m.getPrice() * freeQty;
                }
            }
            i++;
        }
        return totalBogo;
    }

    // Check whether any BOGO is applicable (at least 1 beverage with qty >= 2)
    static boolean hasBogo() {
        int i = 0;
        while (i < totalOrderedItems) {
            Menu m = findMenu(orderName[i]);
            if (m != null && m.getCategory().equals("beverage") && orderQty[i] >= 2) {
                return true;
            }
            i++;
        }
        return false;
    }

    // Build BOGO detail text to display on the receipt
    static String getBogoDetail() {
        String detail = "";
        int i = 0;
        while (i < totalOrderedItems) {
            Menu m = findMenu(orderName[i]);
            if (m != null && m.getCategory().equals("beverage") && orderQty[i] >= 2) {
                int freeQty = orderQty[i] / 2;
                if (!detail.equals("")) {
                    detail += ", ";
                }
                detail += m.getName() + " x" + freeQty;
            }
            i++;
        }
        return detail;
    }

    // ========================
    // 4. PRINT RECEIPT
    // ========================
    static void printReceipt() {
        double subtotal       = calculateSubtotal();
        double taxFee         = subtotal * 0.10;
        double serviceFee     = 20000;
        double discount       = 0;
        double bogoValue      = 0;
        boolean hasDiscount   = subtotal > 100000;
        // BOGO applies if: total > 50.000 AND there is a beverage with qty >= 2
        boolean applyBogo     = subtotal > 50000 && hasBogo();

        // Calculate discount
        if (hasDiscount) {
            discount = subtotal * 0.10;
        }

        // Calculate BOGO value: each beverage with qty>=2 gets floor(qty/2) free
        if (applyBogo) {
            bogoValue = calculateBogoValue();
        }

        double grandTotal = subtotal - discount - bogoValue + taxFee + serviceFee;

        // ---- Print ----
        System.out.println("\n");
        System.out.println("============================================");
        System.out.println("              PAYMENT RECEIPT               ");
        System.out.println("            RESTO NUSANTARA                 ");
        System.out.println("============================================");
        System.out.printf("%-22s %-6s %-10s %s%n", "Item", "Qty", "Price/item", "Subtotal");
        System.out.println("--------------------------------------------");

        int i = 0;
        while (i < totalOrderedItems) {
            Menu m = findMenu(orderName[i]);
            if (m != null) {
                double itemSubtotal = m.getPrice() * orderQty[i];
                System.out.printf("%-22s %-6d Rp%,-8.0f Rp%,.0f%n",
                    m.getName(), orderQty[i], m.getPrice(), itemSubtotal);
            }
            i++;
        }

        System.out.println("--------------------------------------------");
        System.out.printf("%-35s Rp%,.0f%n", "Order Total:", subtotal);

        // Discount
        if (hasDiscount) {
            System.out.printf("%-35s -Rp%,.0f%n", "Discount 10% (total > Rp100.000):", discount);
        }

        // BOGO
        if (applyBogo) {
            System.out.printf("%-35s -Rp%,.0f%n",
                "BOGO Beverage (" + getBogoDetail() + "):", bogoValue);
        }

        System.out.printf("%-35s Rp%,.0f%n", "Tax 10%:", taxFee);
        System.out.printf("%-35s Rp%,.0f%n", "Service Fee:", serviceFee);
        System.out.println("============================================");
        System.out.printf("%-35s Rp%,.0f%n", "GRAND TOTAL:", grandTotal);
        System.out.println("============================================");

        // Applied promotions info
        if (hasDiscount || applyBogo) {
            System.out.println("\n[INFO] Promotions applied to your order:");
            if (hasDiscount) {
                System.out.println("  * 10% discount because order total exceeds Rp 100.000");
            }
            if (applyBogo) {
                System.out.println("  * Buy 1 Get 1 Free: " + getBogoDetail()
                    + " (total > Rp 50.000, applies to beverages with qty >= 2)");
            }
        }

        System.out.println("\nThank you for dining at Resto Nusantara!");
        System.out.println("============================================");
    }
}