package de.dema.pd3.model;

import de.dema.pd3.persistence.TopicVote;

public class TopicVoteModel extends VoteModelBase {

	private String topicTitle;

	private Long topicId;
	
	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public static TopicVoteModel map(TopicVote vote) {
		if (vote == null) {
			return null;
		}
		TopicVoteModel model = new TopicVoteModel();
		VoteModelBase.map(vote, model);
		model.setTopicId(vote.getTopic().getId());
		model.setTopicTitle(vote.getTopic().getTitle());
		
		return model;
	}
	
}
