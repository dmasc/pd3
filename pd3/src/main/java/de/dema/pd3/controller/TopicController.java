package de.dema.pd3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.dema.pd3.VoteOption;
import de.dema.pd3.model.TopicModel;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.TopicService;
import de.dema.pd3.services.VoteService;

@Controller
public class TopicController {

	private static final Logger log = LoggerFactory.getLogger(TopicController.class);
	
	@Autowired
	private TopicService topicService;
	
	@Autowired
	private VoteService voteService;

	@Autowired
    private UserRepository userRepo;
	
	@GetMapping("/topic-overview")
	public String topicOverview(Model model, @PageableDefault(sort = "deadline", size = 10) Pageable pageable, Authentication auth) {
        User user = userRepo.findOne(((CurrentUser) auth.getPrincipal()).getId());
		Page<TopicModel> page = topicService.getRunningTopics(pageable, user);
		model.addAttribute("page", page);

		return "topics";
	}

	@GetMapping("/topicdetails")
	public String topicDetails(Model model, @RequestParam("id") Long id) {
		log.info("showing details [topicID:{}]", id);
		TopicModel topicModel = topicService.loadTopic(id);
		model.addAttribute("topic", topicModel);
		return "topicdetails";
	}

    @PostMapping("/topic/vote")
    public String voteForTopic(Model model, @RequestParam("topicId") long id, Authentication auth,
                               @RequestParam(required = false, name = "voteYes") String voteYes,
                               @RequestParam(required = false, name = "voteNo") String voteNo) {
    	VoteOption option = VoteOption.ABSTENTION;
    	if (voteYes != null) {
    		option = VoteOption.YES;
    	} else if (voteNo != null) {
    		option = VoteOption.NO;
    	} 
        log.info("user voted for topic [user:{}] [topicId:{}] [option:{}]", auth.getName(), id, option);

        voteService.storeVote(auth.getName(), id, option);

        return "redirect:/topic-overview";
    }

    @GetMapping("/edittopic")
    public String editTopic(Model model, @RequestParam(name = "id", required = false) Long id) {
        TopicModel topic;
        if (id != null) {
            topic = topicService.loadTopic(id);
        } else {
            topic = new TopicModel();
        }
        model.addAttribute("topic", topic);

        return "topicedit";
    }

    @PostMapping("/edittopic")
    public String editTopic(Model model, @ModelAttribute TopicModel topicModel, Authentication auth) {
        topicModel = topicService.save(topicModel, auth.getName());
        model.addAttribute("topic", topicModel);
        return "topicedit";
    }

}
