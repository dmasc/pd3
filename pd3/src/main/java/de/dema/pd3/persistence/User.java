package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@Cacheable
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue
	private Long id;
	
    @Column(nullable = false, unique = true)
	private String email;
    
    @Column(nullable = true)
	private String password;
    
    @Column(nullable = false, unique = true)
	private String idCardNumber;

	@Column(nullable = false)
	private String forename;
    
	@Column(nullable = false)
	private String surname;
    
	private String street;
	
	private String zip;
	
	private String district;
	
	private String countryCode = Locale.GERMANY.getCountry();
	
	private String phone;
	
    @Column(nullable = false)
	private LocalDate birthday;

	private LocalDateTime lastLogin;
	
	private boolean male;
    
    private Boolean locked = Boolean.FALSE;
    
    @OneToMany(mappedBy = "id.user", cascade = CascadeType.REMOVE)
    private Set<ChatroomUser> chatroomUsers;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private PasswordResetToken passwordResetToken;
    
    @OneToOne
    private Image profilePicture;
    
    @OneToOne
    private Image profilePictureSmall;
    
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private Set<Image> images;
	
	public User() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(LocalDateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getForename() {
		return forename;
	}

	public void setForename(String forename) {
		this.forename = forename;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	public boolean isMale() {
		return male;
	}

	public void setMale(boolean male) {
		this.male = male;
	}

	public Set<ChatroomUser> getChatroomUsers() {
		return chatroomUsers;
	}

	public void setChatroomUsers(Set<ChatroomUser> chatroomUsers) {
		this.chatroomUsers = chatroomUsers;
	}

	public PasswordResetToken getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(PasswordResetToken passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public Image getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(Image profilePicture) {
		this.profilePicture = profilePicture;
	}

	public Image getProfilePictureSmall() {
		return profilePictureSmall;
	}

	public void setProfilePictureSmall(Image profilePictureSmall) {
		this.profilePictureSmall = profilePictureSmall;
	}

	public static class Builder {

		private User user = new User();
		
		public Builder lastLogin(LocalDateTime lastLogin) {
			user.lastLogin = lastLogin;
			return this;
		}

		public Builder email(String email) {
			user.email = email;
			return this;
		}

		public Builder password(String password) {
			user.password = password;
			return this;
		}

		public Builder locked() {
			user.locked = true;
			return this;
		}

		public Builder idCardNumber(String idCardNumber) {
			user.idCardNumber = idCardNumber;
			return this;
		}

		public Builder forename(String forename) {
			user.forename = forename;
			return this;
		}

		public Builder surname(String surname) {
			user.surname = surname;
			return this;
		}

		public Builder street(String street) {
			user.street = street;
			return this;
		}

		public Builder zip(String zip) {
			user.zip = zip;
			return this;
		}

		public Builder district(String district) {
			user.district = district;
			return this;
		}

		public Builder countryCode(String countryCode) {
			user.countryCode = countryCode;
			return this;
		}

		public Builder phone(String phone) {
			user.phone = phone;
			return this;
		}

		public Builder birthday(LocalDate birthday) {
			user.birthday = birthday;
			return this;
		}

		public Builder male(boolean male) {
			user.male = male;
			return this;
		}

		public Builder male() {
			return male(true);
		}
		
		public Builder female() {
			return male(false);
		}
		
		public Builder chatroomUsers(Set<ChatroomUser> chatroomUsers) {
			user.chatroomUsers = chatroomUsers;
			return this;
		}

		public Builder addChatroomUser(ChatroomUser chatroomUser) {
			if (user.chatroomUsers == null) {
				user.chatroomUsers = new HashSet<>();
			}
			user.chatroomUsers.add(chatroomUser);
			return this;
		}
		
		public User build() {
			return user;
		}
		
	}
	
}
