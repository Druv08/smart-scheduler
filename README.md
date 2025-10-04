# Smart Scheduler

A Java-based scheduling system for educational institutions to manage courses, rooms, and timetables.

## Features

- User management (students, faculty, admin)
- Course management
- Room allocation
- Timetable generation
- SQLite database storage

## Tech Stack

- Java 17
- Maven
- SQLite
- SLF4J/Logback

## Project Structure

```
smart-scheduler/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── druv/
│       │           └── scheduler/
│       │               ├── Main.java
│       │               ├── Database.java
│       │               └── DatabaseManager.java
│       └── resources/
│           └── schema.sql
├── pom.xml
└── README.md
```

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/Druv08/smart-scheduler.git
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn exec:java
```

## License

MIT License