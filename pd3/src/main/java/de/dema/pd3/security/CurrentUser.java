package de.dema.pd3.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import de.dema.pd3.Pd3Util;

/**
 * Klasse zur Speicherung von zusätzlichen benutzerbezogenen Daten. Eine Instanz dieser Klasse wird von Spring automatisch
 * über den {@link Pd3UserDetailsService} in die {@link Authentication}-Objekte der einzelnen Requests gepackt. Die Instanz
 * kann über {@link Authentication#getPrincipal()} abgerufen werden - ein Cast ist notwendig.  
 * 
 * @author dmasc
 */
public class CurrentUser extends User {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String name;
	
	private boolean male;

	public CurrentUser(Long dbId, String name, String email, String password, boolean male, boolean enabled, 
			boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(email, password, enabled, true, true, accountNonLocked, authorities);
		this.id = dbId;
		this.name = name;
		this.male = male;
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

	/**
	 * Besagt, ob der Benutzer männlich ist.
	 * 
	 * @return Gibt <code>true</code> zurück, wenn der Benutzer männlich ist, sonst <code>false</code>.
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * Legt fest, ob der Benutzer männlich ist.
	 * 
	 * @param male wenn <code>true</code>, dann ist der Benutzer männlich, ansonsten weiblich.
	 */
	public void setMale(boolean male) {
		this.male = male;
	}

	public static CurrentUser map(de.dema.pd3.persistence.User user, String... roles) {
		return new CurrentUser(user.getId(), Pd3Util.username(user), user.getEmail(), 
				user.getPassword(), user.isMale(), true, !user.getLocked(), AuthorityUtils.createAuthorityList(roles));
	}

}
