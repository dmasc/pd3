package de.dema.pd3.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.dema.pd3.model.TopicModel;
import de.dema.pd3.model.UserModel;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.persistence.VoteRepository;

@Controller
public class VotesController {

	private static final Logger log = LoggerFactory.getLogger(VotesController.class);

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private VoteRepository voteRepo;
	
    @GetMapping("/votes")
    public String votesOverview(Model model) {
    	TopicModel vote = new TopicModel();
    	
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	log.info(String.valueOf(auth.getDetails()));
    	UserModel author = new UserModel();
    	author.setName("Franz Remmenscheid");
    	vote.setTitle("Das ist der Titel der Abstimmung");
    	vote.setDeadline(LocalDateTime.now().plusDays(47L));
    	vote.setParticipants(259);
    	vote.setDescription("<p>An dieser Stelle folgt die Beschreibung der Abstimmung mit näheren Details. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.</p><p>Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et</p>");
    	model.addAttribute("vote", vote);
    	
        return "votes";
    }

}
