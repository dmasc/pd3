package de.dema.pd3.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import de.dema.pd3.services.UserService;

public class Pd3AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(Pd3AuthenticationSuccessHandler.class);	

	@Autowired
	private UserService userService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		Long userId = ((CurrentUser) authentication.getPrincipal()).getId();
		userService.updateLastLoginDate(userId);
		log.debug("last login timestamp saved [userId:{}]", userId);
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
