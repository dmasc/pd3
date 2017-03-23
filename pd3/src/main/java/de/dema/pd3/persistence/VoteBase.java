package de.dema.pd3.persistence;

import java.time.LocalDateTime;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import de.dema.pd3.VoteOption;

@Entity
@Table(name = "VOTE")  
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)  
@DiscriminatorColumn(name="type",discriminatorType=DiscriminatorType.CHAR)  
@DiscriminatorValue(value="d")
public class VoteBase {

	@Id
    @GeneratedValue
	private Long id;
	
	@ManyToOne
	private User user;

	@Enumerated(EnumType.STRING)
	private VoteOption selectedOption;
	
	private LocalDateTime voteTimestamp;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public VoteOption getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(VoteOption selectedOption) {
		this.selectedOption = selectedOption;
	}

	public LocalDateTime getVoteTimestamp() {
		return voteTimestamp;
	}

	public void setVoteTimestamp(LocalDateTime voteTimestamp) {
		this.voteTimestamp = voteTimestamp;
	}

}
