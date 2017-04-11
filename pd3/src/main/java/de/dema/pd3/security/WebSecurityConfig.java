package de.dema.pd3.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Sicherheitskonfiguration für die Web-Anwendung. Durch die Annotationen erkennt Spring diese Klasse automatisch.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final int THREE_MONTH_IN_SECONDS = 7776000;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Pd3AuthenticationSuccessHandler successHandler;
	
	// Der Wert kann in der Run Config als VM-Parameter gesetzt werden: -Ddebugpanel=true
	@Value("${debugpanel:false}")
	private boolean debug;
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/public/**", "/css/**", "/js/**", "/img/**", "/db/**", "/webjars/**").permitAll()
                .antMatchers("/test/**").access(debug ? "permitAll" : "denyAll")
                .anyRequest().authenticated()
                .and()
            .requiresChannel().anyRequest().requiresSecure()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(successHandler)
                .and()
            .logout()
            	.deleteCookies("remember-me")
                .permitAll()
                .and()
        	.rememberMe()
        		.userDetailsService(userDetailsService)
        		.tokenValiditySeconds(THREE_MONTH_IN_SECONDS);
//        		.authenticationSuccessHandler(successHandler); -- geht nicht, wenn SESSIONID gelöscht und Seite refresht wird - es erfolgt immer ein Redirect zu Home

        // Bei aktiviertem CSRF geht die H2 Console nicht und es wird daher deaktiviert
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);

		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(provider);
    }
    
}
