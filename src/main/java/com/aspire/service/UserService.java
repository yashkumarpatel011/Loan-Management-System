package com.aspire.service;

import com.aspire.exceptions.UserException;
import com.aspire.model.UserData;

public interface UserService {

	public UserData registerUser(UserData user) throws UserException;
	public UserData loginUser()  throws UserException;
	public UserData fetchAdminUser() ;

	public void validateUser(UserData user);

	public UserData fetchUser(String email);

}
