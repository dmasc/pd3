package de.dema.pd3.controller;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.model.VoteModel;
import de.dema.pd3.persistence.User;
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
    public String registerForm(Model model) {
    	RegisterUserModel userModel = new RegisterUserModel();
        model.addAttribute("reguser", userModel);
        return "public/register";
    }

    @PostMapping("/public/register")
    public String registerSubmit(@ModelAttribute RegisterUserModel user, Model model) {
    	log.debug("register form submitted [data:{}]", user);
        model.addAttribute("reguser", user);
        
        userService.registerUser(user);
        
        return "public/home";
    }
	
    @GetMapping("/user/profile")
    public String userProfile(Model model, @RequestParam(value = "id", required = false) Long id, Authentication auth, 
    		@PageableDefault(sort = "voteTimestamp", size = 10, direction = Direction.DESC) Pageable pageable) {
    	if (id == null) {
    		id = ((CurrentUser) auth.getPrincipal()).getId();
    	}
    	User user = userService.findById(id);
    	
    	Page<VoteModel> votePage = voteService.findByUser(user, pageable);
    	model.addAttribute("ownvotes", votePage);

    	return "profile";
    }
    
}
