package com.druv.scheduler.config;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

public class ThymeleafConfig {
    private static final TemplateEngine templateEngine;
    
    static {
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(getTemplateResolver());
    }
    
    private static ITemplateResolver getTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }
    
    public static TemplateEngine getTemplateEngine() {
        return templateEngine;
    }
}