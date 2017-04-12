package de.dema.pd3.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import de.dema.pd3.Clock;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.UserService;

/**
 * Ein HandlerInterceptor, der Daten, die von vielen oder allen Seiten gebraucht werden (bspw. "Neue Nachricht verfügbar"), im Model verfügbar macht.
 */
public class CommonInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private UserService userService;

	@Value("${debugpanel:false}")
	private boolean debug;
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (modelAndView != null) {
			// modelAndView.addObject("clock", Clock.class); --> führt dazu, dass bei Redirects ein GET-Parameter namens 'clock' hinzugefügt wird.
			// Sollte durch spring.mvc.ignore-default-model-on-redirect=true eigentlich abgeschaltet sein, ist es aber nicht.
			request.setAttribute("clock", Clock.class);
			request.setAttribute("showDebugPanel", debug);
	
			if (auth != null && auth.getPrincipal() instanceof CurrentUser) {
				boolean newMessagesAvailable = userService.areNewMessagesAvailable(((CurrentUser) auth.getPrincipal()).getId());		
				request.setAttribute("newMessagesAvailable", newMessagesAvailable);
			}
		}
	}

}
