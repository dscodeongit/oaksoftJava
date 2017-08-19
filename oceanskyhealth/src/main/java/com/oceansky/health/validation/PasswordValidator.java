package com.oceansky.health.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator{	

	  private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{6,20})";
	  private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
	  
	  /**
	   * Validate password with regular expression
	   * @param password password for validation
	   * @return true valid password, false invalid password
	   */
	  public static boolean validate(final String password){		  
		  Matcher matcher = pattern.matcher(password);
		  return matcher.matches();	    	    
	  }
	  
	  public static void main(String[] args){
		  String pass = "sD@94";
		  System.out.println(validate(pass));
	  }
}