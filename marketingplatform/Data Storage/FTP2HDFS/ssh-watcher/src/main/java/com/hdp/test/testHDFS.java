package com.hdp.test;

import java.security.PrivilegedExceptionAction;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileStatus;

public class testHDFS {

    public static void main(String args[]) {

        try {
            UserGroupInformation ugi
                = UserGroupInformation.createRemoteUser("albert.aouad");

            ugi.doAs(new PrivilegedExceptionAction<Void>() {

                public Void run() throws Exception {

                    Configuration conf = new Configuration();
                    conf.set("fs.defaultFS", "hdfs://nex-hdp-14.nexius.com");
                    conf.set("hadoop.job.ugi", "albert.aouad");

                    FileSystem fs = FileSystem.get(conf);

                    fs.createNewFile(new Path("test"));

                    FileStatus[] status = fs.listStatus(new Path("user/albert.aouad"));
                    for(int i=0;i<status.length;i++){
                        System.out.println(status[i].getPath());
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}