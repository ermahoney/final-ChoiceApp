package com.amazonaws.lambda.demo.model;

public class Vote { //not modified from Constants.java
	public final String vid;
	public String mid;
	public final String username;
	public final String aid;
	public final String kind;
	
	public String getVid() {return vid;}
	public String getMid() {return mid;}
	public String getUsername() {return username;}
	public String getAid() {return aid;}
	public String getKind() {return kind;}
	
	public Vote (String vid, String mid, String aid, String kind, String username) {
		this.vid = vid;
		this.mid = mid;
		this.aid = aid;
		this.kind = kind;
		this.username = username;
	}
	
	public Vote() {
		vid = null;
		mid = null;
		aid = null;
		kind = null;
		username = null;
	}
	
	public boolean equals (Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Vote) {
			Vote other = (Vote) o;
			return vid.equals(other.vid);
		}
		
		return false; 
	}
	
	public String toString() {
		return "Vote " + vid + " in Alternative :" + aid + " \n\tby Member " + mid + " \n\tusername: "+username+" \n\tis of kind: " + kind;
	}
}
