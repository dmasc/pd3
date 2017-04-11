package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	private LocalDateTime creationDate;
	
	@Lob
	private String text;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private Set<Comment> replies;
	
	@OneToOne(optional = true)
	private Comment parent;

	@ManyToOne
	private Topic topic;
	
	@ManyToOne
	private User author;
	
	@OneToMany(mappedBy="comment", cascade = CascadeType.REMOVE)
	private Set<CommentVote> votes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Set<Comment> getReplies() {
		return replies;
	}

	public void setReplies(Set<Comment> replies) {
		this.replies = replies;
	}

	public Comment getParent() {
		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Set<CommentVote> getVotes() {
		return votes;
	}

	public void setVotes(Set<CommentVote> votes) {
		this.votes = votes;
	}

	public static class Builder {
		
		private Comment comment = new Comment();
		
		public Builder creationDate(LocalDateTime creationDate) {
			comment.creationDate = creationDate;
			return this;
		}

		public Builder text(String text) {
			comment.text = text;
			return this;
		}

		public Builder replies(Set<Comment> replies) {
			comment.replies = replies;
			return this;
		}

		public Builder parent(Comment parent) {
			comment.parent = parent;
			return this;
		}

		public Builder topic(Topic topic) {
			comment.topic = topic;
			return this;
		}

		public Builder author(User author) {
			comment.author = author;
			return this;
		}

		public Builder votes(Set<CommentVote> votes) {
			comment.votes = votes;
			return this;
		}

		public Comment build() {
			return comment;
		}

	}
	
}
