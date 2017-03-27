package de.dema.pd3.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dema.pd3.Pd3Util;
import de.dema.pd3.VoteOption;
import de.dema.pd3.persistence.Comment;

public class CommentModel {

	private Long id;
	
	private String authorName;
	
	private boolean authorGenderMale;
	
	private Long authorId;
	
	private LocalDateTime creationTimestamp;
	
	private String text;
	
	private VoteOption userLikeSelection;

	private List<CommentModel> replies;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public LocalDateTime getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(LocalDateTime creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<CommentModel> getReplies() {
		return replies;
	}

	public void setReplies(List<CommentModel> children) {
		this.replies = children;
	}
	
	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public boolean isAuthorGenderMale() {
		return authorGenderMale;
	}

	public void setAuthorGenderMale(boolean authorGenderMale) {
		this.authorGenderMale = authorGenderMale;
	}

	public VoteOption getUserLikeSelection() {
		return userLikeSelection;
	}

	public void setUserLikeSelection(VoteOption userLikeSelection) {
		this.userLikeSelection = userLikeSelection;
	}

	public static CommentModel map(Comment comment, Function<Comment, VoteOption> userLikeSelection) {
		CommentModel model = new CommentModel();
		model.setId(comment.getId());
		model.setAuthorName(comment.getAuthor().getForename() + " " + comment.getAuthor().getSurname());
		model.setAuthorId(comment.getAuthor().getId());
		model.setCreationTimestamp(comment.getCreationDate());
		model.setText(Pd3Util.injectHtmlTags(comment.getText()));
		model.setUserLikeSelection(userLikeSelection.apply(comment));
		model.setAuthorGenderMale(comment.getAuthor().isMale());
		List<CommentModel> repliesList = comment.getReplies().stream().map(c -> CommentModel.map(c, userLikeSelection))
				.sorted((m1, m2) -> m2.getCreationTimestamp().compareTo(m1.getCreationTimestamp()))
				.collect(Collectors.toList());
		model.setReplies(repliesList);
		
		return model;
	}
	
}
