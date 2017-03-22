package de.dema.pd3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.services.UserService;

import javax.validation.Valid;

@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
    @GetMapping("/public/register")
    public String registerForm(RegisterUserModel model) {
    	RegisterUserModel userModel = new RegisterUserModel();
       // model.addAttribute("reguser", userModel);
        return "public/register";
    }

    @PostMapping("/public/register")
    public String registerSubmit(@Valid @ModelAttribute RegisterUserModel user, BindingResult bindingResult) {
    	log.debug("register form submitted [data:{}]", user);
      //  model.addAttribute("reguser", user);
        if (bindingResult.hasErrors()) {
            log.error("register form kaputt submitted [data:{}]", user);
            return "/public/register";
        }

//        userService.registerUser(user);

        return "public/home";
    }
	
    @GetMapping("/user/profile")
    public String userProfile(Model model) {
    	return "profile";
    }
    
}
