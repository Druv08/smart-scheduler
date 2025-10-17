package com.druv.scheduler;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.sqlite.JDBC");
        dataSource.setUrl("jdbc:sqlite:smart_scheduler.db");
        return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    UserDAO userDAO(JdbcTemplate jdbcTemplate) {
        return new UserDAOImpl(jdbcTemplate);
    }

    @Bean
    CourseDAO courseDAO() {
        return new CourseDAO();
    }

    @Bean
    RoomDAO roomDAO() {
        return new RoomDAO();
    }

    @Bean
    TimetableDAO timetableDAO() {
        return new TimetableDAO();
    }

    @Bean
    Security security() {
        return new Security();
    }

    @Bean
    SchedulerService schedulerService(UserDAO userDAO, CourseDAO courseDAO,
        RoomDAO roomDAO, TimetableDAO timetableDAO) {
        return new SchedulerService(userDAO, courseDAO, roomDAO, timetableDAO);
    }

    @Bean
    UserManager userManager(UserDAO userDAO) {
        return new UserManager(userDAO);
    }

    @Bean
    CourseManager courseManager(CourseDAO courseDAO) {
        return new CourseManager(courseDAO);
    }

    @Bean
    RoomManager roomManager(RoomDAO roomDAO) {
        return new RoomManager(roomDAO);
    }

    @Bean
    TimetableManager timetableManager(TimetableDAO timetableDAO) {
        return new TimetableManager(timetableDAO);
    }
}