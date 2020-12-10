package com.amazonaws.lambda.demo.http;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class ChoiceRequest {
	String cid;

	public String getCid() { return cid; }
	public void setCid(String cid) { this.cid = cid; }
	
	public String toString() {
		return "ChoiceRequest(" + cid + ")";
	}
	
	public ChoiceRequest (String cid) {
		this.cid = cid;;
	}
	
	public ChoiceRequest() {
	}
}
