package com.amazonaws.lambda.demo;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.lambda.demo.AlterVoteHandler;
import com.amazonaws.lambda.demo.ChooseAlternativeHandler;
import com.amazonaws.lambda.demo.CreateChoiceHandler;
import com.amazonaws.lambda.demo.DeleteChoiceHandler;
import com.amazonaws.lambda.demo.RefreshChoiceHandler;
import com.amazonaws.lambda.demo.ShowAllChoicesHandler;
import com.amazonaws.lambda.demo.SignInChoiceHandler;
import com.amazonaws.lambda.demo.SubmitFeedbackHandler;
import com.amazonaws.lambda.demo.db.ChoiceDAO;
import com.amazonaws.lambda.demo.http.AlternativeRequest;
import com.amazonaws.lambda.demo.http.ChoiceCreationRequest;
import com.amazonaws.lambda.demo.http.ChoiceRequest;
import com.amazonaws.lambda.demo.http.ChoiceResponse;

import com.amazonaws.lambda.demo.http.DeleteChoiceRequest;
import com.amazonaws.lambda.demo.http.FeedbackRequest;
import com.amazonaws.lambda.demo.http.SignInRequest;
import com.amazonaws.lambda.demo.http.VoteRequest;
import com.amazonaws.lambda.demo.model.Alternative;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;



/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class SafeTest {
	Choice choiceCH;
	
	
	boolean fail = false;
	String failMessage = "";
	
	Context createContext(String apiCall) {
        TestContext ctx = new TestContext();
        ctx.setFunctionName(apiCall);
        return ctx;
    }
	@Before
	public void createChoice() {
		CreateChoiceHandler cch = new CreateChoiceHandler();
    	ArrayList<String> alts = new ArrayList<String>();
    	for(int i = 1; i < 4; i++) {
    		alts.add("alt" + i);
    	}
    	//alts.add("");
    	//alts.add("");
    	System.out.println("alts in test:" + alts);
    	System.out.println("size of alts in test:" + alts.size());
    	ChoiceCreationRequest r = new ChoiceCreationRequest("Please Work", alts, 14);
    	ChoiceResponse response= cch.handleRequest(r, createContext("create quote"));
    	choiceCH = response.choice.get(0);
    	System.out.println("created" + choiceCH.cid);
	}
	
	@After
	public void deleteChoice() {
		ChoiceDAO dao = new ChoiceDAO();
		try {
			//dao.deleteChoice(choiceCH);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetAllChoices() {
		ChoiceDAO dao = new ChoiceDAO();
		try {
			LambdaLogger logger = createContext("").getLogger();
			ArrayList<Choice> list = dao.getAllChoices(logger);
			for (Choice c : list ) {
				System.out.println(c.cid);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}