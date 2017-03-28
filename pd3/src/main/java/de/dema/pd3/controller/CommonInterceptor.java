package de.dema.pd3.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.UserService;

public class CommonInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserService userService;

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (modelAndView != null && auth != null && auth.getPrincipal() instanceof CurrentUser) {
			boolean newMessagesAvailable = userService.areNewMessagesAvailable(((CurrentUser) auth.getPrincipal()).getId());		
			modelAndView.addObject("newMessagesAvailable", newMessagesAvailable);
		}
	}

}
