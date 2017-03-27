package de.dema.pd3.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.model.TopicVoteModel;
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
    	Page<TopicVoteModel> votePage = voteService.findByUserId(id, pageable);

    	model.addAttribute("user", user);
    	model.addAttribute("ownvotes", votePage);

        return "profile";
    }
    
    @GetMapping("/user/inbox")
    public String userInbox(Model model, @RequestParam(value = "selRoom", required = false) Long roomId, Authentication auth, 
    		@PageableDefault(size = 10, direction = Direction.DESC) Pageable pageable) {
		Long userId = ((CurrentUser) auth.getPrincipal()).getId();
    	
    	List<ChatroomModel> rooms = userService.loadAllChatroomsOrderedByTimestampOfLastMessageDesc(userId);
    	model.addAttribute("rooms", rooms);

    	if (roomId != null) {
    		model.addAttribute("messages", userService.loadMessagesforChatroom(userId, roomId));
    	}
    	
    	return "inbox";
    }    		

    @PostMapping("/user/send-message/{target}")
    public String sendMessage(@PathVariable("target") String target, @ModelAttribute("recipientId") Long recipientId, 
    		@ModelAttribute("text") String text, Authentication auth, RedirectAttributes attr) {
    	String redirect = "/";
    	if ("user".equals(target)) {
    		userService.sendMessageToUser(text, ((CurrentUser) auth.getPrincipal()).getId(), recipientId);
        	attr.addAttribute("id", recipientId);
        	redirect = "/user/profile";
    	} else if ("room".equals(target)) {
    		//TODO Service-Methode für "in den Raum reinsenden" aufrufen
        	attr.addAttribute("selRoom", recipientId);
        	redirect = "/user/inbox";
    	}
    	
    	return "redirect:" + redirect;
    }    
    
    @PostMapping("/user/delete-chatroom")
    public String deleteChatroom(@ModelAttribute("roomId") Long roomId, Authentication auth, RedirectAttributes attr) {
    	Long id = ((CurrentUser) auth.getPrincipal()).getId();
    	log.debug("user wants to delete a chatroom [userId:{}] [roomId:{}]", id, roomId);
    	userService.deleteChatroom(id, roomId);
    	
    	return "redirect:/user/inbox";
    }
    
    @PostMapping("/user/chatroom-notifications")
    @ResponseBody
    public void chatroomNotifications(@ModelAttribute("roomId") Long roomId, Model model, @ModelAttribute("notificationsActive") String activeString, 
    		Authentication auth, RedirectAttributes attr) {
    	Long id = ((CurrentUser) auth.getPrincipal()).getId();
    	log.debug("chatroom notifications option changed [userId:{}] [roomId:{}] [activeString:{}]", id, roomId, activeString);
    	
    	userService.storeChatroomNewMessageNotificationActivationStatus(id, roomId, "on".equals(activeString));
    }
    
}
