package de.dema.pd3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;

@Controller
public class TopicController {

	private static final Logger log = LoggerFactory.getLogger(TopicController.class);
	
	@Autowired
	private TopicRepository topicRepository;
	
	@GetMapping("/topic-overview")
	public String topicOverview(Model model) {
		List<Topic> topics = topicRepository.findAllByOrderByCreationDateDesc(new PageRequest(0, 10, new Sort(Sort.Direction.DESC, "creationDate")));
		model.addAttribute("topics", topics);
		
		return "topics";
	}
	
}
