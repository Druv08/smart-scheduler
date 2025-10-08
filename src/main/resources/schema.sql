-- ===========================
-- Smart Scheduler Database Schema
-- ===========================

-- Users table with all required columns
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    role TEXT NOT NULL
);

-- Add a default admin user
INSERT OR IGNORE INTO users (username, password, role) 
VALUES ('admin', 'admin123', 'ADMIN');

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    room_name TEXT UNIQUE NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0)
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_code TEXT UNIQUE NOT NULL,
    course_name TEXT NOT NULL,
    faculty_username TEXT NOT NULL,
    max_students INTEGER NOT NULL CHECK (max_students > 0),
    FOREIGN KEY (faculty_username) REFERENCES users(username)
);

-- Timetable table: actual scheduling
CREATE TABLE IF NOT EXISTS timetable (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    course_id INTEGER,
    room_id INTEGER,
    day_of_week TEXT,
    start_time TEXT,
    end_time TEXT,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (room_id) REFERENCES rooms(id)
);

DELETE FROM users;
