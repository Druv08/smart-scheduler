package com.druv.scheduler;

import java.util.List;

public class TimetableManager {
    private final TimetableDAO timetableDAO;

    public TimetableManager(TimetableDAO timetableDAO) {
        this.timetableDAO = timetableDAO;
    }

    public List<TimetableEntry> getAllBookings() {
        return timetableDAO.findAll();
    }

    public boolean addBooking(int courseId, int roomId, String day, String startTime, String endTime) {
        return timetableDAO.addBooking(courseId, roomId, day, startTime, endTime);
    }

    public boolean hasConflict(int roomId, String day, String startTime, String endTime) {
        return timetableDAO.hasTimeConflict(roomId, day, startTime, endTime);
    }
}
