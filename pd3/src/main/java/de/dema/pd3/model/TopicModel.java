package de.dema.pd3.model;

import java.time.LocalDateTime;

import de.dema.pd3.VoteOption;

public class TopicModel {

	private Long id;

	private String title;
	
	private String description;
	
	private LocalDateTime deadline;
	
	private String author;
	
	private int participants;
	
	private VoteOption userVoteSelection;

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

	public LocalDateTime getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDateTime deadline) {
		this.deadline = deadline;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getParticipants() {
		return participants;
	}

	public void setParticipants(int participants) {
		this.participants = participants;
	}

	public VoteOption getUserVoteSelection() {
		return userVoteSelection;
	}

	public void setUserVoteSelection(VoteOption userVoteSelection) {
		this.userVoteSelection = userVoteSelection;
	}
	
	public boolean isRunning() {
		return LocalDateTime.now().isBefore(this.deadline);
	}

}
