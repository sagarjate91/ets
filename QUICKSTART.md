# Quick Start Guide - Employee Tracking System

## Prerequisites
- JDK 17 or higher installed
- MySQL Server 8.0 or higher installed and running
- Maven 3.6+ installed
- Git (optional)

## Step-by-Step Setup

### Step 1: Create Database
Open MySQL and run:
```sql
CREATE DATABASE employee_tracking_system;
USE employee_tracking_system;
```

### Step 2: Configure Database Connection
Edit `src/main/resources/application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employee_tracking_system
    username: root                    # Change if different
    password: your_password           # Your MySQL password
```

### Step 3: Build the Project
```bash
cd "C:\Users\sagar\Documents\D Project\ets"
mvn clean install
```

### Step 4: Run the Application
```bash
mvn spring-boot:run
```

Wait for the message: "Started EtsApplication in X seconds"

### Step 5: Access the Application
Open your browser and go to:
```
http://localhost:8080/login
```

### Step 6: Login
**Admin Account:**
- Username: `admin`
- Password: `admin123`

**Employee Accounts:**
- Username: `emp1`, Password: `emp123`
- Username: `emp2`, Password: `emp123`
- Username: `emp3`, Password: `emp123`

## What You Can Do

### As Admin
✅ Manage employees (Add, Edit, View, Delete)
✅ Manage departments
✅ Track attendance records
✅ Approve/Reject leave requests
✅ Manage user accounts (Enable/Disable)
✅ View dashboard statistics

### As Employee
✅ View personal profile
✅ Check attendance history
✅ Apply for leave
✅ Track leave status
✅ Check-in/Check-out

## Accessing Different Sections

### Admin Dashboard
- URL: `http://localhost:8080/admin/dashboard`
- Sidebar menu for navigation

### Employee Dashboard
- URL: `http://localhost:8080/employee/dashboard`
- Quick action buttons

### Key URLs
| Function | URL |
|----------|-----|
| Login | `/login` |
| Admin Dashboard | `/admin/dashboard` |
| Employee Dashboard | `/employee/dashboard` |
| Employees List | `/admin/employees` |
| Departments | `/admin/departments` |
| Attendance | `/admin/attendance` |
| Leave Requests | `/admin/leaves/pending` |
| My Profile | `/employee/profile` |
| My Attendance | `/employee/attendance` |
| My Leaves | `/employee/leaves` |

## Useful Tasks

### Add a New Employee (as Admin)
1. Go to `/admin/employees`
2. Click "Add Employee"
3. Fill in the form
4. Select department
5. Click "Save Employee"

### Apply for Leave (as Employee)
1. Go to `/employee/leaves`
2. Click "Apply Leave"
3. Select leave type
4. Choose dates
5. Enter reason
6. Submit

### Check Attendance (as Employee)
1. Go to `/employee/attendance`
2. Click "Check In" button
3. View your attendance history

### Approve Leave (as Admin)
1. Go to `/admin/leaves/pending`
2. Review leave request
3. Click "Approve" or "Reject"

## Troubleshooting

### Application Won't Start
```bash
# Check if port 8080 is in use
# Option 1: Change port in application.yaml
server:
  port: 8081

# Option 2: Kill process using port 8080
# Windows PowerShell:
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Can't Connect to Database
```bash
# Check MySQL is running
# Windows:
mysqld --console

# Or start MySQL service
net start MySQL80
```

### Forgot Password
- Use admin/admin123 to login
- Check sample data in DataInitializer.java

### Database Not Creating Tables
```yaml
# Make sure this is set in application.yaml:
spring:
  jpa:
    hibernate:
      ddl-auto: update    # This auto-creates tables
```

## Sample Data Included

The application comes with pre-loaded:
- 1 Admin user
- 3 Employee users
- 3 Departments (IT, HR, Sales)
- Sample attendance records
- Sample leave requests

## Project Structure

```
ets/
├── src/
│   ├── main/
│   │   ├── java/com/data/ets/
│   │   │   ├── config/           (Security, Database init)
│   │   │   ├── controller/       (Web controllers)
│   │   │   ├── model/            (Database entities)
│   │   │   ├── repository/       (Data access layer)
│   │   │   ├── service/          (Business logic)
│   │   │   └── dto/              (Data transfer objects)
│   │   └── resources/
│   │       ├── application.yaml  (Configuration)
│   │       └── templates/        (Thymeleaf templates)
│   └── test/
├── pom.xml                        (Maven dependencies)
├── README.md                      (Full documentation)
├── IMPLEMENTATION_GUIDE.md        (Technical details)
└── QUICKSTART.md                  (This file)
```

## Key Files to Know

| File | Purpose |
|------|---------|
| `application.yaml` | Database and app configuration |
| `SecurityConfig.java` | Authentication setup |
| `DataInitializer.java` | Sample data creation |
| `AdminController.java` | Admin endpoints |
| `EmployeeController.java` | Employee endpoints |
| `templates/admin/` | Admin UI pages |
| `templates/employee/` | Employee UI pages |

## Common Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Create executable JAR
mvn package

# Run the JAR
java -jar target/ets-0.0.1-SNAPSHOT.jar
```

## Next Steps

1. ✅ Install prerequisites
2. ✅ Create database
3. ✅ Update configuration
4. ✅ Build project
5. ✅ Run application
6. ✅ Login and explore

## Support

For detailed information, see:
- README.md - Full project documentation
- IMPLEMENTATION_GUIDE.md - Technical architecture
- Source code comments in Java files

## Performance Tips

- Database queries are optimized
- Lazy loading for relationships
- Pagination support for large datasets
- Session caching enabled

## Security Notes

- Passwords are BCrypt encrypted
- CSRF protection enabled
- Role-based access control
- Session timeouts configured
- SQL injection prevention via JPA

---

**Happy coding! 🚀**

