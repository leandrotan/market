package com.wicom.watcher.DB;

/**
 * Created by Charbel Hobeika on 4/14/2015.
 */

import backtype.storm.tuple.Values;
import com.wicom.watcher.Ericsson.StAXparser.EricssonGsm;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
import com.wicom.watcher.hdfs.HDFSUpload;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.trident.operation.Function;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class transformerEricssonGsmXMLParser implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(transformerEricssonGsmXMLParser.class);
    String ParserfilePath;
    String ParserOutputfilePath;
    String processId;
    private int partitionIndex;
    XMLInputFactory factory;
    private String auditDbAuditTable;
    HbaseAudit HbA;
    HDFSUpload hdfsUpload = null;
    String hdfsuri = null;
    String hdfsDestinationDirectory = null;

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        String xmlFileName = tridentTuple.getStringByField("XMLfilename");
        String auditSid;

        List<EricssonGsm> ericssonList = null;
        EricssonGsm currentMeasInfo = null;
        String tagContent = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        LOGGER.warn(timeStamp + "  .. . Reading file {} from partition {}",xmlFileName,partitionIndex);

        String fullFilePath;
        if (ParserfilePath.endsWith("/") || ParserfilePath.endsWith("\\"))
            fullFilePath = ParserfilePath + xmlFileName;
        else fullFilePath = ParserfilePath+"/"+xmlFileName;

        LOGGER.warn("Constructing Full Queued file path: {}",fullFilePath);

        String fullOutputFilePath;
        if (ParserOutputfilePath.endsWith("/") || ParserOutputfilePath.endsWith("\\"))
            fullOutputFilePath = ParserOutputfilePath + xmlFileName;
        else fullOutputFilePath = ParserOutputfilePath+"/"+xmlFileName;

        String day_key = xmlFileName.substring(1, 9);
        String hour_sid = xmlFileName.substring(10,12);
        String second_sid = "0";
        //String date_id=null;
        String BSC_id = xmlFileName.substring(xmlFileName.indexOf("_")+1,xmlFileName.lastIndexOf("_"));
/*
        try {

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            date_id = sdf2.format(sdf1.parse(day_key));
        } catch (ParseException e) {
            //LOGGER.warn(e.getMessage());
        }
*/
        String genericHeader = day_key + "," + hour_sid + "," + second_sid;

        LOGGER.warn("Constructing Full Generated file path: {}",fullOutputFilePath);

        try {
            String newLine = System.lineSeparator();
            auditSid=HbA.logAudit("XML Parsing",fullFilePath);

            XMLStreamReader reader = null;
            try {
                File inputFile= new File(fullFilePath);
                /*
                while(!inputFile.canRead()){
                    LOGGER.warn("Cannot read File "+fullFilePath+" . " );
                    Thread.sleep(50);
                }
                */
                //Thread.sleep(5*1000);
                if(inputFile.canRead()){
                    reader = factory.createXMLStreamReader(
                            new FileInputStream(fullFilePath));
                }
                LOGGER.warn("Reading: {}",fullFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOGGER.warn("File: {} Not Found",fullFilePath);
                HbA.updateAudit(auditSid,"0","Error File Not Found");
            } catch (XMLStreamException e) {
                LOGGER.warn("File: {} Streaming Error",fullFilePath);
                HbA.updateAudit(auditSid,"0","Error File Streaming");
                e.printStackTrace();
            }


            try {
                while(reader.hasNext()){
                    int event = reader.next();

                    switch(event){
                        case XMLStreamConstants.START_ELEMENT:
                            if("MeasInfo".equals(reader.getLocalName())){
                                currentMeasInfo = new EricssonGsm();
                                if (ericssonList == null) {
                                    ericssonList = new ArrayList<>();
                                }
                                ericssonList.add(currentMeasInfo);
                            }
                            break;

                        case XMLStreamConstants.CHARACTERS:
                            tagContent = reader.getText().trim();
                            break;

                        case XMLStreamConstants.END_ELEMENT:
                            switch(reader.getLocalName()){
                                case "MeasType":
                                    currentMeasInfo.setMeasType(tagContent);
                                    break;
                                case "measObjInstId":
                                    if (tagContent.equals("BSC.-")) {
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1|BSC=NBSC6"+","+genericHeader);
                                    } else if (tagContent.equals("BSCGPRS.-")
                                            ||tagContent.equals("BSCGPRS2.-")
                                            ||tagContent.equals("TRH.-")
                                            ||tagContent.equals("GPHLOADREG.-")
                                            ||tagContent.equals("LOADREG.-")
                                            ||tagContent.equals("LOAS.-")
                                            ||tagContent.equals("LOASMISC.-")
                                            ||tagContent.equals("TRALOST.-")
                                            ||tagContent.equals("TRAPCOM.-")
                                            ) {
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1|BSC="+BSC_id+"|"+tagContent.replace(".-","")+"="+BSC_id+","+genericHeader);
                                    } else if (tagContent.contains("BSCQOS")
                                            || tagContent.contains("DIP")
                                            || tagContent.contains("EMGPRS")
                                            || tagContent.contains("LAPD")
                                            || tagContent.contains("NONRES64K")
                                            || tagContent.contains("RES64K")
                                            || tagContent.contains("TRAPEVENT")
                                            || tagContent.contains("MOTS")
                                            ) {
                                        String t[] = tagContent.split("\\.");
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1|BSC=NBSC6|" + t[0] + "=" + t[1]+","+genericHeader);
                                    } else if (tagContent.contains("NUCELLREL")
                                            || tagContent.contains("NCELLREL")
                                            || tagContent.contains("NECELASS")
                                            || tagContent.contains("NECELHO")
                                            || tagContent.contains("NECELHOEX")
                                            || tagContent.contains("NECELLREL")
                                            || tagContent.contains("NICELASS")
                                            || tagContent.contains("NICELHO")
                                            || tagContent.contains("NICELHOEX")
                                            ) {
                                        String t[] = tagContent.split("\\.");
                                        String tt[] = t[1].split("-");
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1|BSC=NBSC6|CELL=" +tt[0]+"|" + t[0] + "=" + t[1]+","+genericHeader);
                                    } else if (tagContent.contains(".")) {
                                        String t[] = tagContent.split("\\.");
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1|BSC=NBSC6|CELL=" + t[1] + "|" + t[0] + "=" + t[1]+","+genericHeader);
                                    }
                                    else currentMeasInfo.setmeasObjInstId(tagContent+","+genericHeader);
                                    break;
                                case "iValue":
                                    currentMeasInfo.setiValue(tagContent);
                                    break;
                                case "noValue":
                                    currentMeasInfo.setiValue("");
                                    break;
                            }
                            break;

                        case XMLStreamConstants.START_DOCUMENT:
                            ericssonList = new ArrayList<>();
                            break;
                    }

                }
            } catch (XMLStreamException e) {
                e.printStackTrace();
                LOGGER.warn("Streaming Error while reading File: {} ",fullFilePath);
                HbA.updateAudit(auditSid,"0","Streaming Error while reading File");
            }

            File file = new File(fullOutputFilePath.replace(".xml",".csv"));//To remove .xml extension before adding .csv

            File directory = new File(ParserOutputfilePath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(file));

            LOGGER.warn("BufferedWriter for {} created!",file.getAbsolutePath());

            for (EricssonGsm emp : ericssonList) {

               output.write(emp.PrintFunction().replace("], [", newLine)
                               .replace("]]", "")
                               .replace("[[", "")
                               .replace("[", "")
                               .replace("]", "")
                       //.replace(", ", "|") this is not needed because i need the file to be coma separated
               );
            }
            LOGGER.warn("Closing BufferedWriter !. File {} generated", file.getAbsolutePath());

            output.close();
            if (file.exists()) {
                HbA.updateAudit(auditSid,"0","Parsed");
                tridentCollector.emit(new Values(file.getName().replace(".csv",".xml"),auditSid));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void prepare(Map map, TridentOperationContext tridentOperationContext) {
        this.ParserfilePath = (String) map.get(GenericConstants.ASN_PARSER_OUTPUT_DIRECTORY);
        this.partitionIndex=tridentOperationContext.getPartitionIndex();
        this.processId = (String) map.get(GenericConstants.TOPOLOGY_PROCESS_ID);
        this.ParserOutputfilePath = (String) map.get(GenericConstants.STAX_PARSER_OUTPUT_DIRECTORY);
        this.factory = XMLInputFactory.newInstance();
        this.auditDbAuditTable= (String) map.get(GenericConstants.AUDIT_DB_AUDITTABLE);
        this.HbA = new HbaseAudit(this.auditDbAuditTable);

    }

    @Override
    public void cleanup() {

    }
}
