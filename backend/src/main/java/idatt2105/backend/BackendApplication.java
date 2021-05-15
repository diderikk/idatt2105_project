package idatt2105.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import idatt2105.backend.Model.DTO.POSTSectionDTO;
import idatt2105.backend.Service.RoomService;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Autowired
	RoomService roomService;

	@Bean
	public CommandLineRunner run() {
		return args -> {
			roomService.addSectionToRoom(new POSTSectionDTO("Bord 1", "A4-112"));
		};
	}
}