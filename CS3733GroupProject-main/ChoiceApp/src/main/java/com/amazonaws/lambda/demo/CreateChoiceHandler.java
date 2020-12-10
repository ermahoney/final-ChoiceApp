package com.amazonaws.lambda.demo;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.ChoiceCreationRequest;
import com.amazonaws.lambda.demo.http.ChoiceResponse;
import com.amazonaws.lambda.demo.model.Alternative;
import com.amazonaws.lambda.demo.model.Choice;


 //this is where we create a choice

public class CreateChoiceHandler implements RequestHandler<ChoiceCreationRequest, ChoiceResponse> {

	LambdaLogger logger;

	//this creates a choice
	//returns a boolean
	private Choice createChoice(String description, ArrayList<String> alternatives, int numMembers){
		if (logger != null) logger.log("in createChoice");
		String cid = UUID.randomUUID().toString();
		
		//Alternative[] alternatives = new Alternative[5];
		//int numAlt = alternativeTitles.length;
		long millis=System.currentTimeMillis();  
		Timestamp dateT =new Timestamp(millis); 
		String date = dateT.toString();
		Choice choice = new Choice(cid, description, numMembers, date);
		logger.log("numOfMembers in choice in createChoice: "+ choice.numMembers);
		
		for(int i = 0; i < alternatives.size(); i++) {
			if(!alternatives.get(i).equals("") || !alternatives.get(i).equals(null)) {
				String aid = UUID.randomUUID().toString();
				choice.addAlternative(new Alternative(aid, cid, alternatives.get(i)));
				logger.log("alternatives from paramter without empty in createChoice: "+ alternatives.get(i));
				logger.log("alternatives from choice object without empty in createChoice: "+ choice.alternatives.get(i));
			}
		}
		
		return choice;
	}

	//this handles request and catches errors when creating choice
	@Override
	public ChoiceResponse handleRequest(ChoiceCreationRequest req, Context context) {
		logger = context.getLogger();
		logger.log(req.toString());
		//making new ChoiceDAO
		ChoiceDAO dao = new ChoiceDAO();
		boolean fail = false;
		String failMessage = "";
		ArrayList<String> alts = req.getAlternatives();
		logger.log("alts in handler before parse:" + alts);
		logger.log("size of alts in handler before parse:" + alts.size());
		/*ArrayList<Integer> intsToRemove = new ArrayList<Integer>();
		for(int i = 0; i < 5; i++) {
			if(alts.get(i).equals("")) {
				intsToRemove.add(i);
			}
		}
		for(int i = 0; i < intsToRemove.size(); i++) {
			alts.remove((int)intsToRemove.get(0));
		}*/
		logger.log("alts in handler after parse:" + alts);
		//check that given 2-5 alternatives
		if(alts.size()<2 || alts.size()>5) {
			fail = true;
			failMessage = "Number of Alternatives is not between 2 and 5";
		}
		logger.log("numOfMembers from req: " + req.getNumOfMembers());
		Choice choice = createChoice(req.getDescription(), alts, req.getNumOfMembers());
		
		//this sees is choice is in the logger associated with DAO and adds the choice to DAO 
		//if choice already there in DAO then false as in could not add choice
		//if not there already then true as in successfully added
		//Choice choice = new Choice(title, description, alternatives, numMembers);
		if(logger!=null) logger.log("creating choice with addChoice");
		try {
			dao.addChoice(choice, logger);
		}
		catch(Exception e) {
			fail = true;
			failMessage = "Choice has already been created somehow";
		}
		ChoiceResponse response;
		ArrayList<Choice> choiceForResponse = new ArrayList<Choice>();
		choiceForResponse.add(choice);
		if (fail) {
			response = new ChoiceResponse(failMessage, 400, choiceForResponse);
		} else {
			response = new ChoiceResponse("none", 200, choiceForResponse);
		}

		return response;
	}

}

