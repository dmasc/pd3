package de.dema.pd3.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.model.VoteModel;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.UserService;
import de.dema.pd3.services.VoteService;

@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private VoteService voteService;
	
    @GetMapping("/public/register")
    public String registerForm(RegisterUserModel user) {
        return "public/register";
    }

    @PostMapping("/public/register")
    public String registerSubmit(@Validated(value = RegisterUserModel.RegisterUserValidation.class) @ModelAttribute RegisterUserModel user, BindingResult bindingResult) {
    	log.debug("register form submitted [data:{}]", user);
        if (bindingResult.hasErrors()) {
            log.error("register form invalid [data:{}]", user);
            return "/public/register";
        }
        return "public/home";
    }
	
    @GetMapping("/user/profile")
    public String userProfile(Model model, @RequestParam(value = "id", required = false) Long id, Authentication auth, 
    		@PageableDefault(sort = "voteTimestamp", size = 10, direction = Direction.DESC) Pageable pageable) {
    	if (id == null) {
    		id = ((CurrentUser) auth.getPrincipal()).getId();
    	}
    	
    	RegisterUserModel user = userService.findRegisterUserById(id);
    	Page<VoteModel> votePage = voteService.findByUserId(id, pageable);

    	model.addAttribute("user", user);
    	model.addAttribute("ownvotes", votePage);

        return "profile";
    }
    
    
    
}
