package DAO;
import java.sql.*;
import java.util.*;


// import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

public class MessageDao {
    public void addMessage(Message message) {
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

    public boolean deleteMessage(int message_id){
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("Delete from message where message_id=?;");
            ps.setInt(1, message_id);
            // don't need to check since delete does nothing if row doesn't exist
            return ps.executeUpdate()>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateMessage(int message_id,String newMessageTxt){
        if (newMessageTxt.length()<255 && !newMessageTxt.isEmpty()){
            try(Connection conn = ConnectionUtil.getConnection()){
                PreparedStatement ps = conn.prepareStatement("Update message set message_text=? where message_id=?;");
                // ps.setInt(1, message.getPosted_by());
                ps.setString(1, newMessageTxt);
                ps.setInt(2,message_id);
                return ps.executeUpdate()>0;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // Get the message of an ID
    public Message findMessageByID(int message_id){
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

    // Get the ID of a message
    public int findIDMessage(Message message) {
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
    public boolean MessengerExists(int posted_by) {
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
    public ArrayList<Message> getAllMessagesFromAUser(int acc) {
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
    public ArrayList<Message> getAllMessages() {
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
}
