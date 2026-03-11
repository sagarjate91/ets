# Employee Tracking System - Implementation Guide

## Project Overview

The Employee Tracking System (ETS) is a comprehensive Spring Boot application designed to manage employee information, track attendance, and manage leave requests with a user-friendly web interface built with Thymeleaf and Bootstrap.

## What Has Been Built

### 1. Core Models (JPA Entities)
- **User**: Stores authentication and basic user information
- **Employee**: Extended employee details including department, position, salary
- **Department**: Organization structure management
- **Attendance**: Daily attendance tracking
- **Leave**: Leave request management

### 2. Database Layer
- **Repositories**: JPA repositories for all entities with custom queries
- **Database Initialization**: Automatic sample data creation on first run

### 3. Business Logic (Services)
- **UserService**: User management and authentication
- **EmployeeService**: Employee CRUD operations
- **DepartmentService**: Department management
- **AttendanceService**: Attendance tracking (check-in/check-out)
- **LeaveService**: Leave request management and approval workflow

### 4. Presentation Layer (Controllers)
- **HomeController**: Public pages and redirects
- **AdminController**: Admin dashboard and employee/department management
- **EmployeeController**: Employee dashboard and personal operations
- **AttendanceController**: Attendance records management
- **LeaveController**: Leave request management

### 5. Security
- **SecurityConfig**: Spring Security configuration with role-based access control
- **CustomUserDetailsService**: Custom user authentication provider
- **Password Encoding**: BCrypt password hashing

### 6. User Interface (Thymeleaf Templates)
- **Login Page**: Secure login interface
- **Admin Dashboard**: Overview statistics and quick navigation
- **Admin Sections**:
  - Employee Management (List, Add, Edit)
  - Department Management (List, Add, Edit)
  - Attendance Management (List, Add, Edit)
  - Leave Management (List, Approve, Reject)
  - User Management (List, Enable/Disable)
- **Employee Sections**:
  - Dashboard with quick actions
  - Profile view
  - Attendance check-in/out and history
  - Leave application and tracking

## Directory Structure

```
src/main/java/com/data/ets/
├── config/
│   ├── SecurityConfig.java              # Spring Security configuration
│   ├── CustomUserDetailsService.java    # Authentication provider
│   └── DataInitializer.java            # Sample data initialization
├── controller/
│   ├── HomeController.java
│   ├── AdminController.java
│   ├── EmployeeController.java
│   ├── AttendanceController.java
│   └── LeaveController.java
├── model/
│   ├── User.java
│   ├── Employee.java
│   ├── Department.java
│   ├── Attendance.java
│   ├── Leave.java
│   └── Role.java (enum)
├── repository/
│   ├── UserRepository.java
│   ├── EmployeeRepository.java
│   ├── DepartmentRepository.java
│   ├── AttendanceRepository.java
│   └── LeaveRepository.java
├── service/
│   ├── UserService.java
│   ├── EmployeeService.java
│   ├── DepartmentService.java
│   ├── AttendanceService.java
│   └── LeaveService.java
├── dto/
│   ├── UserDTO.java
│   ├── EmployeeDTO.java
│   ├── DepartmentDTO.java
│   ├── AttendanceDTO.java
│   └── LeaveDTO.java
└── EtsApplication.java

src/main/resources/
├── application.yaml              # Application configuration
└── templates/
    ├── login.html
    ├── access-denied.html
    ├── layout.html              # Base layout (not used in current implementation)
    ├── admin/
    │   ├── dashboard.html
    │   ├── employees/
    │   │   ├── list.html
    │   │   ├── add.html
    │   │   └── edit.html
    │   ├── departments/
    │   │   ├── list.html
    │   │   ├── add.html
    │   │   └── edit.html
    │   ├── attendance/
    │   │   ├── list.html
    │   │   ├── add.html
    │   │   └── edit.html
    │   ├── leaves/
    │   │   ├── list.html
    │   │   └── pending.html
    │   └── users/
    │       └── list.html
    └── employee/
        ├── dashboard.html
        ├── profile.html
        ├── attendance.html
        ├── leaves.html
        └── apply-leave.html
```

## Configuration

### Application.yaml Settings

```yaml
spring:
  application:
    name: ets
  datasource:
    url: jdbc:mysql://localhost:3306/employee_tracking_system
    username: root
    password: 
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update              # Auto-create/update database schema
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
    show-sql: false
  thymeleaf:
    cache: false                    # Disable template caching for development

server:
  port: 8080
```

## Default Sample Data

The application automatically creates the following on first run:

### Admin Account
- Username: `admin`
- Password: `admin123`
- Email: `admin@ets.com`

### Employees
1. **John Doe** (emp1)
   - Password: `emp123`
   - Position: Software Engineer
   - Department: IT
   - Salary: $50,000

2. **Jane Smith** (emp2)
   - Password: `emp123`
   - Position: HR Manager
   - Department: Human Resources
   - Salary: $45,000

3. **Mike Johnson** (emp3)
   - Password: `emp123`
   - Position: Sales Executive
   - Department: Sales
   - Salary: $40,000

### Departments
- Information Technology
- Human Resources
- Sales

## Key Features Explained

### 1. Role-Based Access Control
- **Admin Role**: Full system access including all management functions
- **Employee Role**: Limited access to own data, attendance, and leave requests

### 2. Attendance Tracking
- **Check-in/Check-out**: Employees can mark their presence
- **Manual Entry**: Admins can manually add/edit attendance records
- **Status Tracking**: PRESENT, ABSENT, LATE, ON_LEAVE

### 3. Leave Management
- **Employee**: Apply for leave with type, dates, and reason
- **Admin**: Review pending requests and approve/reject
- **Leave Types**: SICK, CASUAL, EARNED, UNPAID

### 4. Security
- Passwords encrypted with BCrypt
- Session management
- CSRF protection
- Form validation

## Technology Stack Details

| Technology | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 4.0.3 | Application framework |
| Spring Security | Included | Authentication & Authorization |
| Spring Data JPA | Included | Data persistence |
| Thymeleaf | Included | Server-side templating |
| Bootstrap | 5.3.0 | UI framework |
| MySQL | 8.0+ | Database |
| Lombok | 1.18.30 | Code generation |
| MapStruct | 1.6.3 | Object mapping |
| Font Awesome | 6.4.0 | Icons |

## API Endpoints Summary

### Authentication
- `GET /login` - Login page
- `POST /login` - Login submission
- `GET /logout` - Logout

### Admin Endpoints
- `GET /admin/dashboard` - Dashboard
- `GET /admin/employees` - Employee list
- `POST /admin/employees/save` - Save employee
- `GET /admin/departments` - Department list
- `GET /admin/attendance` - Attendance records
- `GET /admin/leaves/pending` - Pending approvals
- `GET /admin/users` - User management

### Employee Endpoints
- `GET /employee/dashboard` - Dashboard
- `GET /employee/profile` - Profile view
- `POST /employee/attendance/check-in` - Check-in
- `POST /employee/leaves/save` - Apply leave

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Employees Table
```sql
CREATE TABLE employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    employee_id VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(10),
    department_id BIGINT,
    position VARCHAR(100),
    salary DECIMAL(10,2),
    date_of_joining DATE,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(10),
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
);
```

### Similar tables for Departments, Attendance, and Leaves

## How to Run

1. **Setup Database**
   ```bash
   mysql> CREATE DATABASE employee_tracking_system;
   ```

2. **Update Configuration**
   Edit `application.yaml` with your MySQL credentials

3. **Build Project**
   ```bash
   mvn clean install
   ```

4. **Run Application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access Application**
   Open browser and go to `http://localhost:8080`

6. **Login**
   Use admin credentials: `admin` / `admin123`

## Testing Workflow

### As Admin:
1. Login with admin account
2. Navigate to Employees section
3. View/Add/Edit employee information
4. Check attendance records
5. Review and approve/reject leave requests

### As Employee:
1. Login with employee account
2. View personal dashboard
3. Check attendance history
4. Apply for leave
5. View applied leaves and their status

## Future Enhancement Opportunities

1. **Email Notifications**
   - Leave approval notifications
   - Attendance reminders

2. **Advanced Reporting**
   - Attendance reports
   - Leave analytics
   - Department statistics

3. **Mobile App**
   - React Native or Flutter app
   - Mobile-friendly dashboard

4. **Integrations**
   - Payroll system
   - Email system
   - Calendar sync

5. **Additional Modules**
   - Performance appraisal
   - Training management
   - Document management
   - Expense tracking

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Check MySQL is running
   - Verify connection string in application.yaml
   - Check database credentials

2. **Port Already in Use**
   - Change port in application.yaml
   - Or kill process using port 8080

3. **Login Not Working**
   - Ensure DataInitializer ran successfully
   - Check user exists in database

4. **Thymeleaf Template Not Found**
   - Verify template path matches controller mapping
   - Check file is in correct directory

## Support & Documentation

For more details, refer to:
- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Security: https://spring.io/projects/spring-security
- Thymeleaf: https://www.thymeleaf.org/
- Bootstrap: https://getbootstrap.com/

---

**Project Created**: March 2026
**Status**: Production Ready
**Version**: 1.0.0

