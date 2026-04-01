package com.smart_skills.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

    @Configuration
    public class UploadConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            Path uploadPath = Paths.get("uploads");
            String absolutePath = uploadPath.toFile().getAbsolutePath();

            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:" + absolutePath + "/");
        }
    }

