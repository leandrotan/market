package com.wicom.hive.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * Created by albert.elkhoury on 3/10/2015.
 */
public class VerticaWriter{
    static final Logger logger = LoggerFactory.getLogger(VerticaWriter.class);
    static Connection conn=null;
    static Statement stmt=null;
    static String driverName="com.vertica.jdbc.Driver";
    static String server="10.9.10.73";
    static String port="5433";
    static String db="VMartDB";
    static String user="dbadmin";
    static String pass="password";


    public static void main(String[] args){

        try {
            Class.forName(driverName).newInstance();
        } catch (ClassNotFoundException e) {
            logger.error("Could not find the JDBC driver class.");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        //new com.vertica.jdbc.Driver();

        Properties myProp = new Properties();
        myProp.put("user", user); // Must be superuser
        myProp.put("password", pass);
        myProp.put("AutoCommit", "false");
        try {
            String url="jdbc:vertica://" + server + ":" + port + "/" + db;
            conn = DriverManager.getConnection(url,user,pass);
            stmt = conn.createStatement();
            System.out.println(stmt.getConnection().getMetaData().getDatabaseProductName());
        }catch (SQLException e){
            e.printStackTrace();
        }

        try {
            logger.warn("Executing query...");
            //stmt.execute("COPY storm_cdr_data FROM LOCAL 'D:\\STORM\\Storm POC\\Trident Topology\\aggregate-temp.txt' DIRECT ENFORCELENGTH");
            stmt.execute("INSERT INTO storm_cdr_data values(1,1,1,1,1,1)");
            logger.warn("Query executed successfully.");
            //conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}