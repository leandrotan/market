/**
 * Created by Charbel Hobeika on 5/7/2015.
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibatis.common.jdbc.ScriptRunner;


public class RunSqlScript {
    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException,
            SQLException {

        String aSQLScriptFilePath = "C:\\Users\\Charbel Hobeika\\Desktop\\vertica_update.sql";
        Connection conn=null;
        Statement stmt=null;
        String driverName="com.vertica.jdbc.Driver";
        String server="10.104.5.28";
        String port="5433";
        String db="verticadst";
        String user="alfxpldev";
        String pass="xpl123";

        // Create vertica Connection
        Class.forName(driverName);
        String url = "jdbc:vertica://" + server + ":" + port + "/" + db;
        conn = DriverManager.getConnection(url, user, pass);

        try {
            // Initialize object for ScripRunner
            ScriptRunner sr = new ScriptRunner(conn, false, false);

            // Give the input file to Reader
            Reader reader = new BufferedReader(
                    new FileReader(aSQLScriptFilePath));

            // Exctute script
            sr.runScript(reader);

        } catch (Exception e) {
            System.err.println("Failed to Execute" + aSQLScriptFilePath
                    + " The error is " + e.getMessage());
        }
    }
}