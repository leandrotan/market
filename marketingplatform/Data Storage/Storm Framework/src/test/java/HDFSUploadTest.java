
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HDFSUploadTest {
    static Logger LOGGER = LoggerFactory.getLogger(HDFSUploadTest.class);
    private static String hdfsUrl;// = "hdfs://nex-hdp-14.nexius.com:8020";

    private String sourceFilename;
    private String destinationFilename;



    public HDFSUploadTest (String hdfsUrl, String fullSourceFilename, String fullDestinationFilename)
    {
        this.hdfsUrl = hdfsUrl;
        this.sourceFilename = fullSourceFilename;
        this.destinationFilename = fullDestinationFilename;
    }

    public String getfullSourceFilename() {
        return sourceFilename;
    }
    public void setfullSourceFilename(String fullSourceFilename) {
        this.sourceFilename = fullSourceFilename;
    }
    public String getfullDestinationFilename() {
        return destinationFilename;
    }

    public void setfullDestinationFilename(String fullDestinationFilename) {
        this.destinationFilename = fullDestinationFilename;
    }

    public boolean zipXMLFile() throws IOException {
        LOGGER.warn("Zipping: " +sourceFilename);
        String zipFullFilename = sourceFilename.replace(".xml", ".zip");
        String xmlFileName = sourceFilename.substring(sourceFilename.lastIndexOf("/"));


        FileOutputStream fos = new FileOutputStream(zipFullFilename);
        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry ze= new ZipEntry(xmlFileName);
        zos.putNextEntry(ze);

        FileInputStream fis = new FileInputStream(sourceFilename);
        int leng;

        byte[] zipbuffer = new byte[1024];
        while ((leng = fis.read(zipbuffer)) > 0) {
            zos.write(zipbuffer, 0, leng);
        }

        fis.close();
        zos.closeEntry();
        zos.close();

        boolean zipfilecreated = false;


        LOGGER.warn("Zipping Done, {} generated", zipFullFilename);
        File zipFile = new File (zipFullFilename);

        if (zipFile.exists())
        {
            zipfilecreated = true;

        } else zipfilecreated = false;



        return zipfilecreated;

    }

    public void uploadFile()
            throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", this.hdfsUrl);
        DFSClient client = new DFSClient(new URI(this.hdfsUrl), conf);

        String finalDestinationFileName = destinationFilename.replace(".xml",".zip");

        OutputStream out = null;
        InputStream in = null;

        try {
            if (client.exists(finalDestinationFileName)) {
                System.out.println("File already exists in hdfs: " + finalDestinationFileName);
                return;
            }
            out = new BufferedOutputStream(client.create(finalDestinationFileName, false));
            in = new BufferedInputStream(new FileInputStream(sourceFilename));
            byte[] buffer = new byte[1024];

            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } finally {

            if (client.exists(finalDestinationFileName)) {

                if (client != null) {
                    client.close();
                }

                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }

                File tmp = new File (sourceFilename);
                tmp.delete();

                tmp = new File (sourceFilename.replace(".zip",".xml"));
                tmp.delete();
            }

        }
    }
}