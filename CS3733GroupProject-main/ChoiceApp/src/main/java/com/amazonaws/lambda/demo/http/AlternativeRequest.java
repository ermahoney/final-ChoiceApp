package com.amazonaws.lambda.demo.http;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class AlternativeRequest {
	String aid;

	public String getAid() { return aid; }
	public void setAid(String aid) { this.aid = aid; }
	
	public String toString() {
		return "AlternativeRequest(" + aid + ")";
	}
	
	public AlternativeRequest (String aid) {
		this.aid = aid;;
	}
	
	public AlternativeRequest() {
	}
}
