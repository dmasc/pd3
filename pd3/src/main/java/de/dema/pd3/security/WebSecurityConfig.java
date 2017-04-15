package de.dema.pd3.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import de.dema.pd3.Pd3Application;

/**
 * Sicherheitskonfiguration für die Web-Anwendung. Durch die Annotationen erkennt Spring diese Klasse automatisch.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	public static final int THREE_MONTH_IN_SECONDS = 7776000;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private Pd3AuthenticationSuccessHandler successHandler;
	
	@Autowired
	private RememberMeServices rememberMeServices;
	
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
        		.key(PersistentTokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY)
        		.rememberMeServices(rememberMeServices)
        		.authenticationSuccessHandler(successHandler);

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
    
	/**
	 * Erstellt einen Service, der von Spring verwendet wird, um Remember-Me-Tokens zu verwalten.<br>
	 * <br>
	 * Die Bean wird in dieser Klasse erzeugt und nicht in {@linkplain Pd3Application}, da sie dort in jedem Fall erzeugt werden würde,
	 * auch wenn sie gar nicht benötigt wird - bspw. in Controller-Tests, die mit {@code @WebMvcTest} arbeiten. Beim Start solcher
	 * Tests würde es zu einem Fehler kommen, da die abhängigen Objekte wie {@code userDetailsService} von Spring nicht angelegt
	 * worden wären.
	 */
	@Bean
	public RememberMeServices rememberMeServices(UserDetailsService userDetailsService, JdbcTokenRepositoryImpl repo) {
		PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
				PersistentTokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, userDetailsService, repo);
		services.setTokenValiditySeconds(WebSecurityConfig.THREE_MONTH_IN_SECONDS);
		services.setUseSecureCookie(true);
		
		return services;		
	}

	/**
	 * Erstellt ein Repository, dass per JDBC auf die PD3-Datenbank zugreift und Remember-Me-Tokens lädt und speichert.
	 * Wenn das Property {@code spring.jpa.hibernate.ddl-auto} auf {@code create-drop} gesetzt ist, legt das Repository
	 * die benötigte Tabelle beim Start der Anwendung automatisch an.<br>
	 * <br>
	 * Die Bean wird in dieser Klasse erzeugt und nicht in {@linkplain Pd3Application}, da sie dort in jedem Fall erzeugt werden würde,
	 * auch wenn sie gar nicht benötigt wird - bspw. in Controller-Tests, die mit {@code @WebMvcTest} arbeiten. Beim Start solcher
	 * Tests würde es zu einem Fehler kommen, da die abhängigen Objekte wie {@code userDetailsService} von Spring nicht angelegt
	 * worden wären.
	 */
	@Bean
	public JdbcTokenRepositoryImpl rememberMeTokenRepository(DataSource dataSource, @Value("${spring.jpa.hibernate.ddl-auto}") String ddlAuto) {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		repo.setCreateTableOnStartup("create-drop".equals(ddlAuto));
		return repo;
	}

}
