package com.ufcg.psoft.scrum_board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "ScrumBoard API", version = "1.0", description = "REST API for ScrumBoard back-end"))
public class ScrumBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScrumBoardApplication.class, args);
	}

}
