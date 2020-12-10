package com.amazonaws.lambda.demo.http;

import java.util.ArrayList;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class ChoiceCreationRequest {
	String description;
	ArrayList<String> alternatives = new ArrayList<String>();
	int numOfMembers;

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }
	
	public ArrayList<String> getAlternatives() { return alternatives; }
	public void setAlternatives(ArrayList<String> alternatives) { this.alternatives = alternatives; }
	
	public int getNumOfMembers() { return numOfMembers; }
	public void setNumOfMembers(int numOfMembers) { this.numOfMembers = numOfMembers; }

	public String toString() {
		return "ChoiceCreationRequest(" + description + "," + alternatives + "," + numOfMembers + ")";
	}
	
	public ChoiceCreationRequest (String description, ArrayList<String> alternatives, int numOfMembers) {
		this.description = description;
		this.alternatives = alternatives;
		this.numOfMembers = numOfMembers;
	}
	
	public ChoiceCreationRequest() {
	}
}
