package com.druv.scheduler;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:smart_scheduler.db");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public UserDAO userDAO(JdbcTemplate jdbcTemplate) {
        return new UserDAOImpl(jdbcTemplate);
    }

    @Bean
    public CourseDAO courseDAO() {
        return new CourseDAO();
    }

    @Bean
    public RoomDAO roomDAO() {
        return new RoomDAO();
    }

    @Bean
    public TimetableDAO timetableDAO() {
        return new TimetableDAO();
    }

    @Bean
    public Security security() {
        return new Security();
    }

    @Bean
    public SchedulerService schedulerService(UserDAO userDAO, CourseDAO courseDAO,
                                           RoomDAO roomDAO, TimetableDAO timetableDAO) {
        return new SchedulerService(userDAO, courseDAO, roomDAO, timetableDAO);
    }

    @Bean
    public UserManager userManager(UserDAO userDAO) {
        return new UserManager(userDAO);
    }

    @Bean
    public CourseManager courseManager(CourseDAO courseDAO) {
        return new CourseManager(courseDAO);
    }

    @Bean
    public RoomManager roomManager(RoomDAO roomDAO) {
        return new RoomManager(roomDAO);
    }

    @Bean
    public TimetableManager timetableManager(TimetableDAO timetableDAO) {
        return new TimetableManager(timetableDAO);
    }
}
