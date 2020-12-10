package com.amazonaws.lambda.demo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
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


public class SubmitFeedbackHandler implements RequestHandler<FeedbackRequest,ChoiceResponse> {

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
	
	Choice getUpdatedChoice(String cid,LambdaLogger logger) {
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
		logger.log(""+choice.alternatives.size());
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
	public ChoiceResponse handleRequest(FeedbackRequest req, Context context) {
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of SignInChoiceHandler");
		logger.log(req.toString());
		
		Choice choice = new Choice();
		Alternative alt = null;
		try {
			alt = dao.getAlternative(req.getAlternative(), logger);
		} catch (Exception e) {
			fail = true;
			failMessage = "Error getting alterantive";
		}
		logger.log("alt: "+alt);
		logger.log("fail: "+fail);
		choice = getUpdatedChoice(alt.cid, logger);
		if(choice.chosenAid == null) {
			//get mid
			String usernameMid = "";
			try {
				usernameMid = dao.getMid(req.getMember(), choice.cid);
			} catch(Exception e) {
				fail = true;
				failMessage = "Error getting mid for username";
			}
			logger.log("usernameMid: " + usernameMid);
			//create Feedback model object
			Feedback feedback = new Feedback(UUID.randomUUID().toString(), usernameMid, req.getAlternative(), req.getContent(), Timestamp.valueOf(req.getTimestamp()), req.getMember());
			logger.log("feedback.username: " + feedback.username);
			logger.log("feedback.mid: " + feedback.mid);
			logger.log("feedback.aid: " + feedback.aid);
			//add feedback to RDS
			try {
				dao.addFeedback(feedback, logger);
			} catch(Exception e) {
				fail = true;
				failMessage = "Error adding feedback to RDS";
			}
			//add feedback model object to choice.alternatives.get(alt with aid).feedback list
			//or get updated choice object
			choice = getUpdatedChoice(choice.cid, logger);
//			for(int a = 0; a < choice.alternatives.size(); a++) {
//				for(int f = 0; f < choice.alternatives.get(a).feedback.size(); f++) {
//					choice.alternatives.get(a).feedback.get(f).timestamp = choice.alternatives.get(a).feedback.get(f).timestamp.toString();
//				}
//			}
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