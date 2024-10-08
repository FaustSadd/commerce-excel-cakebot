package Omg.CakeBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class CakeBotApplication {
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		SpringApplication.run(CakeBotApplication.class, args);
	}
}