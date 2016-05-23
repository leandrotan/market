package com.wicom.watcher.audit;



import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Charbel Hobeika on 3/23/2015.
 */
public class HbaseAudit {
    static Logger LOGGER = LoggerFactory.getLogger(HbaseAudit.class);

    public static String dimAudit;
    public static HTable auditTable;

    public HbaseAudit(String auditTable)
    {
        this.dimAudit = auditTable;
        LOGGER.warn("Audit Table: " + auditTable);
    };

    public void connect(){
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new Path("/src/main/resources/hbase-site.xml"));
        try {
            HBaseAdmin hbAdmin = new HBaseAdmin(conf);
            /*This instantiates an HTable object that connects you to the "dim_audit" table*/
            this.auditTable = new HTable(conf, dimAudit);
        }catch (TableNotFoundException e) {
            //System.out.println("Audit Table '" +dimAudit +"' does NOT exist");
            LOGGER.error(e.getMessage());
            LOGGER.error("Audit Table '" + dimAudit + "' does NOT exist");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close(){
        try {
            this.auditTable.close();
            LOGGER.warn("Connection to " + dimAudit + " is closed");
        } catch (IOException e) {
            LOGGER.error("Connection to " + dimAudit + " cannot be closed. " + e);
        }

    }

    // logAudit will be used to insert a new record in dim_audit each time a file is captured
    public String logAudit(String process_id, String FileName)  throws MasterNotRunningException, ZooKeeperConnectionException, IOException {

        SimpleDateFormat dateSidFormatting = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat auditSidFormatting = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar c1 = Calendar.getInstance();

        Timestamp currentTS = new Timestamp(Calendar.getInstance().getTime().getTime());

        /* To add to a row, use Put. A Put constructor takes the name of the row
        you want to insert into as a byte array.
         this is considered as the UNIQUE identifier, and we will use as value of audit_sid*/
        Put auditPut = new Put(Bytes.toBytes(auditSidFormatting.format(c1.getTime())));

        /* To set the value you'd like to update in the row 'row1', specify
        the column family, column qualifier, and value of the table cell you'd
        like to update.  The column family must already exist in your table
        schema.  The qualifier can be anything.  */
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("audit_sid"), Bytes.toBytes(auditSidFormatting.format(c1.getTime())));
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("time_created"), Bytes.toBytes(currentTS.toString()));
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("date_sid"), Bytes.toBytes(dateSidFormatting.format(c1.getTime())));
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("process_id"), Bytes.toBytes(process_id));
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("status"), Bytes.toBytes("New"));

        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("FileName"), Bytes.toBytes(FileName));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rowCount"), Bytes.toBytes("0"));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rejectedCount"), Bytes.toBytes("0"));
        /* Once you've adorned your Put instance with all the updates you want to
         make, to commit it do the following */
        this.connect();
        this.auditTable.put(auditPut);
        this.close();
        LOGGER.warn("Audit Entry created in " + dimAudit + " for file: " + FileName + " with audit_sid=" + auditSidFormatting.format(c1.getTime()));

        return auditSidFormatting.format(c1.getTime());

    }

    public void updateAudit(String rowKey, String rowCount) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {

        /* To add to a row, use Put. A Put constructor takes the name of the row
        you want to insert into as a byte array.
        this is considered as the UNIQUE identifier, audit_sid*/
        Put auditPut = new Put(Bytes.toBytes(rowKey));

        /* To set the value you'd like to update in the row 'row1', specify
        the column family, column qualifier, and value of the table cell you'd
        like to update.  The column family must already exist in your table
        schema.  The qualifier can be anything.  */
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("status"), Bytes.toBytes("Processed"));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rowCount"), Bytes.toBytes(rowCount));
        /* Once you've adorned your Put instance with all the updates you want to
        make, to commit it do the following */
        this.connect();
        auditTable.put(auditPut);
        this.close();
        LOGGER.warn("Audit Entry " + rowKey + " is updated with status: Processed");

    }

    public void updateAudit(String rowKey, String rowCount, String Status) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {

        /* To add to a row, use Put. A Put constructor takes the name of the row
        you want to insert into as a byte array.
        this is considered as the UNIQUE identifier, audit_sid*/
        Put auditPut = new Put(Bytes.toBytes(rowKey));

        /* To set the value you'd like to update in the row 'row1', specify
        the column family, column qualifier, and value of the table cell you'd
        like to update.  The column family must already exist in your table
        schema.  The qualifier can be anything.  */
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("status"), Bytes.toBytes(Status));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rowCount"), Bytes.toBytes(rowCount));
        /* Once you've adorned your Put instance with all the updates you want to
        make, to commit it do the following */
        this.connect();
        auditTable.put(auditPut);
        this.close();
        LOGGER.warn("Audit Entry " + rowKey + " is updated with status: {}", Status);

    }

    public void updateAudit(String rowKey, String rowCount, String rejectedCount, String Status) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {

        /* To add to a row, use Put. A Put constructor takes the name of the row
        you want to insert into as a byte array.
        this is considered as the UNIQUE identifier, audit_sid*/
        Put auditPut = new Put(Bytes.toBytes(rowKey));

        /* To set the value you'd like to update in the row 'row1', specify
        the column family, column qualifier, and value of the table cell you'd
        like to update.  The column family must already exist in your table
        schema.  The qualifier can be anything.  */
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("status"), Bytes.toBytes(Status));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rowCount"), Bytes.toBytes(rowCount));
        auditPut.add(Bytes.toBytes("optional_columns"), Bytes.toBytes("rejectedCount"), Bytes.toBytes(rejectedCount));
        /* Once you've adorned your Put instance with all the updates you want to
        make, to commit it do the following */
        this.connect();
        auditTable.put(auditPut);
        this.close();
        LOGGER.warn("Audit Entry " + rowKey + " is updated with status: {}", Status);

    }

    public  void updateAudit(String rowKey) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {

         /* To add to a row, use Put. A Put constructor takes the name of the row
      you want to insert into as a byte array.
      this is considered as the UNIQUE identifier, audit_sid*/
        Put auditPut = new Put(Bytes.toBytes(rowKey));

         /* To set the value you'd like to update in the row 'row1', specify
      the column family, column qualifier, and value of the table cell you'd
      like to update.  The column family must already exist in your table
      schema.  The qualifier can be anything.  */
        auditPut.add(Bytes.toBytes("mandatory_columns"), Bytes.toBytes("status"), Bytes.toBytes("Processed"));
        LOGGER.warn("Audit Entry " + rowKey + " is updated with status: Processed");
         /* Once you've adorned your Put instance with all the updates you want to
      make, to commit it do the following */
        this.connect();
        this.auditTable.put(auditPut);
        this.close();

    }



}
