package com.aspire.controller;


import com.aspire.utils.ResponseCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aspire.exceptions.UserException;
import com.aspire.model.UserData;
import com.aspire.repository.UserRepository;
import com.aspire.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// register user with given details
	
	@PostMapping("/app/signup")
	public ResponseEntity<String> registerUser(@Validated @RequestBody UserData user) throws UserException
	{
		if (user.getRole().isBlank() || !(user.getRole().equalsIgnoreCase("ROLE_USER") || user.getRole().equalsIgnoreCase("ROLE_ADMIN"))) {
			return new ResponseEntity<>(ResponseCodes.INVALID_ROLE.getValue(), HttpStatus.BAD_REQUEST);
		}

		UserData userData = userService.fetchUser(user.getEmail());

		if(userData != null) {
			return new ResponseEntity<>(ResponseCodes.USER_ALREADY_EXIST.getValue(),HttpStatus.BAD_REQUEST);
		}

		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole(user.getRole());



		userService.registerUser(user);
		
		return new ResponseEntity<>(ResponseCodes.SIGNUP_SUCCESSFULL.getValue(),HttpStatus.CREATED);
	}
	
	// first time user login with Email and password and got JWT token 
	
	@GetMapping("/signIn")
	public ResponseEntity<String> getLoggedInCustomerDetailsHandler(Authentication auth) {
		
		UserData customer= userRepository.findByEmail(auth.getName());
		
		if(customer!=null )
		{
			if (customer.isVerified()) {
				return new ResponseEntity<>(ResponseCodes.LOGIN_SUCCESSFUL.getValue(), HttpStatus.ACCEPTED);
			}
			else {
				return new ResponseEntity<>(ResponseCodes.VERIFY_EMAIL_LOGIN.getValue(), HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>(ResponseCodes.SIGN_UP_FIRST.getValue(),HttpStatus.OK);
		}
	}
	
	// Authentication with JWT token 
	
	@GetMapping("/logged-in/user")
	public ResponseEntity<String> LoginUser() throws UserException
	{
		UserData user =  userService.loginUser();

		if (user != null) {
			String message = "Welcome to Yash's API World  : " +user.getFullName() + " role: " + user.getRole();

			return new ResponseEntity<String>(message,HttpStatus.OK);
		}

		return new ResponseEntity<String>(ResponseCodes.LOGIN_NECESSARY.getValue(), HttpStatus.OK);
	}
}
