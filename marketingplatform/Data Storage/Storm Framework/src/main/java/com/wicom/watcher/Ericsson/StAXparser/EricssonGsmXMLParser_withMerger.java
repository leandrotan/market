package com.wicom.watcher.Ericsson.StAXparser;

/**
 * Created by Charbel Hobeika on 4/14/2015.
 */

import backtype.storm.tuple.Values;
import com.vertica.jdbc.DataSource;
import com.wicom.watcher.GenericConstants;
import com.wicom.watcher.audit.HbaseAudit;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EricssonGsmXMLParser_withMerger implements Function {
    static Logger LOGGER = LoggerFactory.getLogger(EricssonGsmXMLParser_withMerger.class);
    String ParserfilePath;
    String ParserOutputfilePath;
    String processId;
    private int partitionIndex;
    XMLInputFactory factory;
    private String auditDbAuditTable;
    HbaseAudit HbA;

    Connection conn=null;
    Statement stmt=null;
    DataSource verticaDS=null;

    @Override
    public void execute(TridentTuple tridentTuple, TridentCollector tridentCollector) {
        String xmlFileName = tridentTuple.getStringByField("XMLfileName"); //.getString(1);
        //String fianlXMLFileName = tridentTuple.getString(2);
        String auditSid;

        List<EricssonGsm> ericssonList = null;
        EricssonGsm currentMeasInfo = null;
        String tagContent = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        LOGGER.warn(timeStamp +" .. . Reading file {} from partition {}",xmlFileName,partitionIndex);

        try {
            conn=verticaDS.getConnection();
            stmt = conn.createStatement();
            LOGGER.warn("Connection to: "+stmt.getConnection().getMetaData().getDatabaseProductName()+" aquired!");
        } catch (Exception e) {
            LOGGER.warn("Unable to aquire connection.");
            e.printStackTrace();
        }

        String fullFilePath;
        if (ParserfilePath.endsWith("/") || ParserfilePath.endsWith("\\"))
            fullFilePath = ParserfilePath + xmlFileName;
        else fullFilePath = ParserfilePath+"/"+xmlFileName;

        LOGGER.warn("Constructing Full Queued file path: {}",fullFilePath);

        String fullOutputFilePath;
        if (ParserOutputfilePath.endsWith("/") || ParserOutputfilePath.endsWith("\\"))
            fullOutputFilePath = ParserOutputfilePath + xmlFileName;
        else fullOutputFilePath = ParserOutputfilePath+"/"+xmlFileName;

        String date_sid = xmlFileName.substring(1, 9);
        String hour_sid = xmlFileName.substring(10,12);
        String second_sid = "0";
        String date_id=null;
        String BSC_id = xmlFileName.substring(xmlFileName.indexOf("_")+1,xmlFileName.lastIndexOf("_"));

        try {

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            date_id = sdf2.format(sdf1.parse(date_sid));
        } catch (ParseException e) {
            //LOGGER.warn(e.getMessage());
        }



        LOGGER.warn("Constructing Full Generated file path: {}",fullOutputFilePath);

        try {
            String newLine = System.lineSeparator();
            auditSid=HbA.logAudit("XML Parsing",fullFilePath);
            //auditSid="20150504";

            String genericHeader = date_sid + "|" + date_id+ "|" +hour_sid+ "|" +second_sid+ "|"+ auditSid;

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
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC="+BSC_id+"|"+genericHeader);
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
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC="+BSC_id+","+tagContent.replace(".-","")+"="+BSC_id+"|"+genericHeader);
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
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC="+BSC_id+"," + t[0] + "=" + t[1]+"|"+genericHeader);
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
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC="+BSC_id+",CELL=" +tt[0]+"," + t[0] + "=" + t[1]+"|"+genericHeader);
                                    } else if (tagContent.contains(".")) {
                                        String t[] = tagContent.split("\\.");
                                        currentMeasInfo.setmeasObjInstId("MSC=MSCS1,BSC="+BSC_id+",CELL=" + t[1] + "," + t[0] + "=" + t[1]+"|"+genericHeader);
                                    }
                                    else currentMeasInfo.setmeasObjInstId(tagContent+"|"+genericHeader);
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
                                .replace(", ", "|")
                );
                //System.out.println(emp.PrintMeasuresCounters());

                // adding batches of INSERTS for each employee instance
                try {
                    stmt.addBatch( emp.PrintMeasuresCounters());
                } catch (SQLException e) {
                    LOGGER.warn(e.getMessage());
                    e.printStackTrace();
                }

            }
            try {
                String query = "\n" +
                        "INSERT INTO storm_generic_dimension_types (dimension_type_sid, dimension_table_name, dimension_type)\n" +
                        "SELECT NEXTVAL('storm_dim_net_seq'), 'storm_dim_network', dimension_type\n" +
                        "FROM (\n" +
                        "SELECT DISTINCT dimension_type \n" +
                        "FROM storm_stg_measure_type\n" +
                        ") z \n" +
                        "WHERE dimension_type \n" +
                        "NOT IN (SELECT DISTINCT dimension_type FROM storm_generic_dimension_types);\n" +
                        "\n" +
                        "INSERT INTO storm_measure_dictionary (measure_sid, measure_name, measure_type, measure_column_index, fact_table)\n" +
                        "SELECT NEXTVAL('storm_dim_net_seq'), measure_name, measure_type, measure_column_index, fact_table\n" +
                        "FROM (SELECT DISTINCT measure_name, 'numeric' as measure_type, measure_column_index, 'storm_stg_fact' as fact_table FROM storm_stg_measure_type\n" +
                        ") z WHERE ISNULL(measure_name,'') <> ''\n" +
                        "AND measure_name NOT IN (SELECT DISTINCT measure_name FROM storm_measure_dictionary);\n" +
                        "\n" +
                        "INSERT INTO storm_types_x_measures (dimension_type_sid, measure_sid)\n" +
                        "SELECT dimension_type_sid,measure_sid \n" +
                        "FROM (\n" +
                        "SELECT DISTINCT measure_name,dimension_type FROM storm_stg_measure_type ) z \n" +
                        "INNER JOIN storm_generic_dimension_types b\n" +
                        "ON z.dimension_type = b.dimension_type\n" +
                        "INNER JOIN storm_measure_dictionary c\n" +
                        "ON z.measure_name = c.measure_name\n" +
                        "WHERE ISNULL(z.measure_name,'') <> ''\n" +
                        "AND CONCAT(dimension_type_sid,measure_sid)\n" +
                        "NOT IN (SELECT CONCAT(dimension_type_sid,measure_sid) FROM storm_types_x_measures);\n" +
                        "\n" +
                        "DELETE FROM storm_stg_measure_type;";

                stmt.addBatch(query);

                stmt.executeBatch();

                LOGGER.warn("Table storm_stg_measure_type is populated");
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                LOGGER.warn(e.getMessage());
                e.printStackTrace();
            }

            LOGGER.warn("Closing BufferedWriter !. File {} generated", file.getAbsolutePath());
            output.close();
            if (file.canRead()) {
                HbA.updateAudit(auditSid,"0","Parsed");
                tridentCollector.emit(new Values(auditSid));
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

        verticaDS=new DataSource();
        verticaDS.setHost((String) map.get(GenericConstants.VERTICA_SERVER));
        verticaDS.setPort(Short.parseShort(((String) map.get(GenericConstants.VERTICA_PORT))));
        verticaDS.setDatabase((String) map.get(GenericConstants.VERTICA_DB));
        verticaDS.setUserID((String) map.get(GenericConstants.VERTICA_USER));
        verticaDS.setPassword((String) map.get(GenericConstants.VERTICA_PASS));
        verticaDS.setAutoCommitOnByDefault(true);
    }

    @Override
    public void cleanup() {

    }
}
