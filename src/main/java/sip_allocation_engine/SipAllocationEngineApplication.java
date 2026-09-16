package sip_allocation_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SipAllocationEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SipAllocationEngineApplication.class, args);
	}

}
