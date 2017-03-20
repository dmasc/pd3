package de.dema.pd3.services;

import de.dema.pd3.persistence.Topic;
import de.dema.pd3.persistence.TopicRepository;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;
import de.dema.pd3.persistence.Vote;
import de.dema.pd3.persistence.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    @Autowired
    private VoteRepository voteRepo;

    @Autowired
    private TopicRepository topicRepo;

    @Autowired
    private UserRepository userRepo;

    public void storeVote(String userEmail, long topicId, boolean accepted) {
        User user = userRepo.findByEmail(userEmail);
        Topic topic = topicRepo.findOne(topicId);

        Vote vote = new Vote();
        vote.setVotePk(user, topic);
        vote.setAccepted(accepted);
        vote.setVoteTimestamp(LocalDateTime.now());

        voteRepo.save(vote);
    }
}
