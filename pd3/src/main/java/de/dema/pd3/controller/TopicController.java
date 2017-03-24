package de.dema.pd3.controller;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.dema.pd3.VoteOption;
import de.dema.pd3.model.TopicModel;
import de.dema.pd3.model.VoteModel;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.security.CurrentUser;
import de.dema.pd3.services.CommentService;
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
	private CommentService commentService;
	
	@Autowired
    private UserRepository userRepo;
	
	@GetMapping("/topic/overview")
	public String topicOverview(Model model, @PageableDefault(sort = "deadline", size = 10) Pageable pageable, Authentication auth) {
        User user = userRepo.findOne(((CurrentUser) auth.getPrincipal()).getId());
		Page<TopicModel> page = topicService.getRunningTopics(pageable, user);
		model.addAttribute("page", page);

		return "topics";
	}

	@GetMapping("/topic/details")
	public String topicDetails(Model model, @RequestParam("id") Long id, Authentication auth,
			@PageableDefault(sort = "creationDate", size = 10, direction = Direction.DESC) Pageable pageable) {
		log.debug("showing details [topicID:{}]", id);
		Long userId = ((CurrentUser) auth.getPrincipal()).getId();
		TopicModel topicModel = topicService.loadTopic(id);
		model.addAttribute("topic", topicModel);
		VoteModel topicVote = voteService.findByUserIdAndTopicId(userId, id);
		if (topicVote != null) {
			model.addAttribute("topicVote", topicVote.getSelectedOption());
		}
		model.addAttribute("comments", commentService.loadByTopic(id, userId, pageable));
		
		return "topicdetails";
	}

    @PostMapping("/topic/vote")
    public String voteForTopic(Model model, @RequestParam("topicId") long id, Authentication auth,
                               @RequestParam(required = false, name = "voteYes") String voteYes,
                               @RequestParam(required = false, name = "voteNo") String voteNo) {
    	VoteOption option = VoteOption.ABSTENTION;
    	if (voteYes != null) {
    		option = VoteOption.ACCEPTED;
    	} else if (voteNo != null) {
    		option = VoteOption.REJECTED;
    	} 
        log.info("user voted for topic [user:{}] [topicId:{}] [option:{}]", auth.getName(), id, option);

        voteService.storeTopicVote(((CurrentUser) auth.getPrincipal()).getId(), id, option);

        return "redirect:/topic/overview";
    }

    @GetMapping("/topic/edit")
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

    @PostMapping("/topic/edit")
    public String editTopic(Model model, @ModelAttribute TopicModel topicModel, Authentication auth) {
        topicModel = topicService.save(topicModel, ((CurrentUser) auth.getPrincipal()).getId());
        model.addAttribute("topic", topicModel);
        return "topicedit";
    }

    @PostMapping("/topic/comment")
    public String comment(@ModelAttribute("text") String text, @ModelAttribute("topicId") Long topicId, Authentication auth, RedirectAttributes attr) {
    	if (!StringUtils.isBlank(text)) {
	    	commentService.save(((CurrentUser) auth.getPrincipal()).getId(), topicId, text);
    	}
        attr.addAttribute("id", topicId);
    	return "redirect:/topic/details";
    }
    
    @PostMapping("/comment/reply")
    public String saveCommentVote(@RequestParam("topicId") Long topicId, @RequestParam("commentId") Long commentId, @RequestParam("page") int page,  
    		@ModelAttribute("text") String text, Authentication auth, RedirectAttributes attr) {
    	if (!StringUtils.isBlank(text)) {
    		commentService.saveReply(((CurrentUser) auth.getPrincipal()).getId(), commentId, text);
    	}
    	
    	attr.addAttribute("id", topicId);
    	attr.addAttribute("page", page);
    	return "redirect:/topic/details";
    }

}
