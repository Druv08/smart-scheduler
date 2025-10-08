# Smart Scheduler

A modern web-based scheduling system for educational institutions built with Java and SQLite, featuring a responsive dashboard interface.

## Features

- **User Authentication & Authorization**
  - Role-based access control (Admin, Faculty, Student)
  - Secure password hashing
  - Session management

- **Resource Management**
  - Course creation and assignment
  - Classroom and lab management
  - Faculty scheduling
  - Real-time booking system

- **Dashboard Interface**
  - Responsive modern UI
  - Real-time statistics
  - Quick access navigation
  - Dark mode design

## Tech Stack

- **Backend**
  - Java 17
  - SQLite Database
  - Maven
  - Spark Java Web Framework
  - JUnit 5 for testing

- **Frontend**
  - HTML5/CSS3
  - Modern JavaScript
  - Responsive Design

## Project Structure

```
smart-scheduler/
├── src/
│   ├── main/
│   │   ├── java/com/druv/scheduler/
│   │   │   ├── Main.java
│   │   │   ├── Database.java
│   │   │   ├── AuthService.java
│   │   │   ├── SchedulerService.java
│   │   │   └── dao/
│   │   └── resources/
│   │       ├── public/
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── index.html
│   │       └── schema.sql
│   └── test/
│       └── java/com/druv/scheduler/
├── pom.xml
└── README.md
```

## Setup & Installation

1. **Prerequisites**
   - Java 17 or higher
   - Maven 3.6+
   - Git

2. **Clone the repository**
```bash
git clone https://github.com/Druv08/smart-scheduler.git
cd smart-scheduler
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn exec:java
```

5. **Access the application**
   - Open `http://localhost:8080` in your browser
   - Default admin credentials:
     - Username: admin
     - Password: admin123

## Development

### Running Tests
```bash
mvn test
```

### Database Schema
The application automatically initializes SQLite database with required tables on first run.

### API Endpoints
- POST `/api/auth/login` - User authentication
- GET `/api/users` - List all users
- POST `/api/rooms` - Add new room
- GET `/api/courses` - List all courses
- POST `/api/bookings` - Create new booking

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT License - See LICENSE file for details

## Author

Druv Khurana