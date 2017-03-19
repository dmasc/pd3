package de.dema.pd3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.dema.pd3.model.TopicModel;
import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.VoteRepository;

@Service
public class TopicService {

	private static final Logger log = LoggerFactory.getLogger(TopicService.class);

	@Autowired
	private TopicRepository topicRepo;

	@Autowired
	private VoteRepository voteRepo;
	
	@Transactional(readOnly = true)
	public Page<TopicModel> getRunningTopics(Pageable page) {
		Page<Topic> topicsPage = topicRepo.findAll(page);
		return topicsPage.map(entity -> mapTopic(entity));
	}

	private TopicModel mapTopic(Topic topic) {
		TopicModel model = new TopicModel();
		model.setAuthor(topic.getAuthor().getForename() + " " + topic.getAuthor().getSurname());
		model.setDeadline(topic.getDeadline());
		model.setDescription(topic.getDescription());
		model.setParticipants(voteRepo.countByVotePkTopic(topic));
		model.setTitle(topic.getTitle());
		
		return model;
	}
	
}
