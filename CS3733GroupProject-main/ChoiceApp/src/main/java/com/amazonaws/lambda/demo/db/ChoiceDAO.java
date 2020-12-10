package com.amazonaws.lambda.demo.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.demo.model.Alternative;
import com.amazonaws.lambda.demo.model.Choice;
import com.amazonaws.lambda.demo.model.Feedback;
import com.amazonaws.lambda.demo.model.Member;
import com.amazonaws.lambda.demo.model.Vote;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ChoiceDAO { 

	java.sql.Connection conn;
	

	final String tblName = "Choice";   // Exact capitalization

    public ChoiceDAO() {

    	try  {
    		conn = DatabaseUtil.connect();
    	} catch (Exception e) {
    		conn = null;
    	}
    }

    public Choice getChoice(String cid, LambdaLogger logger) throws Exception {
        
        try {
        	{ logger.log("in getChoice"); }
            Choice choice = null;
            { logger.log("choice = null"); }
            { logger.log("con= " + conn); }
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tblName + " WHERE cid=?;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                choice = generateChoice(resultSet, logger);
                { logger.log("choice (in getChoice): " + choice.toString()); }
            }
            resultSet.close();
            ps.close();
            { logger.log("choice after while loop (in getChoice): " + choice.toString()); }
            return choice;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting choice: " + e.getMessage());
        }
    }
    
    public Alternative getAlternative(String aid, LambdaLogger logger) throws Exception {
        
        try {
        	{ logger.log("in getAlternative"); }
            Alternative alt = null;
            { logger.log("alt = null"); }
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Alternative WHERE aid=?;");
            ps.setString(1, aid);
            logger.log("Before query");
            ResultSet resultSet = ps.executeQuery();
            logger.log("After query, before while");
            while (resultSet.next()) {
            	{ logger.log("(in getAlternative) before generate: "); }
                alt = generateAlternative(resultSet);
                { logger.log("alt (in getAlternative): " + alt.toString()); }
            }
            resultSet.close();
            ps.close();
            return alt;

        } catch (Exception e) {
        	e.printStackTrace();
        	logger.log("In exception");
        	//{ logger.log("e (in getAlternative): " + e.getMessage()); }
            throw new Exception("Failed in getting alternative: " + e.getMessage());
        }
    }
    
    public Vote getVoteFromMember(String aid, String mid, LambdaLogger logger) throws Exception {
        
        try {
        	{ logger.log("in getVoteFromMember"); }
            Vote vote = null;
            { logger.log("vote = null"); }
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vote WHERE (aid=? and mid=?);");
            ps.setString(1, aid);
            ps.setString(2, mid);
            ResultSet resultSet = ps.executeQuery();
            
            while (resultSet.next()) {
                vote = generateVote(resultSet);
                { logger.log("vote (in getVoteFromMember): " + vote.toString()); }
            }
            resultSet.close();
            ps.close();
            return vote;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting vote from member: " + e.getMessage());
        }
    }
    
    public int getNumMembers(String cid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(mid) as count FROM Member where Member.cid=?;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();
            int count = -2;
            while (resultSet.next()) {
                count = resultSet.getInt("count");
            }
            resultSet.close();
            ps.close();
            
            return count;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting count of members: " + e.getMessage());
        }
    }
    
    public ArrayList<Alternative> getAltsForChoice(String cid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Alternative where cid=? order by altOrder;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Alternative> alts = new ArrayList<Alternative>();
            while (resultSet.next()) {
            	alts.add(generateAlternative(resultSet));
            }
            resultSet.close();
            ps.close();
            
            return alts;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting alternatives for choice: " + e.getMessage());
        }
    }
    
    public ArrayList<Member> getMembersForChoice(String cid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Member where cid=?;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Member> members = new ArrayList<Member>();
            while (resultSet.next()) {
            	members.add(generateMember(resultSet));
            }
            resultSet.close();
            ps.close();
            
            return members;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting alternatives for choice: " + e.getMessage());
        }
    }
    
    public ArrayList<Vote> getVotesForAlt(String aid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vote where aid=?;");
            ps.setString(1, aid);
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Vote> votes = new ArrayList<Vote>();
            while (resultSet.next()) {
            	votes.add(generateVote(resultSet));
            }
            resultSet.close();
            ps.close();
            
            return votes;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting votes for an alternative: " + e.getMessage());
        }
    }
    
    public ArrayList<Feedback> getAllFeedbackForAlt(String aid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Feedback where aid=? order by Feedback.timestamp;");
            ps.setString(1, aid);
            ResultSet resultSet = ps.executeQuery();
            ArrayList<Feedback> fs = new ArrayList<Feedback>();
            while (resultSet.next()) {
            	fs.add(generateFeedback(resultSet));
            }
            resultSet.close();
            ps.close();
            
            return fs;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting votes for an alternative: " + e.getMessage());
        }
    }
    
    public String getMid(String username, String cid) throws Exception {
        
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT mid FROM Member where (cid=? and name=?) ;");
            ps.setString(1, cid);
            ps.setString(2, username);
            ResultSet resultSet = ps.executeQuery();
            String mid = "";
            while (resultSet.next()) {
            	mid = resultSet.getString("mid");
            }
            resultSet.close();
            ps.close();
            
            return mid;

        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Failed in getting mid from username and cid: " + e.getMessage());
        }
    }
    
    public boolean updateChoice(Choice choice, String col, Object value) throws Exception {
        try {
        	String query = "UPDATE Choice SET ?=? WHERE cid=?;";
        	PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, col);
            ps.setObject(2, value);
            ps.setString(3, choice.cid);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);
        } catch (Exception e) {
            throw new Exception("Failed to update report: " + e.getMessage());
        }
    }
	
    public boolean deleteChoices(LambdaLogger logger, double numDays, Timestamp dateNDaysAgo) throws Exception {
        try {
        	{ logger.log("(in deleteChoices): "); }
        	{ logger.log("(in deleteChoices) dateNDaysAgo: "+dateNDaysAgo); }
        	//SELECT * FROM Choice WHERE dateOfCreation >= dateNDaysAgo
        	PreparedStatement ps = conn.prepareStatement("SELECT * FROM Choice WHERE dateOfCreation <= ?;");
            ps.setTimestamp(1, dateNDaysAgo); //given int that admin puts in for how many days old deleting should start at
            ResultSet resultSet = ps.executeQuery();
            Choice choice = null;
            while (resultSet.next()) {
            	choice = generateChoice(resultSet, logger);
            	choice.alternatives = getAltsForChoice(choice.cid);
            	choice.members = getMembersForChoice(choice.cid);
            	for(int i = 0; i < choice.alternatives.size();i++) {
            		choice.alternatives.get(i).feedback = getAllFeedbackForAlt(choice.alternatives.get(i).aid);
            		choice.alternatives.get(i).votes = getVotesForAlt(choice.alternatives.get(i).aid);
            	}
            	deleteChoice(choice, logger);
            }
            resultSet.close();
            ps.close();
            
            return true;
        	
        	//PreparedStatement ps = conn.prepareStatement("DELETE FROM Choice WHERE cid = ?;");
//            PreparedStatement ps = conn.prepareStatement("DELETE FROM Choice WHERE (CURRENT_TIMESTAMP() - dateOfCreation) > INTERVAL ? DAYS;");
//
//            ps.setDouble(1, numDays); //given int that admin puts in for how many days old deleting should start at
//            
//            ps.executeUpdate();
//            ps.close();
//            return true;

        } catch (Exception e) {
            throw new Exception("Failed to delete choices: " + e.getMessage());
        }
    }
    
    public boolean deleteChoice(Choice choice, LambdaLogger logger) throws Exception {
        try {
        	//call deleteAlternatives(Choice choice) here
        	deleteFeedback(choice, logger);
        	deleteVotes(choice, logger);
        	deleteAlternatives(choice, logger);
        	deleteMembers(choice, logger);
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Choice WHERE cid = ?;");
            ps.setString(1, choice.cid);
            int numAffected = ps.executeUpdate();
            ps.close();
            
            return (numAffected == 1);

        } catch (Exception e) {
            throw new Exception("Failed to delete choice: " + e.getMessage());
        }
    }
    
    public boolean deleteFeedback(Choice choice, LambdaLogger logger) throws Exception {
        try {
        	PreparedStatement ps;
        	int numAffected = -1;
        	//call deleteAlternatives(Choice choice) here
        	for(int i = 0; i < choice.alternatives.size(); i++) {
        		for(int f = 0; f < choice.alternatives.get(i).feedback.size(); f++) {
        			{ logger.log("(in deleteFeedback) fid: "+choice.alternatives.get(i).feedback.get(f).fid); }
        			ps = conn.prepareStatement("DELETE FROM Feedback WHERE fid = ?;");
                    ps.setString(1, choice.alternatives.get(i).feedback.get(f).fid);
                    numAffected = ps.executeUpdate();
                    ps.close();
        		}
        		return (numAffected == choice.alternatives.get(i).feedback.size());
        	}
           return true;

        } catch (Exception e) {
            throw new Exception("Failed to delete feedback: " + e.getMessage());
        }
    }
    
    public boolean deleteVotes(Choice choice, LambdaLogger logger) throws Exception {
        try {
        	PreparedStatement ps;
        	int numAffected = -1;
        	//call deleteAlternatives(Choice choice) here
        	for(int i = 0; i < choice.alternatives.size(); i++) {
        		for(int f = 0; f < choice.alternatives.get(i).votes.size(); f++) {
        			{ logger.log("(in deleteVotes) vid: "+choice.alternatives.get(i).votes.get(f).vid); }
        			ps = conn.prepareStatement("DELETE FROM Vote WHERE vid = ?;");
                    ps.setString(1, choice.alternatives.get(i).votes.get(f).vid);
                    numAffected = ps.executeUpdate();
                    ps.close();
        		}
        		return (numAffected == choice.alternatives.get(i).votes.size());
        	}
           return true;

        } catch (Exception e) {
            throw new Exception("Failed to delete votes: " + e.getMessage());
        }
    }
    
    public boolean deleteAlternatives(Choice choice, LambdaLogger logger) throws Exception {
        try {
        	PreparedStatement ps;
        	int numAffected = -1;
        	//call deleteAlternatives(Choice choice) here
        	for(int i = 0; i < choice.alternatives.size(); i++) {
        		{ logger.log("(in deleteAlternatives) aid: "+choice.alternatives.get(i).aid); }
        		ps = conn.prepareStatement("DELETE FROM Alternative WHERE aid = ?;");
                ps.setString(1, choice.alternatives.get(i).aid);
                numAffected = ps.executeUpdate();
                ps.close();
        	}
            
            return (numAffected == choice.alternatives.size());

        } catch (Exception e) {
            throw new Exception("Failed to delete alternatives: " + e.getMessage());
        }
    }
    
    public boolean deleteMembers(Choice choice, LambdaLogger logger) throws Exception {
        try {
        	PreparedStatement ps;
        	int numAffected = -1;
        	//call deleteAlternatives(Choice choice) here
        	for(int i = 0; i < choice.members.size(); i++) {
        		{ logger.log("(in deleteMembers) mid: "+choice.members.get(i).mid); }
        		ps = conn.prepareStatement("DELETE FROM Member WHERE mid = ?;");
                ps.setString(1, choice.members.get(i).mid);
                numAffected = ps.executeUpdate();
                ps.close();
        	}
            
            return (numAffected == choice.members.size());

        } catch (Exception e) {
            throw new Exception("Failed to delete members: " + e.getMessage());
        }
    }

    public boolean addChoice(Choice choice, LambdaLogger logger) throws Exception { //Create Choice
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Choice WHERE cid = ?;");
            ps.setString(1, choice.cid);
            ResultSet resultSet = ps.executeQuery();
            { logger.log("ps for select (in addChoice): " + ps); }
            // already present?
            while (resultSet.next()) {
                Choice c = generateChoice(resultSet, logger);
                resultSet.close();
                return false;
            }
            logger.log("choice.numMembers in addChoice before insert: " + choice.numMembers);
            ps = conn.prepareStatement("INSERT INTO Choice (cid, chosenAid, description, numMembers, dateOfCreation, dateOfCompletion) values(?,?,?,?,?,?);");
            ps.setString(1, choice.cid);
            ps.setString(2, choice.chosenAid);
            ps.setString(3, choice.description);
            ps.setInt(4, choice.numMembers);
            ps.setTimestamp(5, Timestamp.valueOf(choice.dateOfCreation));
            if(choice.dateOfCompletion != null) {
            	ps.setTimestamp(6, Timestamp.valueOf(choice.dateOfCompletion));
            } else {
            	ps.setTimestamp(6, null);
            }
            { logger.log("ps for insert (in addChoice): " + ps); }
            ps.execute();
            boolean test;
            int index = 1;
            for(Alternative alt: choice.alternatives) {
            	{logger.log(""+alt.cid+ " " + alt.aid);}
            	test = addAlternative(alt, index, logger);
            	index++;
            	if(!test) {return false;}
            }
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert choice: " + e.getMessage());
        }
    }
    
    public boolean addAlternative(Alternative alt, int index, LambdaLogger logger) throws Exception { //Create Alternative
        try {
            /*PreparedStatement ps = conn.prepareStatement("SELECT * FROM Alternative WHERE aid = ?;");
            ps.setString(1, alt.aid);
            ResultSet resultSet = ps.executeQuery();
            { logger.log("ps for select (in addAlternative): " + ps); }
            // already present?
            while (resultSet.next()) {
                Alternative a = generateAlternative(resultSet);
                resultSet.close();
                { logger.log("select (in addAlternative) result: " + ps); }
                return false;
            }*/
        	{ logger.log("(in addAlternative) : "); 
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Alternative (aid, cid, description, altOrder) values (?,?,?,?);");
            { logger.log("(in addAlternative)alt.aid: " + alt.aid); }
            { logger.log("(in addAlternative)alt.cid: " + alt.cid); }
            { logger.log("(in addAlternative)alt.des: " + alt.description); }
            { logger.log("(in addAlternative)alt.index: " + index); }
            ps.setString(1, alt.aid);
            ps.setString(2, alt.cid);
            ps.setString(3, alt.description);
            { logger.log("(in addAlternative)alt.aid: " + alt.aid); }
            ps.setInt(4, index);
            { logger.log("(in addAlternative)alt.aid: " + alt.aid); }
            ps.execute();
            { logger.log("ps for insert (in addAlternative): " + ps); }
            return true;

        }} catch (Exception e) {
        	 { logger.log("(in addAlternative) e: "+e.getMessage()); }
            throw new Exception("Failed to insert alternative: " + e.getMessage());
        }
    }
    
    public boolean addMember(Member member, LambdaLogger logger)throws Exception { //Create Member
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Member WHERE mid = ?;");
            ps.setString(1, member.mid);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                Member m = generateMember(resultSet);
                resultSet.close();
                return false;
            }
            { logger.log("(in addMember): "); }
            ps = conn.prepareStatement("INSERT INTO Member (mid, cid, name, password) values(?,?,?,?);");
            ps.setString(1, member.mid);
            ps.setString(2, member.cid);
            ps.setString(3, member.name);
            ps.setString(4, member.password);
            { logger.log("ps for insert (in addMember): " + ps); }
            ps.execute();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert member: " + e.getMessage());
        }
    }
    
    public boolean addVote(Vote vote, LambdaLogger logger)throws Exception { //Create Vote
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Vote WHERE vid = ?;");
            ps.setString(1, vote.vid);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                Vote m = generateVote(resultSet);
                resultSet.close();
                return false;
            }
            { logger.log("(in addVote): "); }
            ps = conn.prepareStatement("INSERT INTO Vote (vid, mid, username, aid, kind) values(?,?,?,?,?);");
            ps.setString(1, vote.vid);
            ps.setString(2, vote.mid);
            ps.setString(3, vote.username);
            ps.setString(4, vote.aid);
            ps.setString(5, vote.kind);
            { logger.log("ps for insert (in addVote): " + ps); }
            ps.execute();
            return true;

        } catch (Exception e) {
        	{ logger.log("e (in addVote): " + e.getMessage()); }
            throw new Exception("Failed to insert vote: " + e.getMessage());
        }
    }
    
    public boolean addFeedback(Feedback feedback, LambdaLogger logger)throws Exception { //Create Feedback
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM Feedback WHERE fid = ?;");
            ps.setString(1, feedback.fid);
            ResultSet resultSet = ps.executeQuery();
            
            // already present?
            while (resultSet.next()) {
                Feedback m = generateFeedback(resultSet);
                resultSet.close();
                return false;
            }
            { logger.log("(in addFeedback): "); }
            ps = conn.prepareStatement("INSERT INTO Feedback (fid, timestamp, content, mid, username, aid) values(?,?,?,?,?,?);");
            ps.setString(1, feedback.fid);
            ps.setTimestamp(2, feedback.timestamp);
            ps.setString(3, feedback.content);
            ps.setString(4, feedback.mid);
            ps.setString(5, feedback.username);
            ps.setString(6, feedback.aid);
            { logger.log("ps for insert (in addFeedback): " + ps); }
            ps.execute();
            return true;

        } catch (Exception e) {
            throw new Exception("Failed to insert feedback: " + e.getMessage());
        }
    }
    
    public boolean removeVote(String aid, String mid, LambdaLogger logger)throws Exception { //Remove Vote
        try {
            { logger.log("(in removeVote): "); }
            PreparedStatement ps = conn.prepareStatement("Delete from Vote where (aid=? and mid=?);");
            ps.setString(1, aid);
            ps.setString(2, mid);
            { logger.log("ps for delete (in removeVote): " + ps); }
            ps.execute();
            return true;
        } catch (Exception e) {
            throw new Exception("Failed to insert vote: " + e.getMessage());
        }
    }
    
    public boolean chooseAlternative(Choice choice, String aid, LambdaLogger logger)throws Exception { //update chosenAid
        try {
        	{ logger.log("(in chooseAlternative): "); }
        	PreparedStatement ps = conn.prepareStatement("Update Choice set chosenAid=? where cid=?;");
            ps.setString(1, aid);
            ps.setNString(2, choice.cid);
            { logger.log("ps for update (in chooseAlternative): " + ps); }
            ps.execute();
            
            ps = conn.prepareStatement("Update Choice set dateOfCompletion=? where cid=?;");
            ps.setTimestamp(1, Timestamp.valueOf(choice.dateOfCompletion));
            ps.setNString(2, choice.cid);
            { logger.log("ps for update 2(in chooseAlternative): " + ps); }
            ps.execute();
            
            return true;
        } catch (Exception e) {
            throw new Exception("Failed to update chosenAid: " + e.getMessage());
        }
    }
    
    public boolean hasChooseAlternative(String cid, String aid, LambdaLogger logger)throws Exception { //update chosenAid
        try {
        	{ logger.log("(in chooseAlternative1): "); }
        	PreparedStatement ps = conn.prepareStatement("SELECT * FROM Choice WHERE cid = ?;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();
            { logger.log("ps for select: " + ps); }
            // already present?
            String ca = "";
            Choice c = null;
            while (resultSet.next()) {
            	ca = resultSet.getString("chosenAid");
            	resultSet.close();
            	{ logger.log("chosenAid from select: " + ca); }
            	{ logger.log("ca != null: " + (ca != null)); }
                if(ca == null) {
                	{ logger.log("in if"); }
                	return false;
                }else {
                	return true;
                }
            }
            return true;
        	
        } catch (Exception e) {
            throw new Exception("Failed to check chosenAid: " + e.getMessage());
        }
    }

    public ArrayList<Choice> getAllChoices(LambdaLogger logger) throws Exception {
        
        ArrayList<Choice> allChoices = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            String query = "SELECT * FROM Choice;";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Choice c = generateChoice(resultSet, logger);
                { logger.log("after generateChoice"); }
                allChoices.add(c);
            }
            resultSet.close();
            statement.close();
            return allChoices;

        } catch (Exception e) {
            throw new Exception("Failed in getting choices: " + e.getMessage());
        }
    }
    
    public ArrayList<Member> getChoiceMembers(String cid) throws Exception {
        
        ArrayList<Member> members = new ArrayList<>();
        try {
        	PreparedStatement ps = conn.prepareStatement("SELECT * FROM Member WHERE cid = ?;");
            ps.setString(1, cid);
            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Member m = generateMember(resultSet);
                members.add(m);
            }
            resultSet.close();
            ps.close();
            return members;

        } catch (Exception e) {
            throw new Exception("Failed in getting choices: " + e.getMessage());
        }
    }
    
    private Choice generateChoice(ResultSet resultSet,LambdaLogger logger) throws Exception {
        String cid  = resultSet.getString("cid");
        String chosenAid = resultSet.getString("chosenAid");
        String description = resultSet.getNString("description");
        int numMembers = resultSet.getInt("numMembers");
        String dateOfCreation = resultSet.getTimestamp("dateOfCreation").toString();
        { logger.log("dateOfCreation: " + dateOfCreation); }
        String dateOfCompletion = null;
        if(resultSet.getTimestamp("dateOfCompletion") != null) {
        	dateOfCompletion = resultSet.getTimestamp("dateOfCompletion").toString();
        }
        { logger.log("dateOfCompletion: " + dateOfCompletion); }
        return new Choice(cid, chosenAid, description, numMembers, dateOfCreation, dateOfCompletion);
    }
    
    private Member generateMember(ResultSet resultSet) throws Exception {
    	String mid = resultSet.getString("mid");
    	String cid = resultSet.getString("cid");
    	String name = resultSet.getString("name");
    	String password = resultSet.getString("password");
    	return new Member(mid, cid, name, password);
    }
    
    private Alternative generateAlternative(ResultSet resultSet) throws Exception {
    	System.out.println("in generateAlternative 1 ");
    	String aid = resultSet.getString("aid");
    	String cid = resultSet.getString("cid");
    	String description = resultSet.getString("description");
    	//int index = resultSet.getInt("index");
    	System.out.println("in generateAlternative 2 ");
    	return new Alternative(aid, cid, description);
    }
    
    private Vote generateVote(ResultSet resultSet) throws Exception {
    	String vid = resultSet.getString("vid");
    	String aid = resultSet.getString("aid");
    	String mid = resultSet.getString("mid");
    	String kind = resultSet.getString("kind");
    	String username = resultSet.getString("username");
    	return new Vote(vid, mid, aid, kind, username);
    }
    
    private Feedback generateFeedback(ResultSet resultSet) throws Exception {
    	String fid = resultSet.getString("fid");
    	String aid = resultSet.getString("aid");
    	String mid = resultSet.getString("mid");
    	String content = resultSet.getString("content");
    	Timestamp timestamp = resultSet.getTimestamp("timestamp");
    	String username = resultSet.getString("username");
    	return new Feedback(fid, mid, aid, content, timestamp, username);
    }

}
