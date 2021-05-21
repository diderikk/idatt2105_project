package idatt2105.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import idatt2105.backend.Model.DTO.Section.POSTSectionDTO;
import idatt2105.backend.Model.DTO.User.POSTUserDTO;
import idatt2105.backend.Service.RoomService;
import idatt2105.backend.Service.UserService;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	// @Autowired
	// UserService userService;

	// @Bean
	// public CommandLineRunner run() {
	// 	return args -> {
	// 		POSTUserDTO dto = new POSTUserDTO("did", "Kra", "diderikk@stud.ntnu.no", "phoneNumber", null, false);
	// 		userService.createUser(dto);
	// 	};
	// }
}