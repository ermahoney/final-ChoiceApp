package com.amazonaws.lambda.demo.http;

/** To work with AWS must not have final attributes, must have no-arg constructor, and all get/set methods. */
public class FeedbackRequest {
	String alternative; //aid
	String content;
	String member; //mid
	String timestamp; //approval or disapproval

	public String getAlternative() { return alternative; }
	public void setAlternative(String alternative) { this.alternative = alternative; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	
	public String getMember() { return member; }
	public void setMember(String member) { this.member = member; }
	
	public String getTimestamp() { return timestamp; }
	public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

	public String toString() {
		return "FeedbackRequest(" + alternative + "," + member + ", content " + content + ", timestamp " + timestamp + ")";
	}
	
	public FeedbackRequest (String alternative, String content, String member, String timestamp) {
		this.alternative = alternative;
		this.content = content;
		this.member = member;
		this.timestamp = timestamp;
	}
	
	public FeedbackRequest() {
	}
}
