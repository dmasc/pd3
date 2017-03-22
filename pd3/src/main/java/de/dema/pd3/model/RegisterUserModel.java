package de.dema.pd3.model;

import de.dema.pd3.validation.Birthday;
import de.dema.pd3.validation.PLZ;
import de.dema.pd3.validation.PersoId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class RegisterUserModel {

	@NotNull(message = "{register_user_model.forename.null}")
	@NotEmpty(message = "{register_user_model.forename.null}")
	private String forename;

	@NotNull(message = "{register_user_model.surname.null}")
	@NotEmpty(message = "{register_user_model.surname.null}")
	private String surname;

	@Email(message = "{register_user_model.email.format}")
	@NotNull(message = "{register_user_model.email.null}")
	@NotEmpty(message = "{register_user_model.email.null}")
	private String email;

	@NotNull(message = "{register_user_model.password.null}")
	@NotEmpty(message = "{register_user_model.password.null}")
	private String password;

	@NotNull(message = "{register_user_model.passwordRepeat.null}")
	@NotEmpty(message = "{register_user_model.passwordRepeat.null}")
	private String passwordRepeat;

	@NotNull(message = "{register_user_model.street.null}")
	@NotEmpty(message = "{register_user_model.street.null}")
	private String street;

	@NotNull(message = "{register_user_model.district.null}")
	@NotEmpty(message = "{register_user_model.district.null}")
	private String district;

	@PLZ(message = "{register_user_model.zip.format}")
	private String zip;
	
	private String phone;

	@DateTimeFormat(pattern = "dd.MM.yyyy")
	@Birthday(message = "{register_user_model.birthday.age}")
	private LocalDate birthday;

	@PersoId(message = "{register_user_model.idCardNumber.format}")
	private String idCardNumber;

	public RegisterUserModel() {
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
	
}
