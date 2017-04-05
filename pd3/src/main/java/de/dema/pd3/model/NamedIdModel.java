package de.dema.pd3.model;

import de.dema.pd3.persistence.User;

public class NamedIdModel {
	
	private Long id;
	
	private String name;

	public NamedIdModel() {		
	}
	
	public NamedIdModel(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public static NamedIdModel map(User user) {
		NamedIdModel model = new NamedIdModel();
		model.setId(user.getId());
		model.setName(user.getForename() + " " + user.getSurname());
		
		return model;
	}
	
}
