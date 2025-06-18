package com.makers.project3.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// This class customises how the app serves static resources (e.g. images) from the file system
// Resource handlers map URL paths to filesystem directories so static files (e.g. images) can be served via HTTP
// This is required for image storage outside the classpath (src) for deckImages and uploads

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){

        // Handlers for deck images
        registry.addResourceHandler("/deckImages/**")
                .addResourceLocations("file:./deckImages/");

        // Handler for profile pictures
        registry.addResourceHandler("/uploads/profilePics/**")
                .addResourceLocations("file:./uploads/profilePics/");
    }

}
