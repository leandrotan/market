package com.wicom.watcher.hdfs;

import backtype.storm.tuple.Values;
import com.wicom.watcher.GenericConstants;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HDFSUpload implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(HDFSUpload.class);
    private static String hdfsUrl;
    private String xmlFilename;
    private String xmlFileSourceDirectory;
    private String hdfsDestinationDirectory;

    @Override
    public void execute(TridentTuple tuple, TridentCollector tridentCollector) {
        xmlFilename = tuple.getStringByField("XMLfileName");
        LOGGER.warn("Zipping: " +xmlFileSourceDirectory+"/"+xmlFilename);
        String zipFullFilename = xmlFileSourceDirectory.concat("/"+xmlFilename.replace(".xml", ".zip"));
        String xmlFullFilename = xmlFileSourceDirectory.concat("/"+xmlFilename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(zipFullFilename);

        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry ze= new ZipEntry(xmlFilename);
        zos.putNextEntry(ze);

        FileInputStream fis = new FileInputStream(xmlFullFilename);
        int leng;

        byte[] zipbuffer = new byte[1024];
        while ((leng = fis.read(zipbuffer)) > 0) {
            zos.write(zipbuffer, 0, leng);
        }

        fis.close();
        zos.closeEntry();
        zos.close();

        LOGGER.warn("Zipping Done, {} generated", zipFullFilename);
            File zipFile = new File (zipFullFilename);

            if (zipFile.exists())
            {

                Configuration conf = new Configuration();
                conf.set("fs.defaultFS", this.hdfsUrl);

                DFSClient client = new DFSClient(new URI(this.hdfsUrl), conf);
                String date_sid = xmlFilename.substring(1, 9);
                LOGGER.warn("Connection to {} established", this.hdfsUrl);
                String finalDestinationFileName = hdfsDestinationDirectory.concat(date_sid+"/").concat(xmlFilename.replace(".xml", ".zip"));
                OutputStream out = null;
                InputStream in = null;

                try {
                    if (client.exists(finalDestinationFileName)) {
                        LOGGER.warn("File already exists in hdfs: " + finalDestinationFileName);
                        return;
                    }
                    out = new BufferedOutputStream(client.create(finalDestinationFileName, false));
                    in = new BufferedInputStream(new FileInputStream(zipFullFilename));
                    byte[] buffer = new byte[1024];

                    int len = 0;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }finally {

                    if (client.exists(finalDestinationFileName)) {
                        LOGGER.warn("{} succesfully Uploaded to hdfs {} under {}", zipFullFilename, this.hdfsUrl, finalDestinationFileName);
                        if (client != null) {
                            client.close();
                        }

                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }

                        File tmp = new File(xmlFullFilename);
                        LOGGER.warn("Deleteing {}", xmlFullFilename);
                        tmp.delete();

                        tmp = new File(zipFullFilename);
                        LOGGER.warn("Deleteing {}", zipFullFilename);
                        tmp.delete();

                        tridentCollector.emit(new Values(xmlFilename.replace(".xml",".csv")));
                    } else {
                        if (client != null) {
                            client.close();
                        }

                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                        }

                    }
                }
            }

        } catch (FileNotFoundException e) {
            LOGGER.warn("FileNotFoundException." + e.getMessage());
            //e.printStackTrace();
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
        } catch (URISyntaxException e) {
            LOGGER.warn(e.getMessage());
        }
    }

    @Override
    public void prepare(Map conf, TridentOperationContext context) {
        this.hdfsUrl= (String) conf.get(GenericConstants.HDFS_URI);
        this.hdfsDestinationDirectory = (String) conf.get(GenericConstants.HDFS_DESTINATION_DIRECTORY);
        this.xmlFileSourceDirectory = (String) conf.get(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY);
    }

    @Override
    public void cleanup() {
    }
}