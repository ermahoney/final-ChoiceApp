package com.amazonaws.lambda.demo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.AlternativeRequest;
import com.amazonaws.lambda.demo.http.ChoiceRequest;
import com.amazonaws.lambda.demo.http.ChoiceResponse;
import com.amazonaws.lambda.demo.http.FeedbackRequest;
import com.amazonaws.lambda.demo.model.Alternative;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.lambda.demo.model.Feedback;
import com.amazonaws.lambda.demo.model.Member;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;


public class ChooseAlternativeHandler implements RequestHandler<AlternativeRequest,ChoiceResponse> {

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
	public ChoiceResponse handleRequest(AlternativeRequest req, Context context) {
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of SignInChoiceHandler");
		logger.log(req.toString());
		
		Choice choice = new Choice();
		Alternative alt = null;
		try {
			alt = dao.getAlternative(req.getAid(), logger);
		} catch (Exception e) {
			fail = true;
			failMessage = "Error getting alterantive";
		}
		choice = getUpdatedChoice(alt.cid);
		if(choice.chosenAid == null) {
			choice.chosenAid = req.getAid();
			choice.dateOfCompletion = (new Timestamp(System.currentTimeMillis())).toString();
			boolean hasChosenAlt = false;
			try {
				hasChosenAlt = dao.hasChooseAlternative(choice.cid, req.getAid(), logger);
			} catch(Exception e) {
				fail = true;
				failMessage = "Error checking chosenAid in RDS";
			}
			if(hasChosenAlt) {
				fail = true;
				failMessage = "Error: Alternative has aleady been choosen for Choice";
			} else {
				try {
					dao.chooseAlternative(choice, req.getAid(), logger);
				} catch(Exception e) {
					fail = true;
					failMessage = "Error choosing alternative in RDS";
				}
			}
		}else {
			fail = true;
			failMessage = "Error: This Choice has been closed. Please re-login.";
		}
		
		logger.log("fail: " + fail);
		// compute proper response and return. Note that the status code is internal to the HTTP response
		// and has to be processed specifically by the client code.
		ArrayList<Choice> choiceForResponse = new ArrayList<Choice>();
		choiceForResponse.add(choice);
		ChoiceResponse response;
		if (fail) {
			response = new ChoiceResponse(failMessage, 400, choiceForResponse);
		} else {
			response = new ChoiceResponse("none", 200, choiceForResponse);  // success
		}

		return response; 
	}
}