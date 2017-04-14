package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Image implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Lob
	private String data;
	
	@ManyToOne
	private User owner;
	
	private LocalDateTime uploadTimestamp;
	
	@Enumerated(EnumType.STRING)
	private ImageType type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public LocalDateTime getUploadTimestamp() {
		return uploadTimestamp;
	}

	public void setUploadTimestamp(LocalDateTime uploadTimestamp) {
		this.uploadTimestamp = uploadTimestamp;
	}

	public ImageType getType() {
		return type;
	}

	public void setType(ImageType type) {
		this.type = type;
	}

	public static enum ImageType {
		
		JPG("jpeg"), SVG("svg"), PNG("png"), GIF("gif");
		
		private String typeString;
		
		private ImageType(String typeString) {
			this.typeString = typeString;
		}

		public String getTypeString() {
			return typeString;
		}

	}
	
}
