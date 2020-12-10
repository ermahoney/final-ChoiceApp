package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.ChoiceResponse;
import com.amazonaws.lambda.demo.http.SignInRequest;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.lambda.demo.model.Member;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;


public class SignInChoiceHandler implements RequestHandler<SignInRequest,ChoiceResponse> {

	LambdaLogger logger;
	boolean fail = false;
	String failMessage = "";
	
	private AmazonS3 s3 = null;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	Choice loadChoiceFromRDS(String cid) throws Exception {
		if (logger != null) { logger.log("in loadValueFromRDS"); }
		ChoiceDAO dao = new ChoiceDAO();
		if (logger != null) { logger.log("retrieved DAO"); }
		if (logger != null) { logger.log("cid: " + cid); }
		Choice choice = dao.getChoice(cid, logger);
		if (logger != null) { logger.log("choice: " + choice.toString()); }
		logger.log(choice.toString());
		if (logger != null) { logger.log("retrieved Choice"); }
		return choice;
	}
	
	Choice getUpdatedChoice(String cid) {
		ChoiceDAO dao = new ChoiceDAO();
		Choice choice = null;
		try {
			choice = loadChoiceFromRDS(cid);
		} catch(Exception e){
			fail = true;
			failMessage = "Error getting choice.";
		}
		try {
			choice.alternatives = dao.getAltsForChoice(choice.cid);
		} catch(Exception e){
			fail = true;
			failMessage = "Error getting alternatives for choice";
		}

		try {
			choice.members = dao.getChoiceMembers(choice.cid);
		} catch (Exception e) {
			fail = true;
			failMessage = "Error getting the choice members.";
		}
		
		for(int i = 0; i < choice.alternatives.size(); i++) {
			try {
				choice.alternatives.get(i).votes = dao.getVotesForAlt(choice.alternatives.get(i).aid);
			} catch (Exception e) {
				fail = true;
				failMessage = "Error getting the votes for an alternative.";
			}
			try {
				choice.alternatives.get(i).feedback = dao.getAllFeedbackForAlt(choice.alternatives.get(i).aid);
			} catch (Exception e) {
				fail = true;
				failMessage = "Error getting the feedback for an alternative.";
			}
		}
		return choice;
	}
	
	@Override
	public ChoiceResponse handleRequest(SignInRequest req, Context context) {
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of SignInChoiceHandler");
		logger.log(req.toString());
		boolean fail = false;
		String failMessage = "";
		Choice choice = new Choice();
		try {
			choice = loadChoiceFromRDS(req.getChoiceID());
		} catch(Exception e){
			fail = true;
			failMessage = "This choice does not exist, it may have been deleted.";
		}
		boolean returning = false;
		boolean retGood = false;
		if(!fail) {
			try {
				choice.alternatives = dao.getAltsForChoice(choice.cid);
			} catch(Exception e){
				fail = true;
				failMessage = "Error getting alternatives for choice";
			}
		
			//Returning Member
			ArrayList<Member> members = new ArrayList<Member>();
			try {
				members = dao.getChoiceMembers(choice.cid);
			} catch (Exception e) {
				fail = true;
				failMessage = "Error getting the choice members.";
			}
			for(int m = 0; m < members.size(); m++) {
				if(members.get(m).name.equals(req.getUsername())) {
					returning = true;
					if(members.get(m).password.equals(req.getPassword())) {
						retGood = true;
					}
				}
			}
			//New Member
			int numMembers = -1;
			try {
				numMembers = dao.getNumMembers(choice.cid);
			} catch(Exception e) {
				e.printStackTrace();
				fail = true;
				failMessage = "Error getting number of members. numMembers=" + numMembers;
			}
			if(!returning) {
				if(numMembers < choice.numMembers) {
					boolean memberAdded = false;
					Member member = new Member(UUID.randomUUID().toString(), choice.cid, req.getUsername(), req.getPassword());
					logger.log("member: " + member.toString());
					try {
						memberAdded = dao.addMember(member, logger);
						if(!memberAdded) {fail = true;};
					} catch(Exception e) {
						fail = true;
						failMessage = "Error adding member";
					}
					choice.members.add(member);
				}else {
					fail = true;
					failMessage = "Choice has already reached its maximum number of members";
				}
			}
	
			if(!retGood && returning) {
				failMessage = "Password entered for member is incorrect";
			}
			else if(fail && failMessage.equals("")) {
				failMessage = "Choice has already reached its maximum number of members";
			}
			
			choice = getUpdatedChoice(choice.cid);
		}
		logger.log("fail: " + fail);
		logger.log("retGood: " + retGood);
		logger.log("returning: " + returning);
		// compute proper response and return. Note that the status code is internal to the HTTP response
		// and has to be processed specifically by the client code.
		ArrayList<Choice> choiceForResponse = new ArrayList<Choice>();
		choiceForResponse.add(choice);
		ChoiceResponse response;
		if (fail || (!retGood && returning)) {
			response = new ChoiceResponse(failMessage, 400, choiceForResponse);
		} else {
			response = new ChoiceResponse("none", 200, choiceForResponse);  // success
		}

		return response; 
	}
}