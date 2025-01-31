package Service;
import DAO.MessageDao;
import Model.Message;
import java.util.*;

public class MessageService {
    MessageDao messageDao = new MessageDao();

    public Message createMessage(Message msg){
        int userN = msg.getPosted_by();
        if(!messageDao.MessengerExists(userN)) {
            return null;
        } else {
            // message must be 0<x<256
            if(msg.getMessage_text().length() > 255 || msg.getMessage_text().isEmpty()) {
                return null;
            }else {
                messageDao.addMessage(msg);
                // System.out.println("in http msg: "+ findIDMessage(message));
                // viewDatabase();
                msg.setMessage_id(messageDao.findIDMessage(msg));
                return msg;
            }
        }
    }

    public ArrayList<Message> getAllMessages() {
        return messageDao.getAllMessages();
    }

    public  Message findMessageByID(int message_id){
        return messageDao.findMessageByID(message_id);
    }

    public Message deleteMessage(int mID){
        Message toBeDeleted = messageDao.findMessageByID(mID);
        if (toBeDeleted!=null && messageDao.deleteMessage(mID)){
            return toBeDeleted;
        }
        return null;
    }

    public Message updateMessage(int message_id,String newMessageTxt){
        if ((newMessageTxt.length()<255 && !newMessageTxt.isEmpty()) && messageDao.updateMessage(message_id,newMessageTxt)){
            return messageDao.findMessageByID(message_id);
        }
        return null;
    }

    public ArrayList<Message> getAllMessagesFromAUser(int accountId) {
        return messageDao.getAllMessagesFromAUser(accountId);
    }
}
