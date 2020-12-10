package com.amazonaws.lambda.demo.model;

public class Member { //not modified from Constants.java
	public final String mid;
	public final String cid;
	public final String name;
	public final String password;
	
	public String getMid() {return mid;}
	public String getCid() {return cid;}
	public String getName() {return name;}
	public String getPassword() {return password;}
	
	public Member (String mid, String cid, String name, String password) {
		this.mid = mid;
		this.cid = cid;
		this.name = name;
		this.password = password;
	}
	
	public Member (String mid, String cid, String name) {
		this.mid = mid;
		this.cid = cid;
		this.name = name;
		this.password = null;
	}
	
	public Member() {
		mid = null;
		cid = null;
		name = null;
		password = null;
	}
	
	public boolean equals (Object o) {
		if (o == null) { return false; }
		
		if (o instanceof Member) {
			Member other = (Member) o;
			return mid.equals(other.mid);
		}
		
		return false;
	}
	
	public String toString() {
		return "Member " + mid + " in Choice " + cid + " has name: " + name + " and password: " + password;
	}
}
