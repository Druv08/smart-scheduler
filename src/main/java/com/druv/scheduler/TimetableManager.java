package com.druv.scheduler;

import java.util.List;

public class TimetableManager {
    private final TimetableDAO timetableDAO = new TimetableDAO();

    public boolean addEntry(int courseId, int roomId, String dayOfWeek, String startTime, String endTime) {
        return timetableDAO.addEntry(courseId, roomId, dayOfWeek, startTime, endTime);
    }

    public List<TimetableEntry> listEntries() {
        return timetableDAO.getAllEntries();
    }

    public boolean deleteEntry(int id) {
        return timetableDAO.deleteEntry(id);
    }
}
