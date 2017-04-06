package de.dema.pd3.controller;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.dema.pd3.Pd3Util;
import de.dema.pd3.model.ChatroomMessageModel;
import de.dema.pd3.model.ChatroomModel;
import de.dema.pd3.model.NamedIdModel;
import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.model.TopicVoteModel;
import de.dema.pd3.services.UserService;
import de.dema.pd3.services.VoteService;

@Controller
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

	@Autowired
	private UserService userService;
	
	@Autowired
	private VoteService voteService;
	
    @GetMapping("/public/register")
    public String registerForm(RegisterUserModel user) {
        return "public/register";
    }

    @PostMapping("/public/register")
    public String registerSubmit(@Valid @ModelAttribute RegisterUserModel user, BindingResult bindingResult) {
    	log.debug("register form submitted [data:{}]", user);
        if (bindingResult.hasErrors()) {
            log.debug("register form invalid [data:{}]", user);
            return "/public/register";
        }
        return "public/home";
    }
	
    @GetMapping("/user/profile")
    public String userProfile(Model model, @RequestParam(value = "id", required = false) Long id, Authentication auth, 
    		@PageableDefault(sort = "voteTimestamp", size = 10, direction = Direction.DESC) Pageable pageable) {
    	if (id == null) {
    		id = Pd3Util.currentUserId(auth);
    	}
    	
    	RegisterUserModel user = userService.findRegisterUserById(id);
    	if (user == null) {
    		log.warn("requested user not found [userId:{}]", id);
    		return "redirect:/";
    	}

    	model.addAttribute("user", user);
    	Page<TopicVoteModel> votePage = voteService.findByUserId(id, pageable);
    	if (votePage != null) {
    		model.addAttribute("ownvotes", votePage);
    	}

        return "profile";
    }
    
    @GetMapping("/user/inbox")
    public String userInbox(Model model, @RequestParam(value = "selRoom", required = false) Long roomId, Authentication auth) {
		Long userId = Pd3Util.currentUserId(auth);
    	
    	boolean userRoomAssociationExists = false;
    	
    	if (roomId != null) {
    		userRoomAssociationExists = userService.storeLastMessageRead(userId, roomId);
    	}

    	List<ChatroomModel> rooms = userService.loadAllChatroomsOrderedByTimestampOfLastMessageDesc(userId);
    	model.addAttribute("rooms", rooms);

		if (userRoomAssociationExists) {
	    	Optional<ChatroomModel> activeRoom = rooms.stream().filter(room -> room.getId().equals(roomId)).findFirst();
	    	if (activeRoom.isPresent()) {
	    		userInboxAjax(model, roomId, null, auth);
	        	
		    	model.addAttribute("selectedRoom", activeRoom.get());
	    	}
		}
    	
    	return "inbox";
    }

    @PostMapping("/user/inbox-ajax")
	public String userInboxAjax(Model model, @RequestParam("roomId") Long roomId, @RequestParam("lastMsgId") Long lastMsgId, Authentication auth) {
		Long userId = Pd3Util.currentUserId(auth);

		if (roomId != null) {
    		Page<ChatroomMessageModel> messages = userService.loadMessagesForChatroom(userId, roomId, lastMsgId);
    		if (messages != null) {
    			model.addAttribute("messages", messages);
    			model.addAttribute("lastMsgId", messages.getContent().get(messages.getNumberOfElements() - 1).getId());
    		}
    	}
		model.addAttribute("roomId", roomId);
		return "inbox :: messages-panel";
	}    		

    @PostMapping("/user/send-message/{target}")
    public String sendMessage(@PathVariable("target") String target, @ModelAttribute("targetId") Long targetId, 
    		@ModelAttribute("text") String text, Authentication auth, RedirectAttributes attr) {
    	Long userId = Pd3Util.currentUserId(auth);
    	log.debug("user sends message [userId:{}] [target:{}] [targetId:{}]", userId, target, targetId);
    	String redirect = "/";
    	if ("user".equals(target)) {
    		userService.sendMessage(text, userId, targetId);
        	attr.addAttribute("id", targetId);
        	redirect = "/user/profile";
    	} else if ("room".equals(target)) {
    		userService.sendMessageToChatroom(text, userId, targetId);
        	attr.addAttribute("selRoom", targetId);
        	redirect = "/user/inbox";
    	}
    	
    	return "redirect:" + redirect;
    }    
    
    @PostMapping("/user/delete-chatroom")
    public String deleteChatroom(@ModelAttribute("roomId") Long roomId, Authentication auth, RedirectAttributes attr) {
    	Long id = Pd3Util.currentUserId(auth);
    	log.debug("user wants to delete a chatroom [userId:{}] [roomId:{}]", id, roomId);
    	userService.deleteChatroom(id, roomId);
    	
    	return "redirect:/user/inbox";
    }
    
    @PostMapping("/user/chatroom-notifications")
    @ResponseBody
    public void chatroomNotifications(@ModelAttribute("roomId") Long roomId, Model model, @ModelAttribute("notificationsActive") String activeString, 
    		Authentication auth, RedirectAttributes attr) {
    	Long id = Pd3Util.currentUserId(auth);
    	log.debug("chatroom notifications option changed [userId:{}] [roomId:{}] [activeString:{}]", id, roomId, activeString);
    	
    	userService.storeChatroomNewMessageNotificationActivationStatus(id, roomId, "on".equals(activeString));
    }

    @GetMapping("/user/find")
    @ResponseBody
    public String findUsers(@RequestParam("query") String query, Authentication auth) {
    	Long id = Pd3Util.currentUserId(auth);
    	log.debug("find user invoked [userId:{}] [query:{}]", id, query);
    	
    	List<NamedIdModel> result = userService.findByQuery(query);
    	String json = "{\"suggestions\": [";
    	for (NamedIdModel model : result) {
    		json += "{\"value\": \"" + model.getName() + "\", \"data\": \"" + model.getId() + "\"},";
		}
    	json = json.substring(0, json.length() - 1) + "]}";
    	
    	return json;
    }
    
    @PostMapping("/user/newmsgs")
    @ResponseBody
    public boolean newMessagesAvailable(Authentication auth) {
    	Long id = Pd3Util.currentUserId(auth);
    	return userService.areNewMessagesAvailable(id);
    }

    @PostMapping("/user/rename-chatroom")
    @ResponseBody
    public boolean changeChatroomName(@RequestParam("roomId") Long roomId, @RequestParam("name") String name, Authentication auth) {
    	Long id = Pd3Util.currentUserId(auth);
    	log.debug("renaming chatroom [userId:{}] [name:{}]", id, name);
    	
    	return userService.renameChatroom(id, roomId, name);
    }

    @GetMapping("/public/forgot-password")
    public String forgotPassword() {
    	return "public/forgot-password";
    }
    
    @PostMapping("/public/forgot-password")
    public String forgotPassword(Model model, @RequestParam("email") String email, HttpServletRequest request) {
    	email = email.trim();
    	if (EMAIL_PATTERN.matcher(email).matches()) {
    		String requestUrl = request.getRequestURL().toString();
    		requestUrl = requestUrl.substring(0, requestUrl.indexOf("/public/"));
			userService.sendPasswordResetEmail(requestUrl, email);
			model.addAttribute("success", true);
    	} else {
    		model.addAttribute("invalid", true);
    	}
    	model.addAttribute("email", email);
    	return "public/forgot-password";
    }
    
    @GetMapping("/public/change-password")
    public String changePassword(@RequestParam("id") Long id, @RequestParam("token") String token, RedirectAttributes attr) {
        boolean result = userService.validatePasswordResetToken(id, token);
        if (!result) {
        	attr.addAttribute("error", true);
            return "redirect:/public/forgot-password";
        }
        return "public/change-password";
    }

    @PostMapping("/public/change-password")
    public String updatePassword(Model model, @RequestParam("id") Long userId, @RequestParam("password") String password, 
    		@RequestParam("passwordRepeat") String passwordRepeat, Authentication auth) {
    	if (password.equals(passwordRepeat)) {
	    	userService.changePassword(userId, password);
	    	return "redirect:/user/profile";
    	}
    	model.addAttribute("unequal", true);
    	return "public/change-password";
    }
    
}
