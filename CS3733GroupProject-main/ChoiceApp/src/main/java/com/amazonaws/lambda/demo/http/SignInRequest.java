package com.amazonaws.lambda.demo.http;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class SignInRequest {
	String choiceID;
	String username;
	String password;

	public String getChoiceID() { return choiceID; }
	public void setChoiceID(String choiceID) { this.choiceID = choiceID; }
	
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public String toString() {
		return "SignInRequest(" + choiceID + "," + username + "," + password + ")";
	}
	
	public SignInRequest(String choiceID, String username, String password) {
		this.choiceID = choiceID;
		this.username = username;
		this.password = password;
	}
	
	public SignInRequest() {
	}
}
