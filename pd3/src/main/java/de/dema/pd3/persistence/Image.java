package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import de.dema.pd3.Clock;

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
	
	private String type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gibt die Bilddaten in Form eines Bas64-codierten Strings zurück.
	 * 
	 * @return die Bilddaten in Form eines Bas64-codierten Strings.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Setzt die Bilddaten in Form eines Bas64-codierten Strings.
	 * 
	 * @param data die Bilddaten in Form eines Bas64-codierten Strings.
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	/**
	 * Setzt die Bilddaten in Form von Bytes.
	 * 
	 * @param data die Bilddaten in Form von Bytes.
	 */
	public void setData(byte[] data) {
		this.data = Base64.getEncoder().encodeToString(data);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Baut einen String zusammen, der zur Anzeige des Bilds in einem {@code <img>}-Tag verwendet werden kann. Hierzu
	 * kann der Wert einfach bspw. wie folgt über das {@code th:src}-Attribut definiert werden:<br>
	 * <code>th:src="@{${imgTagSrcAttributeValue}}"</code>
	 *  
	 * @return der Bilddaten-String zur Anzeite in einem {@code <img>}-Tag.
	 */
	public String getImgTagSrcAttributeValue() {
		return "data:image/" + getType() + ";base64," + getData();		
	}

	public static class Builder {
		
		private Image image = new Image();
		
		public Builder(byte[] data) {
			image.setData(data);
			image.setUploadTimestamp(Clock.now());
		}
		
		public Builder owner(User owner) {
			image.setOwner(owner);
			return this;
		}

		public Builder type(String type) {
			image.setType(type);
			return this;
		}
		
		public Image build() {
			return image;
		}
		
	}
	
}
