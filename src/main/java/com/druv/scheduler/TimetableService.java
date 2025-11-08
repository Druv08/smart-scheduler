package com.druv.scheduler;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TimetableService {
    
    private final TimetableDAO timetableDAO;
    
    public TimetableService(TimetableDAO timetableDAO) {
        this.timetableDAO = timetableDAO;
    }
    
    public TimetableEntry addEntry(String courseName, String faculty, String room, 
                                   String dayOfWeek, String startTime, String endTime, 
                                   String slotCode, String type) {
        
        // For now, use simplified IDs (can be enhanced later)
        TimetableEntry entry = new TimetableEntry();
        entry.setCourseId(courseName.hashCode() & Integer.MAX_VALUE); // Generate simple ID
        entry.setRoomId(room.hashCode() & Integer.MAX_VALUE); // Generate simple ID
        entry.setDayOfWeek(dayOfWeek);
        entry.setStartTime(startTime);
        entry.setEndTime(endTime);
        entry.setSlotCode(slotCode);
        entry.setType(type);
        entry.setFaculty(faculty);
        entry.setCourseName(courseName);
        entry.setRoomName(room);
        
        timetableDAO.saveOrUpdateEntry(entry);
        
        return entry;
    }
    
    public List<TimetableEntry> getAllEntries() {
        return timetableDAO.findAll();
    }
    
    public int getUniqueCourseCount() {
        List<TimetableEntry> entries = timetableDAO.findAll();
        return (int) entries.stream()
                .map(TimetableEntry::getCourseName)
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .count();
    }
}
