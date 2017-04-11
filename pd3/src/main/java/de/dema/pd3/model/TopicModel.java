package de.dema.pd3.model;

import java.time.LocalDateTime;

import de.dema.pd3.Clock;
import de.dema.pd3.VoteOption;
import de.dema.pd3.persistence.Topic;

public class TopicModel {

	private Long id;

	private String title;
	
	private String description;
	
	private LocalDateTime deadline;
	
	private String author;
	
	private int participants;
	
	private VoteOption userVoteSelection;
	
	private LocalDateTime creationDate;

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
	
	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isRunning() {
		return Clock.now().isBefore(this.deadline);
	}

	public static TopicModel map(Topic topic) {
		if (topic == null) {
			return null;
		}
		
        TopicModel model = new TopicModel();
        model.setAuthor(topic.getAuthor().getForename() + " " + topic.getAuthor().getSurname());
        model.setCreationDate(topic.getCreationDate());
        model.setDeadline(topic.getDeadline());
        model.setDescription(topic.getDescription());
        model.setId(topic.getId());
        model.setTitle(topic.getTitle());

        return model;
	}

}
