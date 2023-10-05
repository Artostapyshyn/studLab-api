package com.artostapyshyn.studlabapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.security.SecureRandom;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
		info = @Info(title = "StudLab API", version = "v1")
)
public class StudLabApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudLabApiApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public SecureRandom random() {
		return new SecureRandom();
	}
}
