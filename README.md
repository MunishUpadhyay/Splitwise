# Expense Splitter App

A Java Swing-based desktop application for managing shared expenses among groups. This application provides an intuitive GUI for adding users, managing trips, recording expenses, and tracking payment balances.

## 🚀 Features

### Core Functionality
- **User Management**: Add and manage users with name and email
- **Trip Management**: Create and manage expense groups/trips
- **Expense Recording**: Log expenses with automatic splitting
- **Payment Tracking**: Record payments between users
- **Balance Calculation**: View who owes what to whom
- **User Overview**: View all registered users

### User Interface
- **Clean Swing GUI**: Modern dark-themed interface
- **Tab-based Navigation**: Easy switching between different functions:
  - Add User
  - Manage Trips
  - Add Expense
  - Record Payment
  - View Balances
  - View Users

## 📁 Project Structure

Based on the project structure shown:

```
JAVAPROJECT/
├── .vscode/                    # VS Code configuration
├── Splitwise/                  # Main project folder
│   ├── .vscode/               # VS Code settings
│   ├── bin/                   # Compiled Java classes
│   ├── lib/                   # External libraries
│   └── src/                   # Source code
│       ├── App.class          # Main application class
│       ├── App.java           # Main application entry point
│       ├── Database-Manager.java  # Database operations
│       └── ExpenseSplitterS...    # Expense splitter implementation
└── README.md                  # This file
```

## 🛠️ Installation & Setup

### Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher
- **SQL Database**: MySQL, PostgreSQL, or SQLite
- **JDBC Driver**: Database-specific JDBC driver (included in lib/ folder)
- **VS Code** (optional): For development
- **Java Extension Pack** for VS Code (recommended)

### Installation Steps

1. **Clone or Download the Project**
   ```bash
   git clone <repository-url>
   cd JAVAPROJECT/Splitwise
   ```

2. **Database Setup**
   ```bash
   # Create database (MySQL example)
   mysql -u root -p
   CREATE DATABASE expense_splitter;
   USE expense_splitter;
   
   # Run the database schema script (if provided)
   source database/schema.sql;
   ```

3. **Configure Database Connection**
   - Edit database connection settings in `Database-Manager.java`
   - Update connection URL, username, and password:
   ```java
   String url = "jdbc:mysql://localhost:3306/expense_splitter";
   String username = "your_username";
   String password = "your_password";
   ```

4. **Compile the Application**
   ```bash
   # Navigate to the src directory
   cd src
   
   # Compile all Java files (ensure JDBC driver is in classpath)
   javac -cp "../lib/*:." *.java
   
   # Or compile specific files
   javac -cp "../lib/*:." App.java Database-Manager.java ExpenseSplitterS*.java
   ```

5. **Run the Application**
   ```bash
   # From the src directory (include JDBC driver in classpath)
   java -cp "../lib/*:." App
   ```

### Using VS Code

1. Open the `Splitwise` folder in VS Code
2. Ensure Java Extension Pack is installed
3. Press `F5` to run the application
4. Or use the "Run Java" button in the editor

## 🎯 How to Use

### Getting Started

1. **Launch the Application**
   - Run the `App.java` file
   - The main window will open with navigation tabs

2. **Add Users** (First Tab)
   - Click on the "Add User" tab
   - Enter user details:
     - **Name**: Enter the user's name
     - **Email**: Enter the user's email address
   - Click "Add User" button to save

3. **Manage Trips** (Second Tab)
   - Create new expense groups or trips
   - Organize expenses by different events or time periods

4. **Add Expenses** (Third Tab)
   - Record new expenses with details
   - Specify who paid and how to split the cost

5. **Record Payments** (Fourth Tab)
   - Log payments made between users
   - Update balances when someone pays their share

6. **View Balances** (Fifth Tab)
   - See current balance status
   - Check who owes money to whom

7. **View Users** (Sixth Tab)
   - See all registered users
   - Manage user information

### Example Workflow

```
Step 1: Add Users
- Add "Alice" with email "alice@example.com"
- Add "Bob" with email "bob@example.com"
- Add "Charlie" with email "charlie@example.com"

Step 2: Create a Trip
- Create "Weekend Trip" group

Step 3: Add Expenses
- Alice paid $60 for dinner
- Bob paid $30 for gas
- Charlie paid $45 for groceries

Step 4: View Balances
- Check who owes what to whom
- See settlement suggestions

Step 5: Record Payments
- Mark payments as completed when settled
```

## GUI of the App

![App](https://raw.githubusercontent.com/MunishUpadhyay/Material/refs/heads/main/Screenshot%202025-07-18%20231932.png)

## 🔧 Key Components

### Main Classes
- **`App.java`**: Main application class and entry point
- **`Database-Manager.java`**: Handles data persistence and database operations
- **`ExpenseSplitterS...`**: Core expense splitting logic and calculations

### GUI Features
- **Dark Theme**: Modern dark interface for better user experience
- **Tab Navigation**: Organized workflow with clear sections
- **Form Validation**: Input validation for user data
- **Responsive Layout**: Adapts to different window sizes

## 💾 Data Management

The application uses SQL database for persistent data storage through the `Database-Manager.java` class:

### Database Features
- **SQL Database**: All data is stored in a relational SQL database
- **JDBC Integration**: Uses Java Database Connectivity (JDBC) for database operations
- **Structured Storage**: Organized tables for users, expenses, payments, and trips
- **Data Integrity**: Referential integrity maintained through foreign key constraints

### Database Operations
- **User Storage**: Persistent storage of user information in SQL tables
- **Expense Records**: Saving and retrieving expense data with SQL queries
- **Balance Calculations**: Real-time balance calculations using SQL aggregations
- **Payment History**: Complete audit trail of all payments and settlements
- **Trip Management**: Trip/group data stored with relationships to users and expenses

### Database Schema (Typical Structure)
```sql
-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trips table
CREATE TABLE trips (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Expenses table
CREATE TABLE expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    trip_id INT,
    payer_id INT,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    expense_date DATE,
    FOREIGN KEY (trip_id) REFERENCES trips(id),
    FOREIGN KEY (payer_id) REFERENCES users(id)
);

-- Payments table
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    from_user_id INT,
    to_user_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_date DATE,
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id)
);
```

## 🎨 User Interface

### Main Window Features
- **Navigation Tabs**: Six main sections for different operations
- **Form Fields**: Clean input forms with labels
- **Action Buttons**: Clearly labeled buttons for each operation
- **Dark Theme**: Professional dark color scheme

### Add User Interface
- **Name Field**: Text input for user's full name
- **Email Field**: Text input for user's email address
- **Add User Button**: Saves the user information

## 🔍 Technical Details

### Architecture
- **MVC Pattern**: Separation of GUI, logic, and data management
- **Swing Components**: Standard Java Swing for cross-platform GUI
- **File-based Storage**: Uses Database-Manager for data persistence

### Key Features
- **Tab-based Interface**: Easy navigation between different functions
- **Form Validation**: Ensures data integrity
- **Error Handling**: Graceful handling of user input errors
- **Data Persistence**: Saves data between application sessions

## 🧪 Testing

### Manual Testing
1. **User Addition**
   - Test adding users with valid name and email
   - Verify data persistence after restart

2. **Navigation**
   - Test switching between all tabs
   - Verify UI responsiveness

3. **Data Validation**
   - Test with empty fields
   - Test with invalid email formats

### Running Tests
```bash
# Compile and run the application
cd src
javac *.java
java App

# Test each feature systematically
```

## 🚨 Troubleshooting

### Common Issues

**Application won't start:**
- Check Java version: `java -version`
- Ensure all `.java` files are compiled
- Check for missing dependencies

**GUI not displaying properly:**
- Verify screen resolution settings
- Check Java Swing installation
- Try running with different look-and-feel

**Data not saving:**
- Check database connection settings
- Verify SQL database is running and accessible
- Check database permissions and user credentials
- Review Database-Manager logs for SQL errors
- Ensure database tables exist and have correct schema

**Database connection issues:**
- Verify database server is running
- Check connection URL, username, and password
- Ensure JDBC driver is in classpath
- Test database connectivity independently
- Review firewall and network settings

**Compilation errors:**
- Ensure all Java files are in the correct directory
- Check for missing imports
- Verify Java classpath includes JDBC driver
- Ensure database-related imports are available

## 📈 Future Enhancements

### Database Testing
Test database connection:
```bash
# Test database connectivity
java -cp "../lib/*:." Database-Manager
```

### SQL Query Testing
You can test SQL queries directly:
```sql
-- Check user data
SELECT * FROM users;

-- View expense summaries
SELECT u.name, SUM(e.amount) as total_paid 
FROM users u 
JOIN expenses e ON u.id = e.payer_id 
GROUP BY u.id, u.name;

-- Check balance status
SELECT * FROM payments ORDER BY payment_date DESC;
```


### Technical Improvements
- **Database Optimization**: Index optimization and query performance tuning
- **Connection Pooling**: Implement connection pooling for better performance
- **Exception Handling**: Better error handling and user feedback
- **Input Validation**: More robust form validation with SQL injection prevention
- **Performance**: Optimize SQL queries for larger datasets
- **Security**: Add data encryption and secure database connections
- **Backup/Recovery**: Implement database backup and recovery features

### Planned Features
- **Enhanced UI**: More modern look with improved styling
- **Advanced SQL Features**: Stored procedures and database triggers
- **Export Functionality**: Export reports to PDF/Excel with SQL data
- **Email Notifications**: Send payment reminders with database integration
- **Mobile Companion**: Mobile app with shared database
- **Multi-database Support**: Support for different SQL databases (MySQL, PostgreSQL, SQLite)
- **Database Analytics**: Advanced reporting and analytics using SQL queries

## 🤝 Contributing

### Development Setup
1. Fork the repository
2. Set up your SQL database (MySQL/PostgreSQL/SQLite)
3. Configure database connection in `Database-Manager.java`
4. Open `Splitwise` folder in VS Code
5. Install Java Extension Pack
6. Ensure JDBC driver is in the lib/ folder
7. Make your changes
8. Test thoroughly (including database operations)
9. Submit a pull request

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add comments for complex logic and SQL queries
- Maintain consistent indentation
- Use prepared statements for all SQL operations
- Include proper error handling for database operations

## 🙏 Acknowledgments

- Java Swing documentation
- JDBC documentation and best practices
- SQL database documentation (MySQL/PostgreSQL/SQLite)
- VS Code Java development tools
- Open source community contributions

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Developer** - Initial work