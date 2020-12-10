package com.amazonaws.lambda.demo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.sql.Date;
import java.sql.Timestamp;

import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.ChoiceResponse;
import com.amazonaws.lambda.demo.http.DeleteChoiceRequest;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;


public class DeleteChoiceHandler implements RequestHandler<DeleteChoiceRequest,ChoiceResponse> {

	LambdaLogger logger;
	
	private AmazonS3 s3 = null;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	Choice loadChoiceFromRDS(String cid) throws Exception {
		if (logger != null) { logger.log("in loadValueFromRDS"); }
		ChoiceDAO dao = new ChoiceDAO();
		if (logger != null) { logger.log("retrieved DAO"); }
		Choice choice = dao.getChoice(cid, logger);
		if (logger != null) { logger.log("retrieved Choice"); }
		return choice;
	}
	
	//@Override
	public ChoiceResponse handleRequest(DeleteChoiceRequest req, Context context) {
		
		int nDays = -1 * (int)req.getNDays();
		double nHours = req.getNDays()%1.0;
		nHours = nHours*24;
		int hours = (int)nHours * -1;
		
		boolean fail = false;
		String failMessage = "";
		
		//if input of numDays is an int create a choice object
		ChoiceDAO dao = new ChoiceDAO();
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler of DeleteChoiceHandler");
		
		//getting the set of all choices
		ArrayList<Choice> allChoices = new ArrayList<Choice>();
		try {
			allChoices = dao.getAllChoices(logger);
		} catch(Exception e){
			fail = true;
			failMessage = "Error getting all choices.";
		}
		
		//figuring out what the date was n days ago
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
	    cal.add(Calendar.DAY_OF_MONTH, nDays);
	    cal.add(Calendar.HOUR, hours);
	    Timestamp dayNAgo = new Timestamp(cal.getTime().getTime());
		
//		LocalDate localDateNDayAgo = LocalDate.now().minusDays((long)nDays);
//		//Date javaSQLDateFromLocalDate = Date.valueOf(localDateNDayAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		Timestamp javaSQLDateFromLocalDate = Timestamp.valueOf(localDateNDayAgo);
		
		try {
			dao.deleteChoices(logger, nDays, dayNAgo);
		} catch (Exception e) {
			fail = true;
			failMessage = "Error deleting choices in RDS.";
			e.printStackTrace();
		}
		try {
			allChoices = dao.getAllChoices(logger);
		} catch(Exception e){
			fail = true;
			failMessage = "Error getting all choices.";
		}
		/*
		//checks if choice is as old or older than numDays and deletes if true
		for (int i = 0; i < allChoices.size(); i++) {
			if (allChoices.get(i).dateOfCreation.before(javaUtilDateFromLocalDate) || 
					allChoices.get(i).dateOfCreation.equals(javaUtilDateFromLocalDate)) {
				
				try {
					dao.deleteChoices(logger, nDays);
				} catch (Exception e) {
					fail = true;
					failMessage = "Error deleting choices in RDS.";
					e.printStackTrace();
				}
			}
		}*/
		
		//return all choices to show which ones are left?
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