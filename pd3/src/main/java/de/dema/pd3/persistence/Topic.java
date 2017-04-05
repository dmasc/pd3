package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Topic implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
	private Long id;
	
    @Column(nullable = false, unique = true)
	private String title;
    
    @Column(nullable = false)
    @Lob
	private String description;
    
    @ManyToOne
	private User author;
    
    private LocalDateTime creationDate;

    private LocalDateTime deadline;
    
    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private Set<TopicVote> votes;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private Set<Comment> comments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Set<TopicVote> getVotes() {
		return votes;
	}

	public void setVotes(Set<TopicVote> votes) {
		this.votes = votes;
	}	

}
