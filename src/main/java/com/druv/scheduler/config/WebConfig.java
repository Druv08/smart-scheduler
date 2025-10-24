package com.druv.scheduler.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static content from classpath:/public/
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/public/")
                .setCachePeriod(3600);
        
        // Specific handlers for common static resources
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/public/css/")
                .setCachePeriod(3600);
                
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/public/js/")
                .setCachePeriod(3600);
                
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/public/images/")
                .setCachePeriod(3600);
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}