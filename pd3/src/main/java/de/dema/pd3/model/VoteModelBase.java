package de.dema.pd3.model;

import de.dema.pd3.VoteOption;
import de.dema.pd3.persistence.VoteBase;

import java.time.LocalDateTime;

public class VoteModelBase {
	
	private LocalDateTime voteTimestamp;

	private VoteOption selectedOption;
	
	public LocalDateTime getVoteTimestamp() {
		return voteTimestamp;
	}

	public void setVoteTimestamp(LocalDateTime voteTimestamp) {
		this.voteTimestamp = voteTimestamp;
	}

	public VoteOption getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(VoteOption selectedOption) {
		this.selectedOption = selectedOption;
	}
	
	public boolean isAccepted() {
		return VoteOption.ACCEPTED.equals(this.selectedOption);
	}

	public boolean isRejected() {
		return VoteOption.REJECTED.equals(this.selectedOption);
	}

	public boolean isAbstention() {
		return VoteOption.ABSTENTION.equals(this.selectedOption);
	}

	static void map(VoteBase vote, VoteModelBase target) {
		target.setVoteTimestamp(vote.getVoteTimestamp());
		target.setSelectedOption(vote.getSelectedOption());
	}
	
	public static VoteModelBase map(VoteBase vote) {
		if (vote == null) {
			return null;
		}
		VoteModelBase model = new VoteModelBase();
		map(vote, model);
		
		return model;
	}
	
}
