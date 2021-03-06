package de.dema.pd3.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("t")
public class TopicVote extends VoteBase {

	@ManyToOne
	private Topic topic;

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	
	public static class Builder extends VoteBase.Builder<TopicVote> {

		public Builder() {
			super(new TopicVote());
		}
		
		public void topic(Topic topic) {
			build().topic = topic;
		}
		
	}
	
}
