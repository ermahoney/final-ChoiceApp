package com.amazonaws.lambda.demo;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.ChoiceRequest;
import com.amazonaws.lambda.demo.http.ChoiceResponse;
import com.amazonaws.lambda.demo.http.VoteRequest;
import com.amazonaws.lambda.demo.model.Alternative;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.lambda.demo.model.Member;
import com.amazonaws.lambda.demo.model.Vote;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;


public class AlterVoteHandler implements RequestHandler<VoteRequest,ChoiceResponse> {

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
	public ChoiceResponse handleRequest(VoteRequest req, Context context) {
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of SignInChoiceHandler");
		logger.log(req.toString());
		//boolean fail = false;
		//String failMessage = "";
		Alternative alt = null;
		try {
			alt = dao.getAlternative(req.getAlternative(), logger);
		} catch (Exception e) {
			fail = true;
			failMessage = "Error getting alternative";
		}
		
		Choice choice = new Choice();
		try {
			choice = loadChoiceFromRDS(alt.getCid());
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
		
		if(choice.chosenAid == null) {
			
			boolean haveApproved = false;
			boolean haveDisapproved = false;
			boolean hasVoted = true;
			
			//get mid
			String usernameMid = "";
			try {
				usernameMid = dao.getMid(req.getMember(), choice.cid);
			} catch(Exception e) {
				fail = true;
				failMessage = "Error getting mid for username";
			}
			logger.log("usernameMid: " + usernameMid);
			Vote vote = null;
			try {
				vote = dao.getVoteFromMember(req.getAlternative(), usernameMid, logger);
			} catch(Exception e) {
				//no vote for either
				hasVoted = false;
			}
			if(vote==null) {
				hasVoted = false;
			}
			if(!hasVoted) {
				//add approval or disapproval
				//create Vote
				Vote newVote = new Vote(UUID.randomUUID().toString(), usernameMid, req.getAlternative(), req.getKind(), req.getMember());
				//add Vote to RDS
				try {
					dao.addVote(newVote, logger);
				} catch(Exception e) {
					fail = true;
					failMessage = "Error adding vote to RDS";
				}
				//add Vote to votes in choice
				//choice.addVoteToAlternative(req.getAlternative(), newVote);
				choice = getUpdatedChoice(choice.cid);
				haveApproved = true;
			}
			//Request Approval
			if(req.getKind().equals("approval") && hasVoted) {
				logger.log("vote from system: " + vote);
				//logger.log("vote from system kind: " + vote.getKind());
				if(vote.getKind().equals("disapproval")) {
					//remove disapproval and add approval
					try {
						dao.removeVote(req.getAlternative(), usernameMid, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error removing vote";
					}
					//choice.removeVoteFromAlternative(req.getAlternative(), req.getMember());
					
					//create Vote
					Vote newVote = new Vote(UUID.randomUUID().toString(), usernameMid, req.getAlternative(), req.getKind(), req.getMember());
					//add Vote to RDS
					try {
						dao.addVote(newVote, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error adding vote to RDS";
					}
					//add Vote to votes in choice
					//choice.addVoteToAlternative(req.getAlternative(), newVote);
					choice = getUpdatedChoice(choice.cid);
					haveApproved = true;
					haveDisapproved = false;
				}
				else if(vote.getKind().equals("approval")) {
					//remove approval
					try {
						dao.removeVote(req.getAlternative(), usernameMid, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error removing vote";
					}
					//choice.removeVoteFromAlternative(req.getAlternative(), req.getMember());
					choice = getUpdatedChoice(choice.cid);
				}
			}
			
			//Request Disapproval
			if(req.getKind().equals("disapproval") && hasVoted) {
				//if approved, remove approval and add disapproval
				//else, add disapproval
				if(vote.getKind().equals("approval")) {
					//remove disapproval and add approval
					try {
						dao.removeVote(req.getAlternative(), usernameMid, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error removing vote";
					}
					//choice.removeVoteFromAlternative(req.getAlternative(), req.getMember());
					
					//create Vote
					Vote newVote = new Vote(UUID.randomUUID().toString(), usernameMid, req.getAlternative(), req.getKind(), req.getMember());
					//add Vote to RDS
					try {
						dao.addVote(newVote, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error adding vote to RDS";
					}
					//add Vote to votes in choice
					//choice.addVoteToAlternative(req.getAlternative(), newVote);
					choice = getUpdatedChoice(choice.cid);
					haveApproved = false;
					haveDisapproved = true;
				}
				//have already disapproved
				//remove disapproval
				else if(vote.getKind().equals("disapproval")) {
					//remove disapproval
					try {
						dao.removeVote(req.getAlternative(), usernameMid, logger);
					} catch(Exception e) {
						fail = true;
						failMessage = "Error removing vote";
					}
					//choice.removeVoteFromAlternative(req.getAlternative(), req.getMember());
					choice = getUpdatedChoice(choice.cid);
				}
			}
		} else {
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