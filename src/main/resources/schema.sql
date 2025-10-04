-- ===========================
-- Smart Scheduler Database Schema
-- ===========================

-- Users table: stores login info and roles
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT CHECK(role IN ('student', 'faculty', 'admin')) NOT NULL
);

-- Rooms table: classrooms, labs, halls
CREATE TABLE IF NOT EXISTS rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_name TEXT NOT NULL UNIQUE,
    capacity INTEGER NOT NULL
);

-- Courses table: subjects/modules
CREATE TABLE IF NOT EXISTS courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_name TEXT NOT NULL UNIQUE,
    faculty_id INTEGER,
    FOREIGN KEY(faculty_id) REFERENCES users(id)
);

-- Timetable table: actual scheduling
CREATE TABLE IF NOT EXISTS timetable (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER,
    room_id INTEGER,
    day_of_week TEXT,
    start_time TEXT,
    end_time TEXT,
    FOREIGN KEY(course_id) REFERENCES courses(id),
    FOREIGN KEY(room_id) REFERENCES rooms(id)
);
