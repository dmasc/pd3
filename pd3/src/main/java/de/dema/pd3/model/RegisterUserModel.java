package de.dema.pd3.model;

<<<<<<< HEAD
=======
import java.time.LocalDate;
>>>>>>> branch 'master' of https://github.com/dmasc/pd3.git
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.validation.constraints.Pattern;

import de.dema.pd3.validation.MatchingFields;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import de.dema.pd3.persistence.User;
import de.dema.pd3.validation.Age;
import de.dema.pd3.validation.MatchingFields;
import de.dema.pd3.validation.PersoId;
import org.springframework.format.datetime.joda.DateTimeFormatterFactory;

@MatchingFields(message = "{register_user_model.passwordRepeat.notequal}", first = "password",
		second = "passwordRepeat", groups = RegisterUserModel.RegisterUserValidation.class)
public class RegisterUserModel {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("de"));

	private Long id;
	
	@NotEmpty(message = "{register_user_model.forename.null}", groups = RegisterUserValidation.class)
	private String forename;

	@NotEmpty(message = "{register_user_model.surname.null}", groups = RegisterUserValidation.class)
	private String surname;

	@Email(message = "{register_user_model.email.format}", groups = RegisterUserValidation.class)
	@NotEmpty(message = "{register_user_model.email.null}", groups = RegisterUserValidation.class)
	private String email;

	@NotEmpty(message = "{register_user_model.password.null}", groups = RegisterUserValidation.class)
	private String password;

	@NotEmpty(message = "{register_user_model.passwordRepeat.null}", groups = RegisterUserValidation.class)
    private String passwordRepeat;

	@NotEmpty(message = "{register_user_model.street.null}", groups = RegisterUserValidation.class)
	private String street;

	@NotEmpty(message = "{register_user_model.district.null}", groups = RegisterUserValidation.class)
	private String district;

	@Pattern(regexp = "\\d{5}", message = "{register_user_model.zip.format}", groups = RegisterUserValidation.class)
	private String zip;
	
	@Pattern(regexp = "\\+?[\\d -/]{6}", message = "{register_user_model.phone.format}", groups = RegisterUserValidation.class)
	private String phone;

	@Pattern(message = "{register_user_model.birthday.format}", regexp = "\\d{2}\\.\\d{2}\\.\\d{4}", groups = RegisterUserValidation.class)
	@Age(message = "{register_user_model.birthday.age}", minAge = 16, groups = RegisterUserValidation.class)
	private String birthday;

	@PersoId(message = "{register_user_model.idCardNumber.format}", groups = RegisterUserValidation.class)
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

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
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
		model.setBirthday(user.getBirthday().format(DATE_FORMATTER));
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

	public interface RegisterUserValidation {}

}
