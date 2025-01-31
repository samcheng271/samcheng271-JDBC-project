package DAO;
import java.sql.*;
import java.util.*;


import Model.Account;
// import Model.Message;
import Util.ConnectionUtil;
public class AccountDAO {
    // add account to database
    public void addAccount(String username, String password) {
        try(Connection conn = ConnectionUtil.getConnection()){
            PreparedStatement ps = conn.prepareStatement("insert into account (username, password) values (?, ?);");
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean accountExists(String username) {
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

    public int findIDAccount(String user) {
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
    
    
    public String findPass(String user) {
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
    
    // view all values in database. For debugging purposes.
    public void viewDatabase() {
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
