import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;

/**
 * Created by Charbel Hobeika on 5/14/2015.
 */
public class test {

    public static void main(String[] args) throws IOException, InterruptedException {

        UserGroupInformation ugi= UserGroupInformation.createRemoteUser("sm_user");
        ugi.doAs(new PrivilegedExceptionAction<Void>(){
            public Void run() throws Exception{
                Configuration conf = new Configuration();
                conf.addResource(new Path("/src/main/resources/core-site.xml"));
                conf.addResource(new Path("/src/main/resources/hdfs-site.xml"));
                conf.set("fs.hdfs.impl",
                        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
                );
                conf.set("fs.file.impl",
                        org.apache.hadoop.fs.LocalFileSystem.class.getName()
                );
                FileSystem hdfs = FileSystem.get(conf);

                Path homeDir = hdfs.getHomeDirectory();
                //Print the home directory
                System.out.println("Home folder -" + homeDir);

                Path workingDir=hdfs.getWorkingDirectory();
                //Print the workingDir directory
                System.out.println("Working folder -" + workingDir);



                Path newFolderPath1= new Path("/user/sm_user/Storm/SocialMedia/Twitter/country=Egypt/day_key=20150519");

                //Path newFolderPath2= new Path("/user/sm_user/Hobeika/SocialMedia/Storm2/country=Egypt");
                // hdfs.delete(newFolderPath2,true);
                //hdfs.delete(newFolderPath2,true);

               /* if(hdfs.exists(newFolderPath2))
                {
                    hdfs.delete(newFolderPath2, true); //Delete existing Directory
                    hdfs.mkdirs(newFolderPath2);
                }
*/
                if(hdfs.exists(newFolderPath1))
                {
                    hdfs.delete(newFolderPath1, true); //Delete existing Directory
                    // hdfs.mkdirs(newFolderPath1);
                }
                hdfs.close(); // No more filesystem operations are needed. Will release any held locks

                //hdfs.createNewFile(newFolderPath);
                /*
                if(hdfs.exists(newFolderPath))
                {
                    hdfs.delete(newFolderPath,true);
                }


*/
                //Path newFilePath=new Path(newFolderPath1+"/newFile1.txt");
                //hdfs.createNewFile(newFilePath);
                //hdfs.delete(newFilePath,true);
                /*if(hdfs.exists(newFilePath))
                {
                    //hdfs.createNewFile(newFilePath);
                    hdfs.delete(newFilePath,true);
                }
*/
                return null;
            }
        });



    }
}
