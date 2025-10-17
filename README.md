# Smart Scheduler

A comprehensive web-based academic scheduling application designed to manage university course scheduling, room bookings, and user administration.

## Project Overview

Smart Scheduler is built using modern Spring Boot architecture with SQLite database integration, providing an intuitive solution for educational institutions to manage their scheduling needs efficiently.

## Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.1.4
- SQLite Database
- Maven Build System
- JUnit Testing Framework
- BCrypt Security

**Frontend:**
- HTML5
- CSS3 (Manrope Font)
- JavaScript
- Responsive Design
- Thymeleaf Template Engine

## Features

### Current Implementation
- User Authentication and Role Management
- Course Management System
- Room and Building Administration
- Booking Management Interface
- Responsive User Interface
- Database Integration with SQLite
- Comprehensive Test Coverage

### User Roles
- **Students:** Druv, Carlyn, Allan, Amrita
- **Professors:** Dr. Prince (Course Instructor)
- **Administrators:** System management capabilities

### Course Catalog
- **DSA** (Data Structures and Algorithms)
- **COA** (Computer Organization and Architecture)
- **APP** (Application Development)
- **TBVP** (Technology-Based Virtual Programming)
- **OS** (Operating Systems)

All courses are instructed by our esteemed faculty member **Dr. Prince**.

## Project Structure

```
smart-scheduler/
├── src/
│   ├── main/
│   │   ├── java/com/druv/scheduler/
│   │   │   ├── SmartSchedulerApplication.java    # Main Spring Boot application
│   │   │   ├── DatabaseConfig.java               # Database configuration
│   │   │   ├── WebServer.java                    # Web controller and routing
│   │   │   ├── Database.java                     # Database initialization
│   │   │   ├── SchedulerService.java             # Core business logic
│   │   │   ├── AuthService.java                  # Authentication services
│   │   │   ├── dao/                              # Data Access Objects
│   │   │   │   ├── UserDAO.java & UserDAOImpl.java
│   │   │   │   ├── CourseDAO.java
│   │   │   │   ├── RoomDAO.java
│   │   │   │   └── TimetableDAO.java
│   │   │   ├── managers/                         # Administrative services
│   │   │   │   ├── UserManager.java
│   │   │   │   ├── CourseManager.java
│   │   │   │   ├── RoomManager.java
│   │   │   │   └── TimetableManager.java
│   │   │   └── Security.java                     # Security utilities
│   │   └── resources/
│   │       ├── public/                           # Frontend assets
│   │       │   ├── *.html                        # Application pages
│   │       │   ├── css/
│   │       │   │   ├── common.css                # Global styling
│   │       │   │   └── bookings.css              # Booking-specific styles
│   │       │   └── js/
│   │       │       └── login.js                  # Authentication handling
│   │       └── application.properties            # Spring configuration
│   └── test/                                     # Test suites
├── target/                                       # Compiled classes
└── pom.xml                                       # Maven dependencies
```

## Database Schema

### Tables
- **users:** Authentication and role management
- **courses:** Academic course information
- **rooms:** Facility and building management
- **timetable:** Schedule and time slot management

### Building Categories
- University Building
- TP-1 (Technology Park 1)
- TP-2 (Technology Park 2)

## Installation and Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- Git

### Installation Steps

1. **Clone the repository:**
```bash
git clone https://github.com/Druv08/smart-scheduler.git
cd smart-scheduler
```

2. **Navigate to project directory:**
```bash
cd smart-scheduler
```

3. **Install dependencies:**
```bash
mvn clean install
```

4. **Run the application:**
```bash
mvn spring-boot:run
```

5. **Access the application:**
Open your web browser and navigate to: `http://localhost:8080`

## Application Pages

- **Home Page:** Application overview and introduction
- **Login:** User authentication interface
- **Dashboard:** Main application hub
- **Courses:** Course management and catalog
- **Rooms:** Room directory and management
- **Bookings:** Booking system and calendar
- **Users:** User administration panel
- **Timetable:** Schedule viewing and management
- **Profile:** User settings and preferences

## Testing

Run the comprehensive test suite:
```bash
mvn test
```

Current test coverage: **9/9 tests passing**

## Development Status

### Completed Features
- Complete frontend user interface
- Database schema and initialization
- Spring Boot application architecture
- User authentication framework
- Responsive design implementation
- Navigation system across all pages
- Sample data integration

### In Development
- Backend API integration
- Real-time CRUD operations
- Advanced scheduling algorithms
- User session management
- Automated conflict detection

## Contributing

This project follows standard Java and Spring Boot development practices. Contributions should maintain:
- Clean code architecture
- Comprehensive testing
- Consistent styling and user experience
- Proper documentation

## Academic Recognition

We extend our sincere gratitude to **Dr. Prince** for his invaluable guidance and mentorship throughout this project development. His expertise in software engineering and academic excellence has been instrumental in shaping this application.

Dr. Prince serves as the primary instructor for APP Subject in our system, demonstrating his versatility and comprehensive knowledge across multiple computer science domain.

## License

This project is developed for academic purposes as part of coursework requirements and to prove we didnt cheat lol.

## Contact

**Developer:** Druv
**Repository:** https://github.com/Druv08/smart-scheduler
**Project Type:** Academic Software Engineering Project

---

**Smart Scheduler - Revolutionizing Academic Scheduling Management**