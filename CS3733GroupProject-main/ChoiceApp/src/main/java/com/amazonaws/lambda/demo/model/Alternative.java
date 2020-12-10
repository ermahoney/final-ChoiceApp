package com.amazonaws.lambda.demo.model;

import java.util.ArrayList;

public class Alternative { //not modified from Constants.java
	public final String aid;
	public final String cid;
	public final String description;
	//public final int altOrder;
	public ArrayList<Feedback> feedback = new ArrayList<Feedback>();
	public ArrayList<Vote> votes = new ArrayList<Vote>();
	
	public String getAid() {return aid;}
	public String getCid() {return cid;}
	public String getDescription() { return description;}
	//public int getAltOrder() {return altOrder;}
	public ArrayList<Feedback> getFeedback() {return feedback;}
	public ArrayList<Vote> getVotes() {return votes;}
	
	public Alternative (String aid, String cid, String des, ArrayList<Feedback> f, ArrayList<Vote> v) {
		this.aid = aid;
		this.cid = cid;
		this.description = des;
		//this.altOrder = index;
		this.feedback = f;
		this.votes = v;
	}
	
	public Alternative (String aid, String cid, String des) {
		this.aid = aid;
		this.cid = cid;
		this.description = des;
		//this.altOrder = index;
	}
	
	public Alternative() {
		aid = null;
		cid = null;
		description = null;
		//altOrder = -1;
	}
	
	public void removeVote(String mid) {
		for(int i = 0; i < votes.size(); i ++) {
			if(votes.get(i).mid == mid) {
				votes.remove(i);
			}
		}
	}
	
	public boolean equals (Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Alternative) {
			Alternative other = (Alternative) o;
			return aid.equals(other.aid);
		}
		
		return false;
	}
	
	public String toString() {
		return "Alternative " + aid + " in Choice " + cid + " has description: " + description;
	}
}
