package telran.ashkelon2018;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Role;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.service.RolesService;

@SpringBootApplication
public class ForumServiceApplication implements CommandLineRunner{
	
	@Autowired
	RolesService rolesService;
	
	@Autowired
	UserAccountRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ForumServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Set<String> mainRoles = new HashSet<>();
		mainRoles.add("admin");
		mainRoles.add("moderator");
		rolesService.addRoles(new Role("removeUser", mainRoles));
		rolesService.addRoles(new Role("addRole", mainRoles));
		rolesService.addRoles(new Role("removeRole", mainRoles));
		if(!userRepository.existsById("admin")) {
			String hashPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
			UserAccount userAccount = UserAccount.builder()
					.login("admin")
					.password(hashPassword)
					.firstName("Super")
					.lastName("Admin")
					.expdate(LocalDateTime.now().plusYears(25))
					.role("admin")
					.build();
			userRepository.save(userAccount);
		}
		
		
		
	}
}
