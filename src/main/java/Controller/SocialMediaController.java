package Controller;

import java.sql.*;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import Model.Account;
import Model.Message;
import Util.ConnectionUtil;
import Service.MessageService;
import Service.AccountService;
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
        AccountService accountService = new AccountService();
        MessageService messageService = new MessageService();


        // Register
        app.post("register", ctx -> {
            String Json = ctx.body();
            Account account = objectMapper.readValue(Json, Account.class);

            Account registeredAccount = accountService.registerUser(account);
            if (registeredAccount!=null){
                ctx.status(200);
                // account.setAccount_id(findIDAccount(userN));
                ctx.json(account);
            } else{
                ctx.status(400);
            }
        });

        // Login
        app.post("login", ctx -> {
            // json=ctx.body(); then object mapper then extract from account
            String Json = ctx.body();
            Account account = objectMapper.readValue(Json, Account.class);

            Account loggedUser = accountService.loginUser(account);
            if(loggedUser!=null){
                ctx.status(200);
                // account.setAccount_id(findIDAccount(userN));
                ctx.json(account);
            } else{
                ctx.status(401);
            }
        });


        // create message
        app.post("messages", ctx -> {
            String Json = ctx.body();
            Message message = objectMapper.readValue(Json, Message.class);

            Message newMessage = messageService.createMessage(message);
            if(newMessage==null){
                ctx.status(400);
            }else{
                ctx.status(200);
                ctx.json(newMessage);
            }

        });
        // get all messages
        app.get("messages", ctx -> {
            ctx.status(200);
            ctx.json(messageService.getAllMessages());
        });

        // retrieve msg by id
        app.get("messages/{message_id}",ctx -> {
            String mID = ctx.pathParam("message_id");
            // System.out.println("mid: "+ mID);
            Message messageFound = messageService.findMessageByID(Integer.parseInt(mID));
            if(messageFound!=null){
                ctx.json(messageFound);
                ctx.status(200);
            }
        });

        // delete msg by id
        app.delete("messages/{message_id}", ctx -> {
            String mID = ctx.pathParam("message_id");

            int intmID = Integer.parseInt(mID);
            Message toBeDeleted = messageService.deleteMessage(intmID);
            if(toBeDeleted!=null){
                ctx.json(toBeDeleted);
            }
            ctx.status(200);
        });

        // update msg by id
        app.patch("messages/{message_id}", ctx -> {
            String mID = ctx.pathParam("message_id");
            String json = ctx.body();
            Message newMessageTxt = objectMapper.readValue(json, Message.class);

            Message newMessage = messageService.updateMessage(Integer.parseInt(mID), newMessageTxt.getMessage_text());
            if (newMessage!=null){
                ctx.status(200);
                ctx.json(messageService.findMessageByID(Integer.parseInt(mID)));
            } else{
                ctx.status(400);
            }
        });

        // get all messages from user's account id
        app.get("accounts/{account_id}/messages",ctx ->{
            String aID = ctx.pathParam("account_id");
            ArrayList<Message> allMsgs = messageService.getAllMessagesFromAUser(Integer.parseInt(aID));
            ctx.status(200);
            ctx.json(allMsgs);
        });

        return app;
    }

}