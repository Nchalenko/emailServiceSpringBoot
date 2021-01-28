package cloudbeds;

import cloudbeds.email.RegisterUseCase;
import cloudbeds.fileUpload.StorageProperties;
import cloudbeds.fileUpload.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(StorageProperties.class)
@EnableAutoConfiguration(exclude=ErrorMvcAutoConfiguration.class)
public class Application {

	@Autowired
	private final RegisterUseCase registerUseCase;

	public Application(RegisterUseCase registerUseCase) {
		this.registerUseCase = registerUseCase;
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	@EventListener(ApplicationReadyEvent.class)
	public void sendEmailsAfterStartup() {
		System.out.println("Start Event");

		System.out.println("---===### Event ###===---".toUpperCase());

		try {
			registerUseCase.register("bior93@gmail.com", "On Start App Email");
			Thread.sleep(20000);
			registerUseCase.register("nick.chalenko@gmail.com", "20 Sec On start Email");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Finish");
	}

}
