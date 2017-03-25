package de.dema.pd3.model;

import java.time.LocalDate;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import de.dema.pd3.persistence.User;
import de.dema.pd3.validation.Age;
import de.dema.pd3.validation.PersoId;

public class RegisterUserModel {

	private Long id;
	
	@NotEmpty(message = "{register_user_model.forename.null}")
	private String forename;

	@NotEmpty(message = "{register_user_model.surname.null}")
	private String surname;

	@Email(message = "{register_user_model.email.format}")
	@NotEmpty(message = "{register_user_model.email.null}")
	private String email;

	@NotEmpty(message = "{register_user_model.password.null}")
	private String password;

	@NotEmpty(message = "{register_user_model.passwordRepeat.null}")
    private String passwordRepeat;

	@NotEmpty(message = "{register_user_model.street.null}")
	private String street;

	@NotEmpty(message = "{register_user_model.district.null}")
	private String district;

	@Pattern(regexp = "\\d{5}}", message = "{register_user_model.zip.format}")
	private String zip;
	
	@Pattern(regexp = "\\+?[\\d -/]{6m}", message = "{register_user_model.phone.format}")
	private String phone;

	@Age(message = "{register_user_model.birthday.age}", minAge = 16)
	@DateTimeFormat(pattern = "dd.MM.yyyy")
	private LocalDate birthday; // TODO: Validation does not work for invalid dates (no err msg shown)

	@PersoId(message = "{register_user_model.idCardNumber.format}")
	private String idCardNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getPasswordRepeat() {
		return passwordRepeat;
	}

	public void setPasswordRepeat(String passwordRepeat) {
		this.passwordRepeat = passwordRepeat;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
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

	public String getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}
	
	public static RegisterUserModel map(User user) {
		if (user == null) {
			return null;
		}
		
		RegisterUserModel model = new RegisterUserModel();
		model.setId(user.getId());
		model.setBirthday(user.getBirthday());
		model.setDistrict(user.getDistrict());
		model.setEmail(user.getEmail());
		model.setForename(user.getForename());
		model.setIdCardNumber(user.getIdCardNumber());
		model.setPhone(user.getPhone());
		model.setStreet(user.getStreet());
		model.setSurname(user.getSurname());
		model.setZip(user.getZip());
		
		return model;
	}
}
