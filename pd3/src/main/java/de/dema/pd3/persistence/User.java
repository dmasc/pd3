package de.dema.pd3.persistence;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
	
}
