package com.amazonaws.lambda.demo.http;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class VoteRequest {
	String alternative; //aid
	String member; //mid
	String kind; //approval or disapproval

	public String getAlternative() { return alternative; }
	public void setAlternative(String alternative) { this.alternative = alternative; }
	
	public String getMember() { return member; }
	public void setMember(String member) { this.member = member; }
	
	public String getKind() { return kind; }
	public void setKind(String kind) { this.kind = kind; }

	public String toString() {
		return "VoteRequest(" + alternative + "," + member + "," + kind + ")";
	}
	
	public VoteRequest (String alternative, String member, String kind) {
		this.alternative = alternative;
		this.member = member;
		this.kind = kind;
	}
	
	public VoteRequest() {
	}
}
