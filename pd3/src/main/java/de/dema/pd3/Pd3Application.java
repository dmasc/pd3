package de.dema.pd3;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingDeque;

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
import de.dema.pd3.security.Pd3AuthenticationSuccessHandler;
import nz.net.ultraq.thymeleaf.LayoutDialect;

@SpringBootApplication
@EnableCaching
public class Pd3Application {

	@Value("${logpattern}")
	private String logpattern;
	
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

	@Bean(name = "logdeque")
	public LinkedBlockingDeque<String> createLogDeque() {
		return new LinkedBlockingDeque<>(10000);
	}

	@Bean(name = "logpageAppender", destroyMethod = "stop")
	public OutputStreamAppender<ILoggingEvent> logpageAppender(LoggerContext ctx, PatternLayoutEncoder encoder,
			LinkedBlockingDeque<String> logdeque) {
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
				try {
					logdeque.put(new String(b));
				} catch (InterruptedException e) {
					// ignore
				}
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
