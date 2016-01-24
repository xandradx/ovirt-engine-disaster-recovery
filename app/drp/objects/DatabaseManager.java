package drp.objects;

/**
 * Created by jandrad on 23/01/16.
 */
public class DatabaseManager {

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;

    public DatabaseManager(String host, String port, String name, String user, String password) {
        dbHost = host;
        dbPort = port;
        dbName = name;
        dbUser = user;
        dbPassword = password;
    }

    public String getDbHost() {
        return dbHost;
    }

    public String getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
