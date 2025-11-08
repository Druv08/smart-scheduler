package com.druv.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class TimetableDataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TimetableDataLoader.class);

    @Autowired
    private TimetableDAO timetableDAO;

    @Override
    public void run(String... args) throws Exception {
        try {
            List<TimetableEntry> existingEntries = timetableDAO.findAll();
            
            if (!existingEntries.isEmpty()) {
                logger.info("Timetable already contains {} entries. Skipping seed.", existingEntries.size());
                return;
            }

            logger.info("Loading timetable seed data from JSON...");
            ClassPathResource resource = new ClassPathResource("static/data/timetable_seed.json");
            
            ObjectMapper mapper = new ObjectMapper();
            try (InputStream inputStream = resource.getInputStream()) {
                Map<String, Object> data = mapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> entries = (List<Map<String, Object>>) data.get("entries");

                int count = 0;
                for (Map<String, Object> entryData : entries) {
                    TimetableEntry entry = new TimetableEntry();
                    
                    Integer dayOrder = (Integer) entryData.get("dayOrder");
                    entry.setDayOfWeek(String.valueOf(dayOrder));
                    entry.setStartTime((String) entryData.get("startTime"));
                    entry.setEndTime((String) entryData.get("endTime"));
                    entry.setCourseName((String) entryData.get("courseName"));
                    entry.setFaculty((String) entryData.get("faculty"));
                    entry.setRoomName((String) entryData.get("roomName"));
                    entry.setSlotCode((String) entryData.get("slotCode"));
                    entry.setType((String) entryData.get("type"));

                    timetableDAO.saveOrUpdateEntry(entry);
                    count++;
                }

                logger.info("Successfully seeded {} timetable entries!", count);
            }

        } catch (Exception e) {
            logger.error("Error seeding timetable data: {}", e.getMessage(), e);
        }
    }
}