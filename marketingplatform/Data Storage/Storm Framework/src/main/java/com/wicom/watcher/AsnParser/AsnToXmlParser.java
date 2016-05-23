package com.wicom.watcher.AsnParser;

import backtype.storm.tuple.Values;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by albert.elkhoury on 4/16/2015.
 */
public class AsnToXmlParser implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(AsnToXmlParser.class);
    String asnParserPath;
    String asnParserSchemaPath;
    String asnParserOutputDirectory;
    String binaryFilePath=null;
    String processedFilePath;
    String errorFilePath;
    String auditDbAuditTable;
    HbaseAudit hba;
    private int partitionIndex;

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {

        String binaryFileName=tridentTuple.getString(0);
        String auditSid = tridentTuple.getString(1);

        SimpleDateFormat temporaryXMLfilenameFormat = new SimpleDateFormat("ddHHmmssSSS");
        Calendar c1 = Calendar.getInstance();

        String temporaryXMLfilename = temporaryXMLfilenameFormat.format(c1.getTime());
        String fullbinaryFilePath;

        if (binaryFilePath.endsWith("/") || binaryFilePath.endsWith("\\"))
            fullbinaryFilePath = binaryFilePath + binaryFileName;
        else fullbinaryFilePath = binaryFilePath +"/"+binaryFileName;

        String outputFileName=temporaryXMLfilename+".xml";//Limitation ont he output fikename, the tool is crashing when keeping the same filename
        String outputFilePath;


        if (asnParserOutputDirectory.endsWith("/") || asnParserOutputDirectory.endsWith("\\")) {
            outputFilePath = asnParserOutputDirectory + outputFileName;
        }
        else{
            outputFilePath=asnParserOutputDirectory + "/" + outputFileName;
        }

        String command = asnParserPath + " " + fullbinaryFilePath + " -xer -schema " + asnParserSchemaPath + " -xml -o " + outputFilePath;

        LOGGER.warn("Parsing file  "+binaryFileName+" with asn2txt parser.");
        LOGGER.warn("Running command: "+command);
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            LOGGER.warn(timeStamp+" ... Decoding file {} from partition {}",binaryFileName,partitionIndex);
            Process p=Runtime.getRuntime().exec(command);
            p.waitFor();
            //Add code to move binary filename to processed directory
            LOGGER.warn(binaryFileName+" parsed. Output file  "+outputFilePath+" was generated");

            FileUtils.moveFileToDirectory(new File(fullbinaryFilePath), new File(processedFilePath), true);
            LOGGER.warn(binaryFileName+" is moved to {}", processedFilePath);

            // renaming temporary file to binary.xml since we have limitation on asn2txt
            FileUtils.moveFile(new File(outputFilePath), new File(asnParserOutputDirectory + binaryFileName + ".xml"));
            LOGGER.warn(outputFilePath+" is renamed to {}", asnParserOutputDirectory + binaryFileName + ".xml");

            hba.updateAudit(auditSid, "0", "Decoded");

        } catch (IOException e) {
            LOGGER.error("Error Decoding file."+e.getMessage());
            try {
                FileUtils.moveFileToDirectory(new File(fullbinaryFilePath), new File(errorFilePath), true);
                hba.updateAudit(auditSid, "0", "Error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Unexpected Error Decoding file."+e.getMessage());
            try {
                FileUtils.moveFileToDirectory(new File(fullbinaryFilePath), new File(errorFilePath), true);
                hba.updateAudit(auditSid, "0", "Error");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        tridentCollector.emit(new Values(binaryFileName+".xml"));

    }

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext) {
        this.asnParserPath = (String) map.get(GenericConstants.ASN_PARSER_PATH);
        if(System.getProperty("os.name").startsWith("Windows")){
            this.asnParserPath+="asn2txt.exe";
        }
        else{
            this.asnParserPath=asnParserPath.concat("asn2txt.sh");
        }
        this.asnParserSchemaPath = (String) map.get(GenericConstants.ASN_PARSER_SCHEMA_PATH);
        this.asnParserOutputDirectory = (String) map.get(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY);
        this.binaryFilePath = (String) map.get(GenericConstants.WATCHER_FILE_QUEUED_DIRECTORY);
        this.processedFilePath = (String) map.get(GenericConstants.WATCHER_FILE_PROCESSED_DIRECTORY);
        this.errorFilePath = (String) map.get(GenericConstants.WATCHER_FILE_ERROR_DIRECTORY);
        this.auditDbAuditTable = (String) map.get(GenericConstants.AUDIT_DB_AUDITTABLE);
        this.hba = new HbaseAudit(this.auditDbAuditTable);
        this.partitionIndex=tridentOperationContext.getPartitionIndex();
    }

    @Override
    public void cleanup() {

    }
}
