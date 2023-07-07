package com.windry.chordplayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ChordPlayerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChordPlayerApplication.class, args);
	}

}
