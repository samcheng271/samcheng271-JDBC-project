package Controller;

import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // app.get("example-endpoint", this::exampleHandler);
        ObjectMapper objectMapper = new ObjectMapper();

        // Register
        app.post("register", ctx -> {
            String Json = ctx.body();
            Account account = objectMapper.readValue(Json, Account.class);
            String userN = account.getUsername();
            // Check if username and password are valid and if account already exists in database
            if(userN.isEmpty() || account.getPassword().length() < 4 || accountExists(userN)) {
                ctx.status(400);
            } else {
                addAccount(userN, account.getPassword());
                ctx.status(200);
                account.setAccount_id(findIDAccount(userN));
                ctx.json(account);
                // viewDatabase(userN);
            }
        });

        // Login
        app.post("login", ctx -> {
            // json=ctx.body(); then object mapper then extract from account
            String Json = ctx.body();
            Account account = objectMapper.readValue(Json, Account.class);
            String userN = account.getUsername();
            // System.out.println(account.getPassword().equals(findPass(userN)) );
            if(!accountExists(userN) || !(account.getPassword().equals(findPass(userN)))) {
                ctx.status(401);
            } else{
                ctx.status(200);
                account.setAccount_id(findIDAccount(userN));
                ctx.json(account);
            }
        });

        // create message
        app.post("messages", ctx -> {
            String Json = ctx.body();
            Message message = objectMapper.readValue(Json, Message.class);
            int userN = message.getPosted_by();
            if(!MessengerExists(userN)) {
                ctx.status(400);
            } else {
                // message must be less than 255 characters
                if(message.getMessage_text().length() > 255 || message.getMessage_text().isEmpty()) {
                    ctx.status(400);
                }else {
                    ctx.status(200);
                    addMessage(message);
                    // System.out.println("in http msg: "+ findIDMessage(message));
                    // viewDatabase();

                    message.setMessage_id(findIDMessage(message));
                    ctx.json(message);
                }
            }
        });
        // get all messages
        app.get("messages", ctx -> {
            ctx.status(200);
            ctx.json(getAllMessages());
        });

        // retrieve msg by id
        app.get("messages/{message_id}",ctx -> {
            String mID = ctx.pathParam("message_id");
            // System.out.println("mid: "+ mID);
            Message messageFound = findMessageByID(Integer.parseInt(mID));
            if(messageFound!=null){
                ctx.json(messageFound);
                ctx.status(200);
            }
        });

        // delete msg by id
        app.delete("messages/{message_id}", ctx -> {
            String mID = ctx.pathParam("message_id");
            // Message messageFound = findMessageByID(Integer.parseInt(mID));
            // don't need to check since delete does nothing if row doesn't exist
            Message toBeDeleted = findMessageByID(Integer.parseInt(mID));
            if(deleteMessage(Integer.parseInt(mID))){
                // ctx.status(200);
                ctx.json(toBeDeleted);
            }
            // ctx.json(Integer.parseInt(mID));
            ctx.status(200);
        });

        // update msg by id
        app.patch("messages/{message_id}", ctx -> {
            String mID = ctx.pathParam("message_id");
            String json = ctx.body();
            Message newMessageTxt = objectMapper.readValue(json, Message.class);
            // Message messageFound = findMessageByID(Integer.parseInt(mID));
            // don't need to check since delete does nothing if row doesn't exist
            if(updateMessage(Integer.parseInt(mID),newMessageTxt.getMessage_text())==true){
                ctx.status(200);
                ctx.json(findMessageByID(Integer.parseInt(mID)));

            }else{
                ctx.status(400);
            }
        });

        // get all messages from user's account id
        app.get("accounts/{account_id}/messages",ctx ->{
            String aID = ctx.pathParam("account_id");
            ArrayList<Message> allMsgs = getAllMessagesFromAUser(Integer.parseInt(aID));
            ctx.status(200);
            ctx.json(allMsgs);
        });

        return app;
    }

    // ================Messages vvvvvvvvv ========================
        // add message to database
    private void addMessage(Message message) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("insert into message (message_text,posted_by,time_posted_epoch) values (?,?,?);");
            ps.setString(1, message.getMessage_text());
            ps.setInt(2, message.getPosted_by());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean deleteMessage(int message_id){
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("Delete from message where message_id=?;");
            ps.setInt(1, message_id);
            int check = ps.executeUpdate();
            if(check>0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean updateMessage(int message_id,String newMessageTxt){
        if (newMessageTxt.length()<255 && !newMessageTxt.isEmpty()){
            try(Connection conn = ConnectionUtil.getConnection()){
                PreparedStatement ps = conn.prepareStatement("Update message set message_text=? where message_id=?;");
                // ps.setInt(1, message.getPosted_by());
                ps.setString(1, newMessageTxt);
                ps.setInt(2,message_id);
                int rs = ps.executeUpdate();

                if(rs>0){
                    return true;
                } else{
                    return false;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    private Message findMessageByID(int message_id){
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("SELECT * From message where message_id=?;");
            ps.setInt(1,message_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return new Message(rs.getInt("message_id"), rs.getInt("posted_by"),rs.getString("message_text"),rs.getLong("time_posted_epoch"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int findIDMessage(Message message) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select message_id from message where posted_by=? and time_posted_epoch=? and message_text=?;");
            ps.setInt(1, message.getPosted_by());
            ps.setLong(2, message.getTime_posted_epoch());
            ps.setString(3, message.getMessage_text());
            // System.out.println(message.getPosted_by());
            // System.out.println(message.getTime_posted_epoch());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // System.out.println("findIDMsg: "+rs.getString("message_id"));
                return rs.getInt("message_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private boolean MessengerExists(int posted_by) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from message where posted_by = ?;");
            ps.setInt(1, posted_by);  
            ResultSet rs = ps.executeQuery();
            // System.out.println("checker: "+rs);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private ArrayList<Message> getAllMessagesFromAUser(int acc) {
        ArrayList<Message> messages = new ArrayList<Message>();
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from message where posted_by=?;");
            ps.setInt(1,acc);

            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    private ArrayList<Message> getAllMessages() {
        ArrayList<Message> messages = new ArrayList<Message>();
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from message;");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    // ====================Accounts vvvvvvvvv ======================
    // add account to database
    private void addAccount(String username, String password) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("insert into account (username, password) values (?, ?);");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean accountExists(String username) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from account where username = ?;");
            ps.setString(1, username);  
            ResultSet rs = ps.executeQuery();
            // System.out.println("checker: "+rs.getString("username"));
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int findIDAccount(String user) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select account_id from account where username=?;");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // System.out.println(rs.getString("account_id"));
                return rs.getInt("account_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    
    private String findPass(String user) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select password from account where username=?;");
            ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                // System.out.println(rs.getString("account_id"));
                return rs.getString("password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // view all values in database
    private void viewDatabase() {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from message;");
            // ps.setString(1, user);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                System.out.println("in ViewDB, msgID: "+rs.getInt("message_id")+
                " postedBy: "+rs.getInt("posted_by")+
                " text: "+rs.getString("message_text")+
                " time: "+rs.getLong("time_posted_epoch")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}