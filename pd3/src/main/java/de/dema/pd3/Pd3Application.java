package de.dema.pd3;

import java.io.IOException;
import java.io.OutputStream;

import javax.sql.DataSource;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.ext.spring.ApplicationContextHolder;
import de.dema.pd3.controller.CommonInterceptor;
import de.dema.pd3.security.Pd3AuthenticationSuccessHandler;
import de.dema.pd3.security.WebSecurityConfig;
import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * Die Hauptklasse der PD3-Webanwendung mit der {@code main}-Methode zum Starten der Anwendung.
 * Zusätzlich werden hier noch alle Beans definiert, die in der Anwendung verwendet und nicht
 * bspw. durch Spring's Auto-COnfiguration automatisch erzeugt werden.
 */
@SpringBootApplication
@EnableCaching
public class Pd3Application {

	@Bean
	public SpringTemplateEngine templateEngine() {
	    SpringTemplateEngine templateEngine = new SpringTemplateEngine();

	    ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	    templateResolver.setPrefix("templates/");
	    templateResolver.setSuffix(".html");
	    templateResolver.setTemplateMode("HTML5");
	    templateEngine.addTemplateResolver(templateResolver);
	    
	    templateEngine.addDialect(new LayoutDialect());
	    templateEngine.addDialect(new SpringSecurityDialect());
	    templateEngine.addDialect(new Java8TimeDialect());
	    
	    return templateEngine;
	}
	
	/**
	 * Erstellt einen Service, der von Spring verwendet wird, um Remember-Me-Tokens zu verwalten.
	 */
	@Bean
	public RememberMeServices rememberMeServices(UserDetailsService userDetailsService, DataSource dataSource, JdbcTokenRepositoryImpl repo) {
		PersistentTokenBasedRememberMeServices services = new PersistentTokenBasedRememberMeServices(
				PersistentTokenBasedRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, userDetailsService, repo);
		services.setTokenValiditySeconds(WebSecurityConfig.THREE_MONTH_IN_SECONDS);
		services.setUseSecureCookie(true);
		
		return services;		
	}

	/**
	 * Erstellt ein Repository, dass per JDBC auf die PD3-Datenbank zugreift und Remember-Me-Tokens lädt und speichert.
	 * Wenn das Property {@code spring.jpa.hibernate.ddl-auto} auf {@code create-drop} gesetzt ist, legt das Repository
	 * die benötigte Tabelle beim Start der Anwendung automatisch an.
	 */
	@Bean
	public JdbcTokenRepositoryImpl rememberMeTokenRepository(DataSource dataSource, @Value("${spring.jpa.hibernate.ddl-auto}") String ddlAuto) {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource);
		repo.setCreateTableOnStartup("create-drop".equals(ddlAuto));
		return repo;
	}
	
	/**
	 * Erstellt den AuthenticationSuccessHandler, der von Spring Security automatisch nach jedem erfolgreichen
	 * Login aufgerufen wird.
	 * 
	 * @return eine neue Instanz eines {@linkplain Pd3AuthenticationSuccessHandler}.
	 */
	@Bean
	public Pd3AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new Pd3AuthenticationSuccessHandler(); 
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Bean
	public CommonInterceptor commonInterceptor() {
		return new CommonInterceptor();
	}
	
	@Bean
	public ApplicationContextHolder applicationContextHolder() {
		return new ApplicationContextHolder();
	}

	@Bean
	public LoggerContext loggerContext() {
		return (LoggerContext) LoggerFactory.getILoggerFactory();
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	public PatternLayoutEncoder encoder(LoggerContext ctx, @Value("${logpage.pattern}") String logpattern) {
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(ctx);
		encoder.setPattern(logpattern);
		return encoder;
	}

	/**
	 * Erstellt eine Queue, die als Warteschlange für Lognachrichten dient. Alle Lognachrichten werden in diese Queue 
	 * dupliziert und können dann ausgelesen und auf einer Log-Webseite dargestellt werden.<br>
	 * <br>
	 * Die Anzahl der Lognachrichten
	 * ist standardmäßig begrenzt auf 200, kann aber durch Änderung des Properties {@code logpage.max-messages} angepasst werden.
	 *
	 * @return {@linkplain CircularFifoQueue} mit Lognachrichten in Form von einzelnen Strings.
     */
	@Bean(name = "logmessages")
	public CircularFifoQueue<String> createLogQueue(@Value("${logpage.max-messages:200}") int maxLogMessages) {
		return new CircularFifoQueue<>(maxLogMessages);
	}

	/**
	 * Erstellt einen Logback {@linkplain OutputStreamAppender}, der sämtliche Lognachrichten in Form von einzelnen
	 * Strings in die {@linkplain CircularFifoQueue} {@code logqueue} einfügt. 
	 */
	@Bean(name = "logpageAppender", destroyMethod = "stop")
	public OutputStreamAppender<ILoggingEvent> logpageAppender(LoggerContext ctx, PatternLayoutEncoder encoder,
			CircularFifoQueue<String> logqueue) {
		OutputStreamAppender<ILoggingEvent> appender = new OutputStreamAppender<>();
		appender.setContext(ctx);
		appender.setEncoder(encoder);
		OutputStream dos = new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				throw new RuntimeException("not supported");
			}

			@Override
			public void write(byte[] b) throws IOException {
				logqueue.add(new String(b));
			}
		};
		appender.setOutputStream(dos);
		appender.start();
		return appender;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Pd3Application.class, args);
	}
	
}
