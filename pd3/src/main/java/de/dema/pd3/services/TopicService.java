package de.dema.pd3.services;

import de.dema.pd3.model.TopicModel;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.persistence.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TopicService {

	private static final Logger log = LoggerFactory.getLogger(TopicService.class);

	@Autowired
	private TopicRepository topicRepo;

	@Autowired
	private VoteRepository voteRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional(readOnly = true)
	public Page<TopicModel> getRunningTopics(Pageable page, User user) {
		Page<Topic> topicsPage = topicRepo.findAllWhereDeadlineGreaterNowAndUserHasntVotedYet(user, page);
		return topicsPage.map(this::mapTopic);
	}

	public TopicModel loadTopic(Long id) {
		Topic topic = topicRepo.findOne(id);
		return mapTopic(topic);
	}

    public TopicModel save(TopicModel topicModel, String userEmail) {
        Topic topic;
        if (topicModel.getId() != null) {
            topic = topicRepo.findOne(topicModel.getId());
        } else {
            topic = new Topic();
            topic.setAuthor(userRepo.findByEmail(userEmail));
            topic.setCreationDate(LocalDateTime.now());
        }

        topic.setDeadline(topicModel.getDeadline());
        topic.setDescription(topicModel.getDescription());
        topic.setTitle(topicModel.getTitle());

        topic = topicRepo.save(topic);
        topicModel.setId(topic.getId());
        return topicModel;
    }

    private TopicModel mapTopic(Topic topic) {
        TopicModel model = new TopicModel();
        model.setAuthor(topic.getAuthor().getForename() + " " + topic.getAuthor().getSurname());
        model.setDeadline(topic.getDeadline());
        model.setDescription(topic.getDescription());
        model.setId(topic.getId());
        model.setParticipants(voteRepo.countByVotePkTopic(topic));
        model.setTitle(topic.getTitle());

        return model;
    }

}
