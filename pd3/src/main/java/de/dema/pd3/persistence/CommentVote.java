package de.dema.pd3.persistence;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("c")
public class CommentVote extends VoteBase {

	@ManyToOne
	private Comment comment;

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return "CommentVote [comment=" + comment + ", getId()=" + getId() + ", getUser()=" + getUser()
				+ ", getSelectedOption()=" + getSelectedOption() + ", getVoteTimestamp()=" + getVoteTimestamp() + "]";
	}

}
