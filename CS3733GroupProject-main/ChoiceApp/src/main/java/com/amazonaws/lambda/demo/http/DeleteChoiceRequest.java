package com.amazonaws.lambda.demo.http;

public class DeleteChoiceRequest {
	
	public double nDays;
	
	public double getNDays() { return nDays; }
	public void setNDays(double nDays) { this.nDays = nDays; }
	
	public DeleteChoiceRequest (double nDays){
		this.nDays = nDays;
	}
	
	DeleteChoiceRequest () {
		
	}
	
	public String toString() {
		return "ChoiceCreationRequest(" + nDays + ")";
	}
	
	
	
	

}
