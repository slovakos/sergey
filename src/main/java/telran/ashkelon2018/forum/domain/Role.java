package telran.ashkelon2018.forum.domain;

import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "method" })
@Document(collection = "forum_roles")
public class Role {
	@Id
	String method;
	@Singular
	Set<String> roles;

	public void addRole(String role) {
		roles.add(role);

	}

	public void removeRole(String role) {
		roles.remove(role);

	}

}
