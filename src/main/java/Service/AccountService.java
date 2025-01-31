package Service;
import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    AccountDAO accountDao = new AccountDAO();
    
    // Register new users
    public Account registerUser(Account account){
        String userN = account.getUsername();
        // Check if username and password are valid and if account already exists in database
        if(userN.isEmpty() || account.getPassword().length() < 4 || accountDao.accountExists(userN)) {
            return null;
        }
            accountDao.addAccount(userN, account.getPassword());
            account.setAccount_id(accountDao.findIDAccount(userN));
            return account;
            // viewDatabase(userN);
    }

    // Login user
    public Account loginUser(Account account){
        String userN = account.getUsername();

        if(!accountDao.accountExists(userN) || !(account.getPassword().equals(accountDao.findPass(userN)))) {
                return null;
            }
                account.setAccount_id(accountDao.findIDAccount(userN));
                return account;
    }
}
