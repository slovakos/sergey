package telran.ashkelon2018.forum.service;

import java.util.Set;

import telran.ashkelon2018.forum.domain.Role;

public interface RolesService {

	Role addRoles(Role role);

	Role remove(String method);
	
	Role getRole(String method);

	Role addRoleByMethod(String method, Set<String> roles);

	Role removeRoleByMethod(String method, Set<String> roles);

	

}
