package de.dema.pd3.security;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
@Transactional
public class Pd3UserDetailsService implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(Pd3UserDetailsService.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User dbuser = userRepo.findByEmail(username);
		if (dbuser != null) {
			log.info("attemted user login [id:{}]", dbuser.getId());
			return new CurrentUser(dbuser.getId(), dbuser.getForename() + " " + dbuser.getSurname(), dbuser.getEmail(), 
					dbuser.getPassword(), true, !dbuser.getLocked(), AuthorityUtils.createAuthorityList("USER"));
		}
		throw new UsernameNotFoundException("unable to find user with email " + username);
	}

}
