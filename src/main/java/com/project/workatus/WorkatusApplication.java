package com.project.workatus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WorkatusApplication implements ApplicationRunner {

	@Autowired
	private RestTemplate restTemplate;

	public static void main(String[] args) {

		SpringApplication.run(WorkatusApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		final ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://self-signed.badssl.com/",
				String.class);
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			System.out.println(responseEntity.getBody());
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
}
