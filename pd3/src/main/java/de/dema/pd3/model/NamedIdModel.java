package de.dema.pd3.model;

import de.dema.pd3.persistence.User;

public class NamedIdModel {
	
	private Long id;
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public static NamedIdModel map(User user) {
		NamedIdModel model = new NamedIdModel();
		model.setId(user.getId());
		model.setText(user.getForename() + " " + user.getSurname());
		
		return model;
	}
	
}
