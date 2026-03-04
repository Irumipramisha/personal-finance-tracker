# 💰 Personal Finance Tracker

A lightweight, Full-Stack web application designed to track daily income and expenses. This project demonstrates a complete integration between a web frontend, a Java backend, and a MySQL database.

## 🎥 Project Demo
*(https://www.linkedin.com/posts/activity-7434311600578273280-BRGo?utm_source=share&utm_medium=member_desktop&rcm=ACoAAFkT5TIBdWV0A9oOra0r7K0oOR9e93hAmT4)*

---

## 🚀 Key Features
* **Add Transactions:** Input description, amount, and type (Income/Expense) which saves instantly to the database.
* **Real-time UI Updates:** Total Income, Total Expense, and the Current Balance are calculated and displayed automatically.
* **Data Persistence:** All records are stored in a MySQL database, ensuring data is not lost when the browser is closed.
* **Delete Records:** Remove any transaction from the list, which also deletes the record from the database.

---

## 🛠️ Tech Stack
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla JS using Fetch API).
* **Backend:** Java (using `com.sun.net.httpserver.HttpServer`).
* **Database:** MySQL 8.0+.

---

## 📂 Project Structure
* **`/frontend`**:
  * `index.html` - The user interface.
  * `style.css` - Modern styling for the tracker.
  * `app.js` - Handles API calls and UI logic.
* **`/backend`**:
  * `TransactionServer.java` - The main server handling GET, POST, and DELETE requests.
  * `DBConnection.java` - Manages the JDBC connection to MySQL.

---

## ⚙️ Setup & Installation

### 1. Database Setup
Create a database named `personal_finance_tracker` and run the following SQL command in MySQL Workbench:

```sql
CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    type VARCHAR(50) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
