package com.scribblemate.services;

import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.scribblemate.dto.LoginDto;
import com.scribblemate.dto.RegistrationDto;
import com.scribblemate.entities.User;
import com.scribblemate.exceptions.users.RegistrationException;
import com.scribblemate.exceptions.users.UserAlreadyExistException;
import com.scribblemate.exceptions.users.UserNotFoundException;
import com.scribblemate.repositories.UserRepository;
import com.scribblemate.utility.UserUtils;
import com.scribblemate.utility.Utils.Status;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService {

	private Random random = new Random(1000);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User signUp(RegistrationDto input) {
		Optional<User> existingUser = userRepository.findByEmail(input.getEmail());
		if (existingUser.isPresent())
			throw new UserAlreadyExistException();
		User newUser = null;
		try {
			newUser = new User().setFullName(input.getFullName()).setEmail(input.getEmail())
					.setPassword(passwordEncoder.encode(input.getPassword())).setStatus(Status.ACTIVE);
			return userRepository.save(newUser);
		} catch (Exception exp) {
			log.error(UserUtils.ERROR_PERSISTING_USER, newUser);
			throw new RegistrationException();
		}
	}

	public User authenticate(LoginDto input) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return userRepository.findByEmail(input.getEmail()).orElseThrow(() -> {
			log.error(UserUtils.ERROR_USER_NOT_FOUND);
			return new UserNotFoundException();
		});
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
