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


public class ShowAllChoicesHandler implements RequestHandler<Object,ChoiceResponse> {

	LambdaLogger logger;
	
	private AmazonS3 s3 = null;

	
	@Override
	public ChoiceResponse handleRequest(Object input, Context context) {
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of ShowAllChoicesHandler");
		boolean fail = false;
		String failMessage = "";
		ArrayList<Choice> allChoices = new ArrayList<Choice>();
		try {
			allChoices = dao.getAllChoices(logger);
		} catch(Exception e){
			fail = true;
			failMessage = "Error getting all choices.";
		}
		
		// compute proper response and return. Note that the status code is internal to the HTTP response
		// and has to be processed specifically by the client code.
		ChoiceResponse response;
		if (fail) {
			response = new ChoiceResponse(failMessage, 400, allChoices);
		} else {
			response = new ChoiceResponse("none", 200, allChoices);  // success
		}

		return response; 
	}
}