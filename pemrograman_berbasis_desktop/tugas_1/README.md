# Resto Nusantara — Restaurant Application

A simple Java console application that simulates a restaurant ordering system with menu display, order taking, promotion handling, and receipt printing.

---

## How to Run

1. Make sure Java is installed (JDK 8+)
2. Open terminal / command prompt
3. Navigate to the program folder
4. Compile:
   ```
   javac Menu.java Main.java
   ```
5. Run:
   ```
   java Main
   ```

---

## Program Flow

```
displayMenu() → takeOrder() → printReceipt()
```

---

## Class Structure

### `Menu.java`
Represents a single menu item with three attributes:

| Field | Type | Description |
|---|---|---|
| `name` | `String` | Item name |
| `price` | `double` | Item price |
| `category` | `String` | `"food"` or `"beverage"` |

### `Main.java`
The main class containing all program logic, split into these methods:

| Method | Return | Description |
|---|---|---|
| `main()` | `void` | Entry point, calls the three main steps |
| `displayMenu()` | `void` | Detects categories dynamically, prints grouped menu |
| `displayCategory()` | `int` | Prints items of one category, returns next number |
| `takeOrder()` | `void` | Reads and validates up to 4 orders from user |
| `findMenu()` | `Menu` | Searches `menuList` by name (case-insensitive) |
| `calculateSubtotal()` | `double` | Sums up all ordered items × quantity |
| `calculateBogoValue()` | `double` | Calculates total BOGO discount across all eligible beverages |
| `hasBogo()` | `boolean` | Returns true if any beverage has qty ≥ 2 |
| `getBogoDetail()` | `String` | Builds a display string of all free items |
| `printReceipt()` | `void` | Applies promotions and prints the full receipt |

---

## Decision Structures

All conditional logic in this program uses `if` / `if-else` structures. Here is a complete breakdown by method:

### `displayMenu()` — Category deduplication
```
if category not yet in uniqueCategories
    → add it to the list
```

### `displayCategory()` — Filter by category
```
if menuList[i].getCategory() equals category
    → print the item and increment number
```

### `takeOrder()` — Input validation (nested if)
```
if input equals "done"
    → stop taking orders

if input does not contain "="
    → reject: invalid format

if parts.length != 2
    → reject: invalid format

if qty <= 0
    → reject: quantity must be > 0

if NumberFormatException caught
    → reject: quantity must be a number

if findMenu(nameInput) == null
    → reject: item not found in menu

(all validations passed)
    → save to orderName[] and orderQty[]

if totalOrderedItems == 0
    → exit program (nothing ordered)
```

### `calculateBogoValue()` — Per-item BOGO calculation
```
if item category equals "beverage"
    if qty >= 2
        → freeQty = qty / 2
        → add (price × freeQty) to totalBogo
```

### `hasBogo()` — BOGO eligibility check
```
if item category equals "beverage" AND qty >= 2
    → return true
```

### `getBogoDetail()` — Build BOGO description string
```
if item category equals "beverage" AND qty >= 2
    → append "ItemName x{freeQty}" to detail string
```

### `printReceipt()` — Promotion application
```
if subtotal > 100.000
    → hasDiscount = true
    → discount = subtotal × 10%

if subtotal > 50.000 AND hasBogo()
    → applyBogo = true
    → bogoValue = calculateBogoValue()

if hasDiscount
    → print discount line on receipt

if applyBogo
    → print BOGO line on receipt

if hasDiscount OR applyBogo
    → print promotions info section
    if hasDiscount → print discount info
    if applyBogo   → print BOGO info
```

---

## BOGO Logic Explained

> **Buy 1 Get 1 Free** applies per beverage item, based on quantity ordered.

Formula: `freeQty = qty / 2` (integer division)

| Beverage Ordered | Free Items | Reason |
|---|---|---|
| Es Teh qty = 1 | 0 | qty < 2, not eligible |
| Es Teh qty = 2 | 1 | 2 / 2 = 1 |
| Es Teh qty = 3 | 1 | 3 / 2 = 1 |
| Es Teh qty = 4 | 2 | 4 / 2 = 2 |
| Es Teh qty=2 + Jus Alpukat qty=2 | 1 + 1 = 2 | both eligible |

BOGO only activates when `subtotal > Rp 50.000`.

---

## Sample Session

```
============================================
        WELCOME TO RESTO NUSANTARA         
============================================

--- FOOD MENU ---
No.  Item Name            Price
--------------------------------------------
1    Nasi Padang          Rp 25.000
2    Nasi Goreng          Rp 20.000
3    Mie Ayam             Rp 18.000
4    Ayam Bakar           Rp 30.000
5    Soto Ayam            Rp 22.000

--- BEVERAGE MENU ---
No.  Item Name            Price
--------------------------------------------
6    Es Teh Manis         Rp 5.000
7    Jus Alpukat          Rp 15.000
8    Es Jeruk             Rp 8.000
9    Air Mineral          Rp 4.000
10   Kopi Susu            Rp 12.000
============================================

Order 1: Nasi Padang = 2
Order 2: Ayam Bakar = 1
Order 3: Jus Alpukat = 2
Order 4: done

============================================
              PAYMENT RECEIPT               
            RESTO NUSANTARA                 
============================================
Item                   Qty    Price/item Subtotal
--------------------------------------------
Nasi Padang            2      Rp 25.000  Rp 50.000
Ayam Bakar             1      Rp 30.000  Rp 30.000
Jus Alpukat            2      Rp 15.000  Rp 30.000
--------------------------------------------
Order Total:                        Rp 110.000
Discount 10% (total > Rp100.000):  -Rp 11.000
BOGO Beverage (Jus Alpukat x1):    -Rp 15.000
Tax 10%:                            Rp 11.000
Service Fee:                        Rp 20.000
============================================
GRAND TOTAL:                        Rp 115.000
============================================

[INFO] Promotions applied to your order:
  * 10% discount because order total exceeds Rp 100.000
  * Buy 1 Get 1 Free: Jus Alpukat x1 (total > Rp 50.000, applies to beverages with qty >= 2)

Thank you for dining at Resto Nusantara!
============================================
```

---

## Assignment Checklist

- [x] `Menu` class with attributes: `name`, `price`, `category`
- [x] `Main` class with separate methods for each function
- [x] Minimum 4 food items + 4 beverage items stored in an array
- [x] Array used to manage the menu list (`menuList[]`)
- [x] Array used to manage orders (`orderName[]`, `orderQty[]`)
- [x] Decision structures (`if` / `if-else` / nested `if`) throughout all methods
- [x] No `for` loop used — `while` is used only for array iteration
- [x] Order input format: `Item Name = Quantity` (max 4 items)
- [x] Tax 10% + service fee Rp 20.000 added to grand total
- [x] 10% discount when subtotal > Rp 100.000
- [x] BOGO beverage when subtotal > Rp 50.000 and beverage qty ≥ 2
- [x] Receipt shows all item details, tax, service fee, discounts, and grand total