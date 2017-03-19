package de.dema.pd3.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.dema.pd3.model.RegisterUserModel;
import de.dema.pd3.persistence.User;
import de.dema.pd3.persistence.UserRepository;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepo;
	
	public User registerUser(RegisterUserModel userModel) {
		log.debug("registerUser called [model:{}]", userModel);
		
		User user = new User();
		user.setBirthday(userModel.getBirthday());
		user.setDistrict(userModel.getDistrict());
		user.setEmail(userModel.getEmail());
		user.setForename(userModel.getForename());
		user.setIdCardNumber(userModel.getIdCardNumber());
		user.setPassword(userModel.getPassword());
		user.setPhone(userModel.getPhone());
		user.setStreet(userModel.getStreet());
		user.setSurname(userModel.getSurname());
		user.setZip(userModel.getZip());
		
		user = userRepo.save(user);
		log.info("user registered [id:{}]", user.getId());
		return user;
	}
	
}
