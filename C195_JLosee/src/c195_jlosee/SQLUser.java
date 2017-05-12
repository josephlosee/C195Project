package c195_jlosee;

/**
 * ${FILENAME}
 * Created by Joseph Losee on 5/8/2017.
 */
public class SQLUser {
    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    private int userID;
    private String userName;

    public SQLUser (int id, String name){
        this.userName = name;
        this.userID = id;
    }
}
