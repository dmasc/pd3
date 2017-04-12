package de.dema.pd3;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import de.dema.pd3.security.CurrentUser;

/**
 * Die Annotation kann auf Testklassen oder -methoden verwendet werden, um die Spring Security Authentifizierung
 * wegzumocken und eine Standard-{@linkplain CurrentUser}-Instanz im Authentication-Objekt des SecurityContexts zu speichern.
 * Einige Werte, die in der {@linkplain CurrentUser}-Instanz gesetzt werden, können über Parameter dieser Annotation
 * festgelegt werden.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithMockCurrentUser.WithMockCurrentUserSecurityContextFactory.class)
public @interface WithMockCurrentUser {
	
	String email() default "test@test.test";
	
	long id() default 1L;
	
	boolean enabled() default true;

	boolean locked() default false;
	
	/**
	 * SecurityContextFactory, die von {@linkplain WithMockCurrentUser} verwendet wird, um in Unit-Tests einen gemockten
	 * SecurityContext zu erzeugen, in dem ein gemockter Benutzer angemeldet ist.
	 */
	public static class WithMockCurrentUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCurrentUser> {
		
		@Override
		public SecurityContext createSecurityContext(WithMockCurrentUser annotation) {
			SecurityContext context = SecurityContextHolder.createEmptyContext();
			
			CurrentUser principal = new CurrentUser(annotation.id(), "Name von " + annotation.id(), annotation.email(), "password",
					true, annotation.enabled(), !annotation.locked(), AuthorityUtils.createAuthorityList("ROLE_USER"));
			context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities()));
			return context;
		}
		
	}
	
}
