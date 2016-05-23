package com.wicom.hive.test;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by albert.elkhoury on 3/6/2015.
 */
public class HiveQuery {

    final Logger logger = LoggerFactory.getLogger(HiveQuery.class);

    public void performQueryOverHadoop() throws InterruptedException, IOException {
        String driverName = "org.apache.hive.jdbc.HiveDriver";

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection con = null;
        Statement stmt = null;
        ResultSet res = null;
        try {

            logger.debug("Trying to get connection");
            con = DriverManager.getConnection("jdbc:hive2://nex-hdp-14.nexius.com:10000/default", "sm_user", "hdp-08/home");
            logger.debug("Connected");
            stmt = con.createStatement();

            String sent = "";
            String sql = "select * from default.cdr_data where day_key like '2015/03/65'";
            res = stmt.executeQuery(sql);

            int columnsNumber=res.getMetaData().getColumnCount();
            while (res.next()) {
                for(int i=1;i<=columnsNumber;i++){
                    if(i>1) System.out.print(", ");
                    System.out.print(res.getString(i)+" ");
                }
                System.out.println("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                res.close();
            } catch (Exception e) { /* ignored */ }
            try {
                stmt.close();
            } catch (Exception e) { /* ignored */ }
            try {
                con.close();
            } catch (Exception e) { /* ignored */ }
        }
    }

    public static void main(String args[]) {
        try {
            UserGroupInformation ugi = UserGroupInformation.createRemoteUser("sm_user");
            //ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.SIMPLE);
            ugi.doAs(new PrivilegedExceptionAction<Void>() {
                public Void run() throws Exception {
                    HiveQuery hq = new HiveQuery();
                    hq.performQueryOverHadoop();

                    return null;
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
