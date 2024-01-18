package com.artostapyshyn.studlabapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
        info = @Info(title = "StudLab API", version = "v1")
)
public class StudLabApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudLabApiApplication.class, args);
    }
}
