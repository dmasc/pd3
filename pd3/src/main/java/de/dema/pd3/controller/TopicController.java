package de.dema.pd3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.dema.pd3.model.TopicModel;
import de.dema.pd3.persistence.VoteRepository;
import de.dema.pd3.services.TopicService;

@Controller
public class TopicController {

	private static final Logger log = LoggerFactory.getLogger(TopicController.class);
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private VoteRepository voteRepo;
	
	@GetMapping("/topic-overview")
	public String topicOverviewPage(Model model, @PageableDefault(sort = "deadline", size = 10) Pageable pageable) {
		Page<TopicModel> page = topicService.getRunningTopics(pageable);
		model.addAttribute("page", page);
		
		return "topics";
	}

}
