package de.dema.pd3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.services.UserService;

@Controller
public class RegisterController {

	private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

	@Autowired
	private UserService userService;
	
    @GetMapping("/public/register")
    public String greetingForm(Model model) {
    	RegisterUserModel userModel = new RegisterUserModel();
        model.addAttribute("reguser", userModel);
        return "public/register";
    }

    @PostMapping("/public/register")
    public String greetingSubmit(@ModelAttribute RegisterUserModel user, Model model) {
        model.addAttribute("reguser", user);
        
        userService.registerUser(user);
        
        return "public/home";
    }
	
}
