package com.forge.talentacquisitionengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class TalentAcquisitionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentAcquisitionEngineApplication.class, args);
	}

}
