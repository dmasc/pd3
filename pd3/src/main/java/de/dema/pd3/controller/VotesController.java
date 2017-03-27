package de.dema.pd3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    @PostMapping("/comment/vote/{like}")
    @ResponseBody
    public boolean saveCommentVote(@RequestParam("commentId") Long commentId, @PathVariable(value = "like") boolean like, Authentication auth) {
		Long id = voteService.storeCommentVote(((CurrentUser) auth.getPrincipal()).getId(), commentId, like ? VoteOption.ACCEPTED : VoteOption.REJECTED);
    	
    	return id != null;
    }

}
