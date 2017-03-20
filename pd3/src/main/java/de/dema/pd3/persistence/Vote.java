package de.dema.pd3.persistence;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Vote implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
    private VotePk votePk;
	
	private Boolean accepted;
	
	private LocalDateTime voteTimestamp;

	public Vote() {
	}

	public Vote(User user, Topic topic) {
		setVotePk(user, topic);
	}

	public VotePk getVotePk() {
		return votePk;
	}

	public void setVotePk(VotePk votePk) {
		this.votePk = votePk;
	}

	public void setVotePk(User user, Topic topic) {
		this.votePk = new VotePk(user, topic);
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public LocalDateTime getVoteTimestamp() {
		return voteTimestamp;
	}

	public void setVoteTimestamp(LocalDateTime voteTimestamp) {
		this.voteTimestamp = voteTimestamp;
	}

	@Embeddable
	public static class VotePk implements Serializable {
		private static final long serialVersionUID = 1L;
		@ManyToOne
		private User user;
		@ManyToOne
		private Topic topic;

	    public VotePk() {}

		public VotePk(User user, Topic topic) {
			this.user = user;
			this.topic = topic;
		}

		public Topic getTopic() {
			return topic;
		}

		public void setTopic(Topic topic) {
			this.topic = topic;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			VotePk votePk = (VotePk) o;

			return user.equals(votePk.user) && topic.equals(votePk.topic);

		}

		@Override
		public int hashCode() {
			int result = user.hashCode();
			result = 31 * result + topic.hashCode();
			return result;
		}
	}
}
