package telran.ashkelon2018.forum.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.dao.RolesRepository;
import telran.ashkelon2018.forum.domain.Role;

@Service
public class RolesServiceImpl implements RolesService {

	@Autowired
	RolesRepository rolesRepository;

	@Override
	public Role addRoles(Role role) {

		return rolesRepository.save(role);
	}

	@Override
	public Role remove(String method) {
		Role role = rolesRepository.findById(method).orElse(null);
		rolesRepository.delete(role);
		return role;
	}

	@Override
	public Role getRole(String method) {
		return rolesRepository.findById(method).orElse(null);
	}

	@Override
	public Role addRoleByMethod(String method, Set<String> newRoles) {
		Role role = rolesRepository.findById(method).orElse(null);
		if (role != null) {

			for (String r : newRoles) {
				role.addRole(r);
			}
		}
		rolesRepository.save(role);
		return role;
	}

	@Override
	public Role removeRoleByMethod(String method, Set<String> newRoles) {
		Role role = rolesRepository.findById(method).orElse(null);
		if (role != null) {

			for (String r : newRoles) {
				role.removeRole(r);
			}
		}
		rolesRepository.save(role);
		return role;
	}

}
