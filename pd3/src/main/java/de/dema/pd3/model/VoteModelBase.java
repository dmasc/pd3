package de.dema.pd3.model;

import java.time.LocalDateTime;

import de.dema.pd3.VoteOption;
import de.dema.pd3.persistence.VoteBase;

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

	public static VoteModelBase map(VoteBase vote) {
		return map(vote, new VoteModelBase());
	}
	
	public static <T extends VoteModelBase> T map(VoteBase vote, T target) {
		if (vote == null) {
			return null;
		}

		target.setVoteTimestamp(vote.getVoteTimestamp());
		target.setSelectedOption(vote.getSelectedOption());
		
		return target;
	}
	
}
