package de.dema.pd3.persistence;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Vote {

	@EmbeddedId
    private VotePk votePk;
	
	private Boolean accepted;
	
	public VotePk getVotePk() {
		return votePk;
	}

	public void setVotePk(VotePk votePk) {
		this.votePk = votePk;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@Embeddable
	public class VotePk implements Serializable {
		private static final long serialVersionUID = 1L;
		protected User user;
		protected Topic topic;

	    public VotePk() {}

		public VotePk(User user, Topic topic) {
			this.user = user;
			this.topic = topic;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((topic == null) ? 0 : topic.hashCode());
			result = prime * result + ((user == null) ? 0 : user.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VotePk other = (VotePk) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (topic == null) {
				if (other.topic != null)
					return false;
			} else if (!topic.equals(other.topic))
				return false;
			if (user == null) {
				if (other.user != null)
					return false;
			} else if (!user.equals(other.user))
				return false;
			return true;
		}

		private Vote getOuterType() {
			return Vote.this;
		}

	}
}
