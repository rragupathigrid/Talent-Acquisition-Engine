package com.forge.talentacquisitionengine;

import com.forge.talentacquisitionengine.offerService.offer.integration.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.forge.talentacquisitionengine.offerService.offer.integration.DocuSignProperties;


@EnableKafka
@SpringBootApplication
@EnableConfigurationProperties(
		{DocuSignProperties.class, OpenAiProperties.class}
)
public class TalentAcquisitionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalentAcquisitionEngineApplication.class, args);
	}

}
