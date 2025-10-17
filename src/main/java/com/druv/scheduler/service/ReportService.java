package com.druv.scheduler.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.druv.scheduler.Course;
import com.druv.scheduler.CourseDAO;
import com.druv.scheduler.Room;
import com.druv.scheduler.RoomDAO;
import com.druv.scheduler.TimetableDAO;
import com.druv.scheduler.TimetableEntry;
import com.druv.scheduler.UserDAO;

/**
 * Service for generating analytics reports and dashboard data
 */
@Service
public class ReportService {

    @Autowired
    private TimetableDAO timetableDAO;
    
    @Autowired
    private CourseDAO courseDAO;
    
    @Autowired
    private RoomDAO roomDAO;
    
    @Autowired
    private UserDAO userDAO;

    /**
     * Generate comprehensive dashboard summary with metrics and analytics
     */
    public Map<String, Object> generateSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Basic counts
        summary.put("totalCourses", courseDAO.findAll().size());
        summary.put("totalRooms", roomDAO.findAll().size());
        summary.put("totalInstructors", userDAO.getAllUsers().stream()
            .filter(user -> "FACULTY".equals(user.getRole()))
            .count());
        
        List<TimetableEntry> allEntries = timetableDAO.findAll();
        summary.put("totalScheduledSlots", allEntries.size());
        
        // Analytics data
        summary.put("dailyClassCounts", getDailyClassCounts(allEntries));
        summary.put("roomUtilization", getRoomUtilizationStats());
        summary.put("instructorWorkload", getInstructorWorkloadStats(allEntries));
        summary.put("timeSlotPopularity", getTimeSlotPopularity(allEntries));
        summary.put("weeklyScheduleOverview", getWeeklyOverview(allEntries));
        
        return summary;
    }

    /**
     * Get count of classes per day of week
     */
    public Map<String, Integer> getDailyClassCounts(List<TimetableEntry> entries) {
        Map<String, Integer> dailyCounts = new LinkedHashMap<>();
        
        // Initialize with all days
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String day : days) {
            dailyCounts.put(day, 0);
        }
        
        // Count entries by day
        for (TimetableEntry entry : entries) {
            String day = entry.getDayOfWeek();
            if (dailyCounts.containsKey(day)) {
                dailyCounts.put(day, dailyCounts.get(day) + 1);
            }
        }
        
        return dailyCounts;
    }

    /**
     * Calculate room utilization statistics
     */
    public List<Map<String, Object>> getRoomUtilizationStats() {
        List<Map<String, Object>> roomStats = new ArrayList<>();
        
        roomDAO.findAll().forEach(room -> {
            List<TimetableEntry> roomEntries = timetableDAO.getByRoom(room.getId());
            
            // Calculate utilization (assuming 5 days × 8 hours = 40 possible slots per week)
            int totalPossibleSlots = 40;
            int bookedSlots = roomEntries.size();
            double utilizationPercent = (bookedSlots * 100.0) / totalPossibleSlots;
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("roomId", room.getId());
            stat.put("roomName", room.getName());
            stat.put("capacity", room.getCapacity());
            stat.put("bookedSlots", bookedSlots);
            stat.put("utilizationPercent", Math.round(utilizationPercent * 100.0) / 100.0);
            stat.put("availableSlots", totalPossibleSlots - bookedSlots);
            
            roomStats.add(stat);
        });
        
        // Sort by utilization percentage descending
        roomStats.sort((a, b) -> Double.compare(
            (Double) b.get("utilizationPercent"), 
            (Double) a.get("utilizationPercent")
        ));
        
        return roomStats;
    }

    /**
     * Calculate instructor workload statistics
     */
    public List<Map<String, Object>> getInstructorWorkloadStats(List<TimetableEntry> entries) {
        Map<String, List<TimetableEntry>> instructorEntries = new HashMap<>();
        
        // Group entries by instructor
        for (TimetableEntry entry : entries) {
            if (entry.getInstructorId() != null) {
                // Find instructor username
                String instructorName = userDAO.getUserById(entry.getInstructorId())
                    .map(user -> user.getUsername())
                    .orElse("Unknown");
                    
                instructorEntries.computeIfAbsent(instructorName, k -> new ArrayList<>()).add(entry);
            }
        }
        
        List<Map<String, Object>> workloadStats = new ArrayList<>();
        
        instructorEntries.forEach((instructor, instructorClassList) -> {
            Map<String, Object> stat = new HashMap<>();
            stat.put("instructorName", instructor);
            stat.put("totalClasses", instructorClassList.size());
            
            // Calculate classes per day
            Map<String, Integer> classesByDay = getDailyClassCounts(instructorClassList);
            stat.put("classesByDay", classesByDay);
            
            // Calculate average classes per day
            double avgClassesPerDay = instructorClassList.size() / 5.0;
            stat.put("avgClassesPerDay", Math.round(avgClassesPerDay * 100.0) / 100.0);
            
            workloadStats.add(stat);
        });
        
        // Sort by total classes descending
        workloadStats.sort((a, b) -> Integer.compare(
            (Integer) b.get("totalClasses"), 
            (Integer) a.get("totalClasses")
        ));
        
        return workloadStats;
    }

    /**
     * Analyze time slot popularity
     */
    public Map<String, Integer> getTimeSlotPopularity(List<TimetableEntry> entries) {
        Map<String, Integer> timeSlotCounts = new TreeMap<>();
        
        for (TimetableEntry entry : entries) {
            String timeSlot = entry.getStartTime();
            timeSlotCounts.put(timeSlot, timeSlotCounts.getOrDefault(timeSlot, 0) + 1);
        }
        
        return timeSlotCounts;
    }

    /**
     * Generate weekly schedule overview for analytics
     */
    public Map<String, Object> getWeeklyOverview(List<TimetableEntry> entries) {
        Map<String, Object> overview = new HashMap<>();
        
        // Peak day analysis
        Map<String, Integer> dailyCounts = getDailyClassCounts(entries);
        String busiestDay = dailyCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
            
        String lightestDay = dailyCounts.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
        
        overview.put("busiestDay", busiestDay);
        overview.put("lightestDay", lightestDay);
        overview.put("peakDayClasses", dailyCounts.getOrDefault(busiestDay, 0));
        overview.put("lightDayClasses", dailyCounts.getOrDefault(lightestDay, 0));
        
        // Time slot analysis
        Map<String, Integer> timeSlotCounts = getTimeSlotPopularity(entries);
        String peakTimeSlot = timeSlotCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
            
        overview.put("peakTimeSlot", peakTimeSlot);
        overview.put("peakTimeSlotClasses", timeSlotCounts.getOrDefault(peakTimeSlot, 0));
        
        // Overall schedule density
        int totalSlots = entries.size();
        int maxPossibleSlots = roomDAO.findAll().size() * 5 * 8; // rooms × days × hours
        double scheduleDensity = maxPossibleSlots > 0 ? (totalSlots * 100.0) / maxPossibleSlots : 0;
        overview.put("overallScheduleDensity", Math.round(scheduleDensity * 100.0) / 100.0);
        
        return overview;
    }

    /**
     * Generate data for visual timetable grid
     */
    public Map<String, Object> getTimetableGridData() {
        Map<String, Object> gridData = new HashMap<>();
        
        List<TimetableEntry> allEntries = timetableDAO.findAll();
        
        // Group entries by day and time for easy frontend consumption
        Map<String, Map<String, List<Map<String, Object>>>> dayTimeGrid = new HashMap<>();
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String day : days) {
            dayTimeGrid.put(day, new HashMap<>());
        }
        
        for (TimetableEntry entry : allEntries) {
            String day = entry.getDayOfWeek();
            String startTime = entry.getStartTime();
            
            if (dayTimeGrid.containsKey(day)) {
                Map<String, Object> entryData = new HashMap<>();
                entryData.put("id", entry.getId());
                Course course = courseDAO.findById(entry.getCourseId());
                entryData.put("courseCode", course != null ? course.getCourseCode() : "Unknown");
                entryData.put("courseName", course != null ? course.getCourseName() : "Unknown Course");
                
                Room room = roomDAO.findById(entry.getRoomId());
                entryData.put("roomName", room != null ? room.getName() : "Unknown Room");
                entryData.put("startTime", entry.getStartTime());
                entryData.put("endTime", entry.getEndTime());
                entryData.put("instructorName", entry.getInstructorId() != null ?
                    userDAO.getUserById(entry.getInstructorId())
                        .map(user -> user.getUsername())
                        .orElse("TBD") : "TBD");
                
                dayTimeGrid.get(day)
                    .computeIfAbsent(startTime, k -> new ArrayList<>())
                    .add(entryData);
            }
        }
        
        gridData.put("schedule", dayTimeGrid);
        gridData.put("timeSlots", getAvailableTimeSlots());
        gridData.put("days", Arrays.asList(days));
        
        return gridData;
    }

    /**
     * Get available time slots for the grid
     */
    private List<String> getAvailableTimeSlots() {
        return Arrays.asList(
            "08:00", "09:00", "10:00", "11:00", "12:00", 
            "13:00", "14:00", "15:00", "16:00", "17:00"
        );
    }

    /**
     * Generate CSV export data
     */
    public List<Map<String, Object>> getCSVExportData() {
        List<Map<String, Object>> csvData = new ArrayList<>();
        
        List<TimetableEntry> allEntries = timetableDAO.findAll();
        
        for (TimetableEntry entry : allEntries) {
            Map<String, Object> row = new HashMap<>();
            row.put("Day", entry.getDayOfWeek());
            row.put("Start Time", entry.getStartTime());
            row.put("End Time", entry.getEndTime());
            
            Course course = courseDAO.findById(entry.getCourseId());
            row.put("Course Code", course != null ? course.getCourseCode() : "Unknown");
            row.put("Course Name", course != null ? course.getCourseName() : "Unknown");
            
            Room room = roomDAO.findById(entry.getRoomId());
            row.put("Room", room != null ? room.getName() : "Unknown");
            row.put("Instructor", entry.getInstructorId() != null ?
                userDAO.getUserById(entry.getInstructorId())
                    .map(user -> user.getUsername()).orElse("TBD") : "TBD");
            
            csvData.add(row);
        }
        
        return csvData;
    }
}