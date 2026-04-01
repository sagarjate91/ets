# ETS – Employee Tracking System
## Project Documentation Report

---

## 1. Project Overview

**ETS (Employee Tracking System)** is a web-based HR and workforce management application built with **Spring Boot 4.0.3** and **Thymeleaf** for server-side rendering. It provides two distinct user experiences — one for administrators who manage the organization, and one for employees who access their own data and perform self-service actions.

**Technology Stack**

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 4.0.3 |
| Web Layer | Spring MVC, Thymeleaf |
| Persistence | Spring Data JPA, Hibernate |
| Database | H2 (file-based) |
| Security | Spring Security (BCrypt, form login) |
| Utilities | Lombok, MapStruct |
| API Docs | SpringDoc OpenAPI |
| Frontend | Bootstrap 5.3, Font Awesome 6.4 |

**Default Access**

| Role | Username | Password | Landing Page |
|---|---|---|---|
| Admin | `admin` | `admin123` | `/admin/dashboard` |
| Employee | `emp1` | `emp123` | `/employee/dashboard` |
| Employee | `emp2` | `emp123` | `/employee/dashboard` |
| Employee | `emp3` | `emp123` | `/employee/dashboard` |

---

## 2. Application Architecture

The application follows a classic **3-tier architecture**:

```
Browser (Thymeleaf HTML)
        ↓
  Controller Layer          (Spring MVC @Controller)
        ↓
  Service Layer             (Business logic)
        ↓
  Repository Layer          (Spring Data JPA)
        ↓
  H2 Database               (File-based persistence)
```

**Package structure**

```
com.data.ets/
├── config/          Security, authentication, sample data
├── controller/      HTTP request handlers
├── dto/             Data Transfer Objects (view layer contracts)
├── model/           JPA entities (database tables)
├── repository/      Database access interfaces
└── service/         Business logic
```

---

## 3. Modules

### 3.1 User Management Module

**Purpose:** Manages system accounts (login credentials, roles, and account status).

**Entity – `User`**

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| username | String | Unique login name |
| password | String | BCrypt-encrypted password |
| email | String | Unique email address |
| firstName | String | First name |
| lastName | String | Last name |
| role | Role (enum) | `ADMIN` or `EMPLOYEE` |
| enabled | Boolean | Account active status |
| createdAt | LocalDateTime | Auto-set on creation |
| updatedAt | LocalDateTime | Auto-set on update |

**Role enum values:** `ADMIN`, `EMPLOYEE`

**Admin capabilities (via `/admin/users`):**
- View all registered users
- Enable or disable any user account

**UserService operations:**
- Create new users with BCrypt password encoding
- Find users by username, email, ID, or role
- Enable / disable accounts
- Delete users

---

### 3.2 Employee Management Module

**Purpose:** Stores professional and personal details about each employee. Each employee record is linked one-to-one with a User account.

**Entity – `Employee`**

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| user | User | Linked login account (OneToOne) |
| employeeId | String | Unique employee code (e.g. `EMP001`) |
| phoneNumber | String | Contact number |
| dateOfBirth | LocalDate | Date of birth |
| gender | String | Gender |
| department | Department | Assigned department (ManyToOne) |
| position | String | Job title / position |
| salary | Double | Monthly salary |
| dateOfJoining | LocalDate | Joining date |
| address, city, state, zipCode | String | Residential address fields |
| status | String | `ACTIVE` (default) |
| createdAt / updatedAt | LocalDateTime | Audit timestamps |

**Admin capabilities (via `/admin/employees`):**
- View complete list of all employees
- Add a new employee (creates a User account and Employee profile together)
- Edit employee details (position, salary, department, phone)
- Delete an employee record

**Employee self-service (via `/employee/profile`):**
- View own profile details

---

### 3.3 Department Management Module

**Purpose:** Organises employees into departments. Each department can have many employees.

**Entity – `Department`**

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| name | String | Unique department name |
| description | String | Brief description |
| employees | List\<Employee\> | Employees in this department (OneToMany) |
| createdAt / updatedAt | LocalDateTime | Audit timestamps |

**Preconfigured sample departments:** Information Technology, Human Resources, Sales

**Admin capabilities (via `/admin/departments`):**
- View all departments
- Add new department (name + description)
- Edit department details
- Delete department

---

### 3.4 Attendance Management Module

**Purpose:** Tracks daily attendance for every employee, including check-in/check-out times and attendance status.

**Entity – `Attendance`**

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| employee | Employee | Linked employee (ManyToOne) |
| attendanceDate | LocalDate | Date of the record |
| checkInTime | LocalDateTime | Time of check-in |
| checkOutTime | LocalDateTime | Time of check-out |
| status | String | `PRESENT`, `ABSENT`, `LATE`, `ON_LEAVE` |
| remarks | String | Optional note |
| createdAt | LocalDateTime | Auto-set on creation |

**Admin capabilities (via `/admin/attendance`):**
- View all attendance records across all employees
- Manually add an attendance record
- Edit an existing attendance record (status, remarks)
- Delete a record

**Employee self-service (via `/employee/attendance`):**
- View own attendance history with counts (Present / Absent / Late)
- Check in (records current timestamp as checkInTime, status = PRESENT)
- Check out (records current timestamp as checkOutTime)

---

### 3.5 Leave Management Module

**Purpose:** Handles leave applications from employees, with an admin approval workflow.

**Entity – `Leave`**

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| employee | Employee | Applying employee (ManyToOne) |
| leaveType | String | `SICK`, `CASUAL`, `EARNED`, `UNPAID` |
| startDate | LocalDate | Leave start date |
| endDate | LocalDate | Leave end date |
| reason | String | Reason for leave |
| status | String | `PENDING` (default), `APPROVED`, `REJECTED` |
| approvedBy | String | Admin who acted on the request |
| approvalDate | LocalDateTime | Timestamp of approval/rejection |
| createdAt / updatedAt | LocalDateTime | Audit timestamps |

**Admin capabilities (via `/admin/leaves`):**
- View all leave requests
- View only pending leave requests
- Approve a leave request (records approver name and timestamp)
- Reject a leave request
- Delete a leave request

**Employee self-service (via `/employee/leaves`):**
- View own leave history with counts (Approved / Pending / Rejected)
- Apply for leave (select type, dates, and reason)
- New applications default to `PENDING` status

---

## 4. Security & Authentication Module

**Purpose:** Controls who can log in and what each role can access.

**Implementation:** Spring Security with form-based login and BCrypt password encoding.

**URL access rules:**

| URL Pattern | Allowed Roles |
|---|---|
| `/`, `/login`, `/css/**`, `/js/**` | Public (no login required) |
| `/admin/**` | `ROLE_ADMIN` only |
| `/employee/**` | `ROLE_EMPLOYEE` only |

**Login flow:**
1. User submits credentials at `/login`
2. `CustomUserDetailsService` loads user from the database by username
3. `CustomAuthenticationSuccessHandler` redirects admin users to `/admin/dashboard` and employees to `/employee/dashboard`
4. Failed logins redirect to `/login?error`
5. Logout clears the session and redirects to `/login?logout`
6. Unauthorized access redirects to `/access-denied`

---

## 5. Configuration Module

### 5.1 Application Configuration (`application.yaml`)

| Setting | Value |
|---|---|
| Server port | `8080` |
| Database URL | `jdbc:h2:file:./data/employee_tracking_system` |
| Database username | `sa` |
| DDL auto | `update` (auto-creates/migrates schema) |
| Thymeleaf cache | `false` (dev mode) |
| SQL logging | `false` |

### 5.2 Sample Data Initializer (`DataInitializer`)

Runs automatically on first startup (when the database is empty). Creates:
- 3 departments: Information Technology, Human Resources, Sales
- 1 admin user (`admin` / `admin123`)
- 3 employee users with full profiles (`emp1`, `emp2`, `emp3`)
- 2 sample attendance records (today)
- 2 sample leave requests (1 Pending, 1 Approved)

---

## 6. Data Transfer Objects (DTOs)

DTOs are used to pass data between the service layer and the view (Thymeleaf templates) without exposing raw JPA entities.

| DTO | Source Entity | Key Fields |
|---|---|---|
| `UserDTO` | User | id, username, email, fullName, role, enabled |
| `EmployeeDTO` | Employee + User + Department | id, employeeId, fullName, position, salary, departmentName, status |
| `DepartmentDTO` | Department | id, name, description, employeeCount |
| `AttendanceDTO` | Attendance + Employee | id, employeeName, attendanceDate, checkIn/Out, status, remarks |
| `LeaveDTO` | Leave + Employee | id, employeeName, leaveType, startDate, endDate, reason, status, approvedBy |

---

## 7. URL Reference

### Admin URLs

| URL | Method | Description |
|---|---|---|
| `/admin/dashboard` | GET | Dashboard with statistics |
| `/admin/employees` | GET | List all employees |
| `/admin/employees/add` | GET | New employee form |
| `/admin/employees/save` | POST | Create employee |
| `/admin/employees/edit/{id}` | GET | Edit employee form |
| `/admin/employees/update/{id}` | POST | Update employee |
| `/admin/employees/delete/{id}` | GET | Delete employee |
| `/admin/departments` | GET | List all departments |
| `/admin/departments/add` | GET | New department form |
| `/admin/departments/save` | POST | Create department |
| `/admin/departments/edit/{id}` | GET | Edit department form |
| `/admin/departments/update/{id}` | POST | Update department |
| `/admin/departments/delete/{id}` | GET | Delete department |
| `/admin/attendance` | GET | List all attendance records |
| `/admin/attendance/add` | GET | Add attendance form |
| `/admin/attendance/save` | POST | Save attendance record |
| `/admin/attendance/edit/{id}` | GET | Edit attendance form |
| `/admin/attendance/update/{id}` | POST | Update attendance |
| `/admin/attendance/delete/{id}` | GET | Delete attendance |
| `/admin/leaves` | GET | List all leave requests |
| `/admin/leaves/pending` | GET | List pending requests |
| `/admin/leaves/approve/{id}` | POST | Approve leave |
| `/admin/leaves/reject/{id}` | POST | Reject leave |
| `/admin/leaves/delete/{id}` | GET | Delete leave request |
| `/admin/users` | GET | List all users |
| `/admin/users/enable/{id}` | GET | Enable user account |
| `/admin/users/disable/{id}` | GET | Disable user account |

### Employee URLs

| URL | Method | Description |
|---|---|---|
| `/employee/dashboard` | GET | Employee dashboard |
| `/employee/profile` | GET | View own profile |
| `/employee/attendance` | GET | View own attendance |
| `/employee/attendance/check-in` | POST | Record check-in |
| `/employee/attendance/check-out/{id}` | POST | Record check-out |
| `/employee/leaves` | GET | View own leave history |
| `/employee/leaves/apply` | GET | Apply leave form |
| `/employee/leaves/save` | POST | Submit leave application |

### Common URLs

| URL | Method | Description |
|---|---|---|
| `/` | GET | Redirects based on role |
| `/login` | GET | Login page |
| `/logout` | POST | Log out |
| `/access-denied` | GET | Access denied page |

---

## 8. Database Schema

All tables are auto-created and migrated by Hibernate (`ddl-auto: update`).

```
users
  id, username, password, email, first_name, last_name, role, enabled,
  created_at, updated_at

departments
  id, name, description, created_at, updated_at

employees
  id, user_id (FK → users), employee_id, phone_number, date_of_birth,
  gender, department_id (FK → departments), position, salary,
  date_of_joining, address, city, state, zip_code, status,
  created_at, updated_at

attendance
  id, employee_id (FK → employees), attendance_date,
  check_in_time, check_out_time, status, remarks, created_at

leaves
  id, employee_id (FK → employees), leave_type, start_date, end_date,
  reason, status, approved_by, approval_date, created_at, updated_at
```

---

## 9. UI Templates Summary

All pages use a shared `layout.html` providing the navigation bar, role-based sidebar, and Bootstrap 5 styling.

| Template Path | Purpose |
|---|---|
| `login.html` | Login form |
| `access-denied.html` | Access denied error page |
| `admin/dashboard.html` | Admin home with summary stats |
| `admin/employees/list.html` | Employee list table |
| `admin/employees/add.html` | Add employee form |
| `admin/employees/edit.html` | Edit employee form |
| `admin/departments/list.html` | Department list |
| `admin/departments/add.html` | Add department form |
| `admin/departments/edit.html` | Edit department form |
| `admin/attendance/list.html` | Attendance records table |
| `admin/attendance/add.html` | Add attendance form |
| `admin/attendance/edit.html` | Edit attendance form |
| `admin/leaves/list.html` | All leave requests table |
| `admin/leaves/pending.html` | Pending requests with approve/reject |
| `admin/users/list.html` | Users list with enable/disable |
| `employee/dashboard.html` | Employee home with quick actions |
| `employee/profile.html` | Employee profile details |
| `employee/attendance.html` | Own attendance with stats + check-in/out |
| `employee/leaves.html` | Own leave history with stats |
| `employee/apply-leave.html` | Leave application form |
