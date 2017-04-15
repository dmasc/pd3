package de.dema.pd3.model;

import de.dema.pd3.persistence.User;

public class SimpleUserModel extends NamedIdModel {

	private boolean male;
	
	private String imageData;

	public SimpleUserModel() {
	}

	public SimpleUserModel(Long id, String name, boolean male, String imageData) {
		super(id, name);
		this.male = male;
		this.imageData = imageData;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public String getImageData() {
		return imageData;
	}

	public void setImageData(String imageData) {
		this.imageData = imageData;
	}

	public static SimpleUserModel map(User user) {
		if (user == null) {
			return null;
		}
		
		SimpleUserModel model = NamedIdModel.map(user, new SimpleUserModel());
		model.setMale(user.isMale());
		model.setImageData(user.getProfilePictureSmall().getImgTagSrcAttributeValue());
		
		return model;
	}
	
}
