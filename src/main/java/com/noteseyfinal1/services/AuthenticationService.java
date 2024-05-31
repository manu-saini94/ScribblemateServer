package com.noteseyfinal1.services;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.noteseyfinal1.dto.LoginDto;
import com.noteseyfinal1.dto.RegistrationDto;
import com.noteseyfinal1.entities.User;
import com.noteseyfinal1.exceptions.users.RegistrationException;
import com.noteseyfinal1.repositories.UserRepository;
import com.noteseyfinal1.utility.UserUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {

	private Random random = new Random(1000);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;
	
//	@Autowired
//	private CollaboratorRepository collaboratorRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User signUp(RegistrationDto input) {
		User user= null;
//		Collaborator collaborator = null;
		try {
			user = new User().setFullName(input.getFullName()).setEmail(input.getEmail())
					.setPassword(passwordEncoder.encode(input.getPassword()));
//			collaborator = new Collaborator();
//			collaborator.setCollaboratorName(input.getFullName());
//			collaborator.setEmail(input.getEmail());		
//			collaboratorRepository.save(collaborator);
			return userRepository.save(user);
		} catch (Exception exp) {
//			log.error(UserUtils.ERROR_PERSISTING_USER_OR_COLLABORATOR,user,collaborator);
			throw new RegistrationException();	
		}
	}

	public User authenticate(LoginDto input) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
		return userRepository.findByEmail(input.getEmail()).orElseThrow();
	}

	public boolean forgot(String email) {
		int otp = random.nextInt(10000);
		String subject = "OTP from notesy";
		String message = "<h1> OTP = " + otp + "</h1>";
		String to = email;
		boolean flag = emailService.sendEmail(subject, message, to);
		return flag;
	}
}
