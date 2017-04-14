package de.dema.pd3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.dema.pd3.Clock;
import de.dema.pd3.model.TopicModel;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.TopicVoteRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
public class TopicService {

	private static final Logger log = LoggerFactory.getLogger(TopicService.class);

	@Autowired
	private TopicRepository topicRepo;

	@Autowired
	private TopicVoteRepository voteRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional(readOnly = true)
	public Page<TopicModel> getRunningTopics(Pageable page, User user) {
		Page<Topic> topicsPage = topicRepo.findAllWhereDeadlineGreaterNowAndUserHasntVotedYet(user, page);
		return topicsPage.map(this::mapTopic);
	}

	public TopicModel loadTopic(Long id) {
		return mapTopic(topicRepo.findOne(id));
	}

    public TopicModel save(TopicModel topicModel, Long userId) {
    	log.debug("saving topic [topicModel:{}]", topicModel);
        Topic topic;
        if (topicModel.getId() != null) {
            topic = topicRepo.findOne(topicModel.getId());
        } else {
            topic = new Topic();
            topic.setAuthor(userRepo.findOne(userId));
            topic.setCreationDate(Clock.now());
        }

        topic.setDeadline(topicModel.getDeadline());
        topic.setDescription(topicModel.getDescription());
        topic.setTitle(topicModel.getTitle());

        topic = topicRepo.save(topic);
        topicModel.setId(topic.getId());
    	log.info("topic saved [topic:{}] [topicModel:{}]", topic, topicModel);
        return topicModel;
    }

    private TopicModel mapTopic(Topic topic) {
    	if (topic == null) {
    		return null;
    	}
    	
        TopicModel model = TopicModel.map(topic);
        model.setParticipants(voteRepo.countByTopic(topic));
        return model;
    }

}
