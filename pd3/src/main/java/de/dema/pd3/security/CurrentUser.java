package de.dema.pd3.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CurrentUser extends User {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;

	public CurrentUser(Long dbId, String name, String email, String password, boolean enabled, 
			boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(email, password, enabled, true, true, accountNonLocked, authorities);
		this.id = dbId;
		this.name = name;
	}

	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return getUsername();
	}
	
}
