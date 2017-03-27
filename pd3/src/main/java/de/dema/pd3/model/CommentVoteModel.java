package de.dema.pd3.model;

import de.dema.pd3.persistence.CommentVote;

public class CommentVoteModel extends VoteModelBase {

	private Long commentId;
	
	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public static CommentVoteModel map(CommentVote vote) {
		if (vote == null) {
			return null;
		}
		CommentVoteModel model = new CommentVoteModel();
		VoteModelBase.map(vote, model);
		model.setCommentId(vote.getComment().getId());		
		
		return model;
	}
	
}
