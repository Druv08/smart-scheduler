package com.druv.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Check if we're in development mode
        boolean isDevMode = "dev".equals(activeProfile);
        
        if (isDevMode) {
            // Development mode - no caching for immediate updates
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/public/")
                    .setCacheControl(CacheControl.noStore().mustRevalidate())
                    .resourceChain(false);
                    
            registry.addResourceHandler("/css/**")
                    .addResourceLocations("classpath:/public/css/")
                    .setCacheControl(CacheControl.noStore());
                    
            registry.addResourceHandler("/js/**")
                    .addResourceLocations("classpath:/public/js/")
                    .setCacheControl(CacheControl.noStore());
                    
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("classpath:/public/images/")
                    .setCacheControl(CacheControl.noStore());
        } else {
            // Production mode - cache static assets for performance
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/public/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS));
                    
            registry.addResourceHandler("/css/**")
                    .addResourceLocations("classpath:/public/css/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                    
            registry.addResourceHandler("/js/**")
                    .addResourceLocations("classpath:/public/js/")
                    .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
                    
            registry.addResourceHandler("/images/**")
                    .addResourceLocations("classpath:/public/images/")
                    .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
        }
    }
    
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
