package com.amazonaws.lambda.demo.model;

import java.sql.Timestamp;

public class Feedback { //not modified from Constants.java
	public final String fid;
	public String mid;
	public final String username;
	public final String aid;
	public final String content;
	public final Timestamp timestamp;
	
	public String getFid() {return fid;}
	public String getMid() {return mid;}
	public String getUsername() {return username;}
	public String getAid() {return aid;}
	public String getContent() {return content;}
	public Timestamp getTimestamp() {return timestamp;}
	
	public Feedback (String fid, String mid, String aid, String content, Timestamp time, String username) {
		this.fid = fid;
		this.mid = mid;
		this.username = username;
		this.aid = aid;
		this.content = content;
		this.timestamp = time;
	}
	
	public Feedback() {
		fid = null;
		mid = null;
		aid = null;
		content = null;
		timestamp = null;
		username = null;
	}
	
	public boolean equals (Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Feedback) {
			Feedback other = (Feedback) o;
			return fid.equals(other.fid);
		}
		
		return false; 
	}
	
	public String toString() {
		return "Feedback " + fid + " in Alternative: " + aid + " \n\tby Member " + mid + " \n\tusername: "+username+" \n\thas content: " + content;
	}
}
