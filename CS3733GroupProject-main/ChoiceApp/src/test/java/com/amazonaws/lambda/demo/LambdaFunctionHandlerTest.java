package com.amazonaws.lambda.demo;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
import com.amazonaws.lambda.demo.model.Feedback;
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
public class LambdaFunctionHandlerTest {
	
	Context context = createContext();
	LambdaLogger logger = context.getLogger();
	boolean fail = false;
	String failMessage = "";

    private final String CONTENT_TYPE = "image/jpeg";
    private S3Event event;
    
//    Choice getUpdatedChoice(String cid) {
//		ChoiceDAO dao = new ChoiceDAO();
//		Choice choice = null;
//		try {
//			choice = dao.getChoice(cid, logger);
//		} catch(Exception e){
//			fail = true;
//			failMessage = "Error getting choice.";
//		}
//		try {
//			choice.alternatives = dao.getAltsForChoice(choice.cid);
//		} catch(Exception e){
//			fail = true;
//			failMessage = "Error getting alternatives for choice";
//		}
//
//		try {
//			choice.members = dao.getChoiceMembers(choice.cid);
//		} catch (Exception e) {
//			fail = true;
//			failMessage = "Error getting the choice members.";
//		}
//		
//		for(int i = 0; i < choice.alternatives.size(); i++) {
//			try {
//				choice.alternatives.get(i).votes = dao.getVotesForAlt(choice.alternatives.get(i).aid);
//				System.out.println("choice in getUpdatedChoice right after votes size: "+ choice.alternatives.get(i).votes.size());
//			} catch (Exception e) {
//				fail = true;
//				failMessage = "Error getting the votes for an alternative.";
//			}
//			try {
//				choice.alternatives.get(i).feedback = dao.getAllFeedbackForAlt(choice.alternatives.get(i).aid);
//			} catch (Exception e) {
//				fail = true;
//				failMessage = "Error getting the feedback for an alternative.";
//			}
//		}
//		System.out.println("choice in getUpdatedChoice votes: "+ choice.votesToString(cid));
//		System.out.println("choice in getUpdatedChoice @ end votes size: "+ choice.alternatives.get(1).votes.size());
//		return choice;
//	}

    @Mock
    private AmazonS3 s3Client;
    @Mock
    private S3Object s3Object;

    @Captor
    private ArgumentCaptor<GetObjectRequest> getObjectRequest;

    @Before
    public void setUp() throws IOException {
        event = TestUtils.parse("/s3-event.put.json", S3Event.class);

        // TODO: customize your mock logic for s3 client
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(CONTENT_TYPE);
        when(s3Object.getObjectMetadata()).thenReturn(objectMetadata);
        when(s3Client.getObject(getObjectRequest.capture())).thenReturn(s3Object);
    }

    private Context createContext() {
        TestContext ctx = new TestContext();

        // TODO: customize your context here if needed.
        ctx.setFunctionName("Your Function Name");

        return ctx;
    }

    @Test
    public void testSignInChoiceHandler() {
    		/*{
    		  "choiceID": "123456789",
    		  "username": "Lauren",
    		  "password": "password"
    		}*/
    	SignInChoiceHandler sch = new SignInChoiceHandler();
    	SignInRequest r = new SignInRequest("f44f97bf-0f9e-424a-94d3-8b5c33271388", "Lauren3", "pass");
    	ChoiceResponse response= sch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    }
    
    @Test
    public void testSignInChoiceHandler2() {
    		/*{
    		  "choiceID": "bc8403d5-1cee-44af-a571-5800ad9339a8",
    		  "username": "fromJUnit",
    		  "password": ""
    		}*/
    	SignInChoiceHandler sch = new SignInChoiceHandler();
    	SignInRequest r = new SignInRequest("f44f97bf-0f9e-424a-94d3-8b5c33271388", "fromJUnit", "");
    	ChoiceResponse response= sch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	for(int i = 0; i < response.choice.get(0).alternatives.size(); i++) {
    		System.out.println("response.choice.get(0).alternatives.get("+i+"): "+response.choice.get(0).alternatives.get(i));
    	}
    }
    
    @Test
    public void testSignInChoiceHandler3() {
    		/*{
    		  "choiceID": "bc8403d5-1cee-44af-a571-5800ad9339a8",
    		  "username": "fromJUnit",
    		  "password": ""
    		}*/
    	SignInChoiceHandler sch = new SignInChoiceHandler();
    	SignInRequest r = new SignInRequest("91121446-2549-46b1-a547-73d16e54b287", "lauren", "");
    	ChoiceResponse response= sch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	for(int i = 0; i < response.choice.get(0).alternatives.size(); i++) {
    		System.out.println("response.choice.get(0).alternatives.get("+i+"): "+response.choice.get(0).alternatives.get(i));
    	}
    }
    
    @Test
    public void testSignInChoiceHandler4() {
    		/*{
    		  "choiceID": "bc8403d5-1cee-44af-a571-5800ad9339a8",
    		  "username": "fromJUnit",
    		  "password": ""
    		}*/
    	SignInChoiceHandler sch = new SignInChoiceHandler();
    	SignInRequest r = new SignInRequest("91121446-2549-46b1-a547-73d16e54b287", "other2", "");
    	ChoiceResponse response= sch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	for(int i = 0; i < response.choice.get(0).alternatives.size(); i++) {
    		System.out.println("response.choice.get(0).alternatives.get("+i+"): "+response.choice.get(0).alternatives.get(i));
    	}
    }
    
    @Test
    public void testCreateChoiceHandler1() {
    		/*{
				  "description": "Test description",
				  "alternatives": [
				    "alt1",
				    "alt2",
				    "alt3"
				  ],
				  "numOfMembers": 5
				}*/
    	CreateChoiceHandler cch = new CreateChoiceHandler();
    	ArrayList<String> alts = new ArrayList<String>();
    	for(int i = 1; i < 4; i++) {
    		alts.add("alt" + i);
    	}
    	//alts.add("");
    	//alts.add("");
    	System.out.println("alts in test:" + alts);
    	System.out.println("size of alts in test:" + alts.size());
    	ChoiceCreationRequest r = new ChoiceCreationRequest("Christmas2", alts, 14);
    	ChoiceResponse response= cch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice.get(0).alternatives.size(): "+ response.choice.get(0).alternatives.size());
    	for(int i = 0; i < response.choice.get(0).alternatives.size(); i++) {
    		System.out.println("response.choice.get(0).alternatives.get("+i+"): "+response.choice.get(0).alternatives.get(i));
    	}
    }
    
    @Test
    public void testCreateChoiceHandler2() {
    		/*{
				  "description": "Test description",
				  "alternatives": [
				    "alt1",
				    "alt2",
				    "alt3"
				  ],
				  "numOfMembers": 5
				}*/
    	CreateChoiceHandler cch = new CreateChoiceHandler();
    	ArrayList<String> alts = new ArrayList<String>();
    	alts.add("alt1");
    	for(int i = 0; i < 4; i++) {
    		alts.add("");
    	}
    	ChoiceCreationRequest r = new ChoiceCreationRequest("Favorite Holiday", alts, 6);
    	ChoiceResponse response= cch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    }
    
    @Test
    public void testCreateChoiceHandler3() {
    		/*{
				  "description": "Test description",
				  "alternatives": [
				    "alt1",
				    "alt2",
				    "alt3"
				  ],
				  "numOfMembers": 5
				}*/
    	CreateChoiceHandler cch = new CreateChoiceHandler();
    	ArrayList<String> alts = new ArrayList<String>();
    	for(int i = 1; i < 8; i++) {
    		alts.add("alt" + i);
    	}
    	ChoiceCreationRequest r = new ChoiceCreationRequest("Example Choice description", alts, 6);
    	ChoiceResponse response= cch.handleRequest(r, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    }
    
    @Test
    public void testShowAllChoices() {
    	ShowAllChoicesHandler sach = new ShowAllChoicesHandler();
    	Object input = "input";
    	ChoiceResponse response= sach.handleRequest(input, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice.get(0).dateOfCreation: "+ response.choice.get(0).dateOfCreation);
    }
    
    @Test
    public void testRefreshChoice() {
    	RefreshChoiceHandler sach = new RefreshChoiceHandler();
    	//get choice
    	//ChoiceDAO dao = new ChoiceDAO();
    	//Choice ogChoice = null;
    	System.out.println("ogChoice using getUpdatedChoice");
    	//ogChoice = getUpdatedChoice("9f1e60e2-790c-4b79-9d6f-ce887e028f3e");
    	//change choice
    	//AlterVoteHandler avh = new AlterVoteHandler();
    	//VoteRequest vr = new VoteRequest("9adee09b-f5c1-4823-8767-e047af0a46f4", "042a693e-6868-4496-bca8-15d9855d29a6", "disapproval");
    	//ChoiceResponse response= avh.handleRequest(vr, createContext());
    	ChoiceRequest cr = new ChoiceRequest("2231ed4d-0868-41c5-86f8-1120ff3b4d45");
    	//check that old choice and new choice are NOT the same
    	ChoiceResponse response2= sach.handleRequest(cr, createContext());
    	Choice newChoice = response2.getChoice().get(0);
    	System.out.println("response2.statusCode: " + response2.statusCode);
    	System.out.println("response2.error: " + response2.error);
    	System.out.println("response2.choice: " + response2.choice);
    	//System.out.println("ogChoice votes size: "+ ogChoice.alternatives.get(1).votes.size());
    	System.out.println("newChoice alt 0 votes size: "+ newChoice.alternatives.get(0).votes.size());
    	System.out.println("newChoice alt 0 feedback size: "+ newChoice.alternatives.get(0).feedback.size());
    	System.out.println("newChoice alt 1 votes size: "+ newChoice.alternatives.get(1).votes.size());
    	System.out.println("newChoice alt 1 feedback size: "+ newChoice.alternatives.get(1).feedback.size());
    	for(int m= 0; m < newChoice.members.size();m++) {
    		System.out.println("Member "+m+": "+newChoice.members.get(m));
    	}
    	for(int a= 0; a < newChoice.alternatives.size();a++) {
    		System.out.println("Alternative "+a+": "+newChoice.alternatives.get(a));
    		for(int v = 0; v < newChoice.alternatives.get(a).votes.size();v++) {
    			System.out.println("Vote "+v+": "+newChoice.alternatives.get(a).votes.get(v));
    		}
    		for(int f = 0; f < newChoice.alternatives.get(a).feedback.size();f++) {
    			System.out.println("Feedback "+f+": "+newChoice.alternatives.get(a).feedback.get(f));
    		}
    	}
    }
    
    @Test
    public void testAlterVoteA1() {
    	AlterVoteHandler avh = new AlterVoteHandler();
    	//What do you want to eat for dinner? Rachel
    	String exMid = "";
    	//"fe4fc0e9-15ba-4dcc-a4d3-cef7bc814c7b"
    	VoteRequest vr = new VoteRequest("4604ac79-3644-4c06-912b-bf8eeeffa4e8", "lauren", "approval");
    	//check that old choice and new choice are NOT the same
    	ChoiceResponse response= avh.handleRequest(vr, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice votes: "+ response.choice.get(0).votesToString("73fc850a-dc94-48e0-a0d9-fe10e24b76d9"));
    	System.out.println("response.choice alt @ 1 aid: "+ response.choice.get(0).alternatives.get(1).aid);
    	System.out.println("response.choice votes size: "+ response.choice.get(0).alternatives.get(1).votes.size());
    	System.out.println("response.choice.chosenAid: "+ response.choice.get(0).chosenAid);
    }
    
    @Test
    public void testAlterVoteA2() {
    	AlterVoteHandler avh = new AlterVoteHandler();
    	//What do you want to eat for dinner? Rachel
    	VoteRequest vr = new VoteRequest("4604ac79-3644-4c06-912b-bf8eeeffa4e8", "other", "approval");
    	//check that old choice and new choice are NOT the same
    	ChoiceResponse response= avh.handleRequest(vr, createContext());
    	VoteRequest vr2 = new VoteRequest("4604ac79-3644-4c06-912b-bf8eeeffa4e8", "other", "disapproval");
    	ChoiceResponse response2= avh.handleRequest(vr2, createContext());
    	VoteRequest vr3 = new VoteRequest("4604ac79-3644-4c06-912b-bf8eeeffa4e8", "other", "disapproval");
    	ChoiceResponse response3= avh.handleRequest(vr3, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice votes: "+ response.choice.get(0).votesToString("73fc850a-dc94-48e0-a0d9-fe10e24b76d9"));
    	System.out.println("response.choice alt @ 1 aid: "+ response.choice.get(0).alternatives.get(1).aid);
    	System.out.println("response.choice votes size: "+ response.choice.get(0).alternatives.get(1).votes.size());
    }
    
    @Test
    public void testAlterVoteD2() {
    	AlterVoteHandler avh = new AlterVoteHandler();
    	//What do you want to eat for dinner? Rachel
    	VoteRequest vr = new VoteRequest("c8fde476-8a96-4bb2-92ee-490648522410", "other2", "disapproval");
    	//check that old choice and new choice are NOT the same
    	ChoiceResponse response= avh.handleRequest(vr, createContext());
    	VoteRequest vr2 = new VoteRequest("c8fde476-8a96-4bb2-92ee-490648522410", "other2", "approval");
    	ChoiceResponse response2= avh.handleRequest(vr2, createContext());
    	VoteRequest vr3 = new VoteRequest("c8fde476-8a96-4bb2-92ee-490648522410", "other2", "approval");
    	ChoiceResponse response3= avh.handleRequest(vr3, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice votes: "+ response.choice.get(0).votesToString("73fc850a-dc94-48e0-a0d9-fe10e24b76d9"));
    	System.out.println("response.choice alt @ 1 aid: "+ response.choice.get(0).alternatives.get(1).aid);
    	System.out.println("response.choice votes size: "+ response.choice.get(0).alternatives.get(1).votes.size());
    }
    
    @Test
    public void testAlterVote3() {
    	//{"alternative":"2d343362-442e-428d-bdd7-ff2a8ede5111","member":"jenna","kind":"approval"}
    	AlterVoteHandler avh = new AlterVoteHandler();
    	VoteRequest vr = new VoteRequest("cd1d1c03-9e6f-4c74-81cd-e7151eeb85bc", "lauren", "disapproval");
    	ChoiceResponse response= avh.handleRequest(vr, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice votes: "+ response.choice.get(0).votesToString("73fc850a-dc94-48e0-a0d9-fe10e24b76d9"));
    	System.out.println("response.choice alt @ 1 aid: "+ response.choice.get(0).alternatives.get(1).aid);
    	System.out.println("response.choice votes size: "+ response.choice.get(0).alternatives.get(1).votes.size());
    	System.out.println("response.choice member: " + response.choice.get(0).members.get(0));
    }
    
    @Test
    public void testAlterVote4() {
    	//{"alternative":"2d343362-442e-428d-bdd7-ff2a8ede5111","member":"jenna","kind":"approval"}
    	AlterVoteHandler avh = new AlterVoteHandler();
    	VoteRequest vr = new VoteRequest("dd9e05a7-4a99-489d-8b9a-f227dac0de48", "lauren", "approval");
    	ChoiceResponse response= avh.handleRequest(vr, createContext());
    	VoteRequest vr2 = new VoteRequest("dd9e05a7-4a99-489d-8b9a-f227dac0de48", "lauren", "disapproval");
    	ChoiceResponse response2= avh.handleRequest(vr2, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice votes: "+ response.choice.get(0).votesToString("73fc850a-dc94-48e0-a0d9-fe10e24b76d9"));
    	System.out.println("response.choice alt @ 1 aid: "+ response.choice.get(0).alternatives.get(1).aid);
    	System.out.println("response.choice votes size: "+ response.choice.get(0).alternatives.get(1).votes.size());
    	System.out.println("response.choice member: " + response.choice.get(0).members.get(0));
    }
    
    @Test
    public void testSubmitFeedback() {
    	SubmitFeedbackHandler sfh = new SubmitFeedbackHandler();
    	String exDate = "2020-12-01";
    	//new Date(System.currentTimeMillis()).toString()
    	//mid: "4e53bf0e-c4e9-4009-b67d-f62ce3878302"
		String exMid = "";
    	FeedbackRequest fr = new FeedbackRequest("dd9e05a7-4a99-489d-8b9a-f227dac0de48", "Awesome!", "lauren", new Timestamp(System.currentTimeMillis()).toString());
    	ChoiceResponse response= sfh.handleRequest(fr, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	//System.out.println("response.choice feedback: " + response.choice.get(0).alternatives.get(2).feedback.get(3));
    	System.out.println("response.choice member: " + response.choice.get(0).members.get(0));
    	for(Alternative alt: response.choice.get(0).alternatives) {
    		if(alt.feedback != null) {
	    		for(Feedback fed: alt.feedback) {
	    			if(fed!=null) {
	    				System.out.println("Feedback: "+fed+" \n\tfor Alternative: "+alt);
	    			}
	    		}
    		}
    	}
    	//System.out.println("response.choice member: " + response.choice.get(0).alternatives.get(0).feedback.get(0).username);
    }
    
    @Test
    public void testChooseAlternative() {
    	ChooseAlternativeHandler cah = new ChooseAlternativeHandler();
    	AlternativeRequest ar = new AlternativeRequest("473518c6-d39f-489d-9572-7b30c93c2605");
    	ChoiceResponse response= cah.handleRequest(ar, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	System.out.println("response.choice chosenAid: " + response.choice.get(0).chosenAid);
    	System.out.println("response.choice dateOfCompletion: " + response.choice.get(0).dateOfCompletion);
    }
	
	@Test
    public void testDeleteChoice() {
    	DeleteChoiceHandler dch = new DeleteChoiceHandler();
    	DeleteChoiceRequest dcr = new DeleteChoiceRequest(0.5);
    	ChoiceResponse response= dch.handleRequest(dcr, createContext());
    	System.out.println("response.statusCode: " + response.statusCode);
    	System.out.println("response.error: " + response.error);
    	System.out.println("response.choice: " + response.choice);
    	
    	
    }
}
