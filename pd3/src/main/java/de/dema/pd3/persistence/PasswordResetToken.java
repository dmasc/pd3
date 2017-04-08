package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class PasswordResetToken implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int EXPIRATION_MINUTES = 5;
    
    @Id
    @GeneratedValue
    private Long id;
  
    private String token;
  
    @OneToOne
    private User user;
  
    private LocalDateTime expiryDate;

	public PasswordResetToken() {
	}

	public PasswordResetToken(String token, User user) {
		this.token = token;
		this.user = user;
		expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
    
}
