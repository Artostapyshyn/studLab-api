package com.artostapyshyn.studlabapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title="studLab-api"))
public class StudLabApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudLabApiApplication.class, args);
	}

}
