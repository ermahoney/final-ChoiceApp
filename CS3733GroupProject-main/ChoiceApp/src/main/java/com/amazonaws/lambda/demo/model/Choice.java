package com.amazonaws.lambda.demo.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

public class Choice implements Iterable<Alternative>{ //not modified from Constants.java
	public final String cid;
	public String chosenAid; //if -1, choice has not been decided/chosen yet
	public final String description;
	public final int numMembers;
	public final String dateOfCreation;
	public String dateOfCompletion;
	public ArrayList<Alternative> alternatives = new ArrayList<Alternative>();
	public ArrayList<Member> members = new ArrayList<Member>();
	
	public String getCid() {return cid;}
	public String getChosenAid() {return chosenAid;}
	public String getDescription() {return description;}
	public int getNumMembers() {return numMembers;}
	public String getDateOfCreation() {return dateOfCreation;}
	public String getDateOfCompletion() {return dateOfCompletion;}
	public ArrayList<Alternative> getAlternatives() { return alternatives;}
	public ArrayList<Member> getMembers() {return members;}
	
	public Choice(String cid, String description, int numMembers, String dateOfCreation) {
		this.cid = cid;
		this.chosenAid = null;
		this.description = description;
		this.numMembers = numMembers;
		this.dateOfCreation = dateOfCreation;
		this.dateOfCompletion = null;
	}
	
	public Choice(String cid, String chosenAid, String des, int nm, String creation, String completion, ArrayList<Alternative> a, ArrayList<Member> m) {
		this.cid = cid;
		this.chosenAid = chosenAid; //can be changed
		this.description = des;
		this.numMembers = nm;
		this.dateOfCreation = creation;
		this.dateOfCompletion = completion; //can be changed
		this.alternatives = a;
		this.members = m;
	}
	
	public Choice(String cid, String ca, String des, int nm, String creation, String completion) {
		this.cid = cid;
		this.chosenAid = ca;
		this.description = des;
		this.numMembers = nm;
		this.dateOfCreation = creation;
		this.dateOfCompletion = completion;
	}
	
	public Choice() {
		this.cid = null;
		this.chosenAid = null;
		this.description = null;
		this.numMembers = -1;
		this.dateOfCreation = null;
		this.dateOfCompletion = null;
	}
	
	public void addAlternative(Alternative a) {
		alternatives.add(a);
	}
	
	public void addVoteToAlternative(String aid, Vote v) {
		for(Alternative alt: alternatives) {
			if(alt.aid == aid) {
				alt.votes.add(v);
			}
		}
	}
	
	public String votesToString(String aid) {
		String result = "";
		for(Alternative alt: alternatives) {
			if(alt.aid == aid) {
				for(int v = 0; v < alt.votes.size(); v++) {
					result += "Vote " + v + " from member " + alt.votes.get(v).mid + " of kind " + alt.votes.get(v).kind;
				}
			}
		}
		if(result.equals("")) {
			return "no votes";
		} else {
			return result;
		}
	}
	
	public void removeVoteFromAlternative(String aid, String mid) {
		for(Alternative alt: alternatives) {
			if(alt.aid == aid) {
				for(Vote vote: alt.votes) {
					if(vote.mid == mid) {
						alt.removeVote(mid);
					}
				}
			}
		}
	}
	
	public boolean equals(Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Choice) {
			Choice other = (Choice) o;
			return cid.equals(other.cid);
		}
		
		return false;  // not a Constant
	}
	
	public String toString() {
		return "Choice " + cid + " has description: " + description;
	}
	
	@Override
	public Iterator<Alternative> iterator() {
		return alternatives.iterator();
	}
}
