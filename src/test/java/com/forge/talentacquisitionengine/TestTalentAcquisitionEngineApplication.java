package com.forge.talentacquisitionengine;

import com.forge.talentacquisitionengine.TalentAcquisitionEngineApplication;
import org.springframework.boot.SpringApplication;

public class TestTalentAcquisitionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.from(TalentAcquisitionEngineApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
