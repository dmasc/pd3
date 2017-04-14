package de.dema.pd3;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * Die Hauptklasse der PD3-Webanwendung mit der {@code main}-Methode zum Starten der Anwendung.
 * Zusätzlich werden hier noch alle Beans definiert, die in der Anwendung verwendet und nicht
 * bspw. durch Spring's Auto-COnfiguration automatisch erzeugt werden.
 */
@SpringBootApplication
@EnableCaching
public class Pd3Application {

	@Value("${logpage.pattern}")
	private String logpattern;
	
	@Value("${logpage.max-messages:200}")
	private int maxLogMessages;
	
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
	    
	    return templateEngine;
	}
	
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
	public PatternLayoutEncoder encoder(LoggerContext ctx) {
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
	public CircularFifoQueue<String> createLogQueue() {
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
