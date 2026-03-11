# Employee Tracking System (ETS)

A comprehensive Spring Boot application for managing employee information, attendance, and leave management with role-based access control (Admin and Employee).

## Features

### Admin Dashboard
- **Employee Management**: Add, edit, delete employees
- **Department Management**: Create and manage departments
- **Attendance Tracking**: View and manage attendance records
- **Leave Management**: Approve or reject leave requests
- **User Management**: Enable/disable user accounts
- **Dashboard**: Overview statistics

### Employee Dashboard
- **Profile**: View personal information
- **Attendance**: Check-in/Check-out, view attendance history
- **Leave Management**: Apply for leave, view leave status
- **Dashboard**: Quick overview and pending requests

## Tech Stack

- **Backend**: Spring Boot 4.0.3
- **Database**: MySQL 8.0+
- **Frontend**: Thymeleaf with Bootstrap 5
- **Security**: Spring Security with BCrypt password encoding
- **Build Tool**: Maven

## Prerequisites

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## Setup Instructions

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE employee_tracking_system;
```

### 2. Configuration

Update `src/main/resources/application.yaml` with your MySQL credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/employee_tracking_system
    username: root
    password: your_password
```

### 3. Build the Application

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Default Credentials

### Admin Account
- Username: `admin`
- Password: `admin123`

### Employee Accounts
- **Employee 1**
  - Username: `emp1`
  - Password: `emp123`
  
- **Employee 2**
  - Username: `emp2`
  - Password: `emp123`
  
- **Employee 3**
  - Username: `emp3`
  - Password: `emp123`

## Project Structure

```
src/main/java/com/data/ets/
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── CustomUserDetailsService.java
│   └── DataInitializer.java
├── controller/            # REST and View Controllers
│   ├── HomeController.java
│   ├── AdminController.java
│   ├── EmployeeController.java
│   ├── AttendanceController.java
│   └── LeaveController.java
├── model/                 # JPA Entities
│   ├── User.java
│   ├── Employee.java
│   ├── Department.java
│   ├── Attendance.java
│   └── Leave.java
├── repository/            # JPA Repositories
├── service/              # Business Logic
├── dto/                  # Data Transfer Objects
└── EtsApplication.java

src/main/resources/
├── application.yaml      # Application configuration
└── templates/           # Thymeleaf templates
    ├── login.html
    ├── access-denied.html
    ├── admin/           # Admin templates
    │   ├── dashboard.html
    │   ├── employees/
    │   ├── departments/
    │   ├── attendance/
    │   ├── leaves/
    │   └── users/
    └── employee/        # Employee templates
        ├── dashboard.html
        ├── attendance.html
        ├── leaves.html
        ├── apply-leave.html
        └── profile.html
```

## API Endpoints

### Admin Routes
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/employees` - List all employees
- `GET /admin/employees/add` - Add employee form
- `POST /admin/employees/save` - Save employee
- `GET /admin/employees/edit/{id}` - Edit employee form
- `POST /admin/employees/update/{id}` - Update employee
- `GET /admin/employees/delete/{id}` - Delete employee
- `GET /admin/departments` - List departments
- `GET /admin/attendance` - List attendance records
- `GET /admin/leaves` - List leave requests
- `GET /admin/leaves/pending` - Pending leave approvals
- `POST /admin/leaves/approve/{id}` - Approve leave
- `POST /admin/leaves/reject/{id}` - Reject leave
- `GET /admin/users` - List users

### Employee Routes
- `GET /employee/dashboard` - Employee dashboard
- `GET /employee/profile` - View profile
- `GET /employee/attendance` - View attendance
- `POST /employee/attendance/check-in` - Check-in
- `POST /employee/attendance/check-out/{id}` - Check-out
- `GET /employee/leaves` - View leaves
- `GET /employee/leaves/apply` - Apply leave form
- `POST /employee/leaves/save` - Submit leave request

## Database Schema

### Users Table
- Stores user credentials and authentication information
- Columns: id, username, password, email, firstName, lastName, role, enabled, createdAt, updatedAt

### Employees Table
- Stores detailed employee information
- Columns: id, userId, employeeId, phoneNumber, dateOfBirth, gender, departmentId, position, salary, dateOfJoining, address, city, state, zipCode, status

### Departments Table
- Stores department information
- Columns: id, name, description, createdAt, updatedAt

### Attendance Table
- Stores attendance records
- Columns: id, employeeId, attendanceDate, checkInTime, checkOutTime, status, remarks, createdAt

### Leaves Table
- Stores leave requests
- Columns: id, employeeId, leaveType, startDate, endDate, reason, status, approvedBy, approvalDate, createdAt, updatedAt

## Security Features

- Role-based access control (RBAC)
- BCrypt password encoding
- Session management
- CSRF protection
- Form validation

## Future Enhancements

- Email notifications for leave approvals
- Advanced reporting and analytics
- Mobile app support
- Integration with payroll system
- Performance appraisal module
- Document management system

## Support

For issues or questions, please contact the development team.

## License

This project is licensed under the MIT License.

