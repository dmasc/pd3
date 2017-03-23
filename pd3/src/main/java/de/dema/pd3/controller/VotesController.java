package de.dema.pd3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.dema.pd3.VoteOption;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.VoteService;

@Controller
public class VotesController {

	@Autowired
	private VoteService voteService;
	
    @GetMapping("/votes")
    public String votesOverview(Model model) {
    	
        return "votes";
    }
    
    @PostMapping("/comment/vote")
    public String saveCommentVote(@RequestParam("topicId") Long topicId, @RequestParam("commentId") Long commentId, @RequestParam("page") int page,  
    		@RequestParam(value = "like.x", required = false) String like, Authentication auth, RedirectAttributes attr) {
    	voteService.storeCommentVote(((CurrentUser) auth.getPrincipal()).getId(), commentId, like != null ? VoteOption.ACCEPTED : VoteOption.REJECTED);
    	
    	attr.addAttribute("id", topicId);
    	attr.addAttribute("page", page);
    	return "redirect:/topic/details";
    }

}
