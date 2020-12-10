package com.amazonaws.lambda.demo.http;

import java.util.ArrayList;

import com.amazonaws.lambda.demo.model.Choice;

/**
 * In most cases the response is the name of the constant that was being created.
 * 
 * if an error of some sort, then the response describes that error.
 *  
 */
public class ChoiceResponse {
	public final String error;
	public final int statusCode;
	public final ArrayList<Choice> choice; //create in demo.model
	
	public ChoiceResponse (String s, int statusCode, ArrayList<Choice> choice) {
		this.error = s;
		this.statusCode = statusCode;
		this.choice = choice;
	}
	
	// 200 means success
	public ChoiceResponse (String s, ArrayList<Choice> choice) {
		this.error = s;
		this.statusCode = 200;
		this.choice = choice;
	}
	
	public String getError() {return error;}

	public int getStatusCode() { return statusCode;}
	
	public ArrayList<Choice> getChoice() {return choice;}
	
	public String toString() {
		return "Response(" + error + ")";
	}
}
