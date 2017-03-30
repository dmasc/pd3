package de.dema.pd3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import de.dema.pd3.controller.CommonInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private CommonInterceptor commonInterceptor;
	
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/").setViewName("public/home");
        registry.addViewController("/public/home").setViewName("public/home");
    }

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		
		registry.addInterceptor(commonInterceptor);
	}

}
