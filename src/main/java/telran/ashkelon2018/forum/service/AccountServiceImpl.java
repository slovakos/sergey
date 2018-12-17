package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Role;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exceptions.UserConflictException;
import telran.ashkelon2018.forum.exceptions.UserForbiddenException;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Autowired
	RolesService rolesService;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		if (userRepository.existsById(credentials.getLogin())) {
			throw new UserConflictException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
		UserAccount userAccount = UserAccount.builder().login(credentials.getLogin()).password(hashPassword)
				.firstName(userRegDto.getFirstName()).lastName(userRegDto.getLastName()).role("User")
				.expdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod())).build();
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);

	}

	private UserProfileDto convertToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder().firstName(userAccount.getFirstName()).lastName(userAccount.getLastName())
				.login(userAccount.getLogin()).roles(userAccount.getRoles()).build();
	}

	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		if (userAccount.getFirstName() != null) {
			userAccount.setFirstName(userRegDto.getFirstName());
		}
		if (userAccount.getLastName() != null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		//FIXME
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount == null) {
			throw new UserConflictException();
		}
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount superAccount = userRepository.findById(credentials.getLogin()).orElse(null);
		Role role = rolesService.getRole("removeUser");

		if (login.equals(superAccount.getLogin())) {
			userRepository.delete(userAccount);
			return convertToUserProfileDto(userAccount);
		}
		if (role != null && checkRoles(superAccount.getRoles(), role.getRoles())) {
			userRepository.delete(userAccount);
			return convertToUserProfileDto(userAccount);
		}
		throw new UserForbiddenException();
	}

	private boolean checkRoles(Set<String> userRoles, Set<String> methodRoles) {
		for (String role : methodRoles) {
			if (userRoles.contains(role)) {
				return true;
			}

		}
		return false;
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		// FIXME
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount == null) {
			throw new UserConflictException();
		}
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount superAccount = userRepository.findById(credentials.getLogin()).orElse(null);
		Role roles = rolesService.getRole("addRole");
		if (roles != null && checkRoles(superAccount.getRoles(), roles.getRoles())) {
			userAccount.addRole(role);
			userRepository.save(userAccount);
		}

		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		// FIXME
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount == null) {
			throw new UserConflictException();
		}
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount superAccount = userRepository.findById(credentials.getLogin()).orElse(null);
		Role roles = rolesService.getRole("removeRole");
		if (roles != null && checkRoles(superAccount.getRoles(), roles.getRoles())) {
			userAccount.removeRole(role);
			userRepository.save(userAccount);
		}

		return userAccount.getRoles();
	}

	@Override
	public void changePassword(String password, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		userAccount.setPassword(hashPassword);
		userAccount.setExpdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()));
		userRepository.save(userAccount);

	}

	@Override
	public UserProfileDto login(String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		return convertToUserProfileDto(userAccount);
	}

}
