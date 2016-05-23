/**
 * Created by Charbel Hobeika on 4/20/2015.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class VerticaWriter {
    static final Logger logger = LoggerFactory.getLogger(VerticaWriter.class);
    static Connection conn=null;
    static Statement stmt=null;
    static String driverName="com.vertica.jdbc.Driver";
    static String server="10.104.5.28";
    static String port="5433";
    static String db="verticadst";
    static String user="alfxpldev";
    static String pass="xpl123";



    public static void main(String[] args) {

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

        try {
            String url = "jdbc:vertica://" + server + ":" + port + "/" + db;
            conn = DriverManager.getConnection(url, user, pass);
            //conn.setAutoCommit();
            stmt = conn.createStatement();

            String query = " select count (DISTINCT audit_sid) cnt, hour_sid\n" +
                    " from storm_stg_fact\n" +
                    " \n" +
                    " group by hour_sid;";

            /*String query = "CREATE OR REPLACE VIEW V_STORM_NETWORK as \n" +
                    "WITH one AS(\n" +
                    "SELECT DISTINCT SPLIT_PART(network_id,',',1) as id,\n" +
                    "SPLIT_PART(network_id,'=',1) AS type, 1 levels\n" +
                    "FROM storm_stg_fact\n" +
                    "WHERE REGEXP_COUNT(network_id, '=') = 2\n" +
                    "UNION ALL\n" +
                    "SELECT DISTINCT network_id as id,\n" +
                    "SUBSTRING (SUBSTRING(network_id,\n" +
                    "INSTR(network_id, ',', -1) +1),\n" +
                    "1,\n" +
                    "INSTR(SUBSTRING(network_id,\n" +
                    "INSTR(network_id, ',', -1)+2), '=') ) AS type, \n" +
                    " REGEXP_COUNT(network_id, '=') levels\n" +
                    "FROM storm_stg_fact\n" +
                    "UNION ALL \n" +
                    "SELECT DISTINCT SUBSTRING(network_id, 1,INSTR(network_id, ',',-1)-1) as id,\n" +
                    "SPLIT_PART(SPLIT_PART(network_id,'=',3), ',',2) AS type, \n" +
                    " 3 levels\n" +
                    "FROM storm_stg_fact\n" +
                    "WHERE REGEXP_COUNT(network_id, '=') = 4\n" +
                    ")\n" +
                    "SELECT id,type, levels \n" +
                    ", CASE levels\n" +
                    "WHEN 5 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',3), ',',2),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',4), ',',2),'|')\n" +
                    ",SPLIT_PART(SPLIT_PART(id,'=',5), ',',2)))))\n" +
                    "WHEN 4 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',3), ',',2),'|')\n" +
                    ",SPLIT_PART(SPLIT_PART(id,'=',4), ',',2))))\n" +
                    " WHEN 3 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')\n" +
                    ",CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',2), ',',2),'|')\n" +
                    ",SPLIT_PART(SPLIT_PART(id,'=',3), ',',2)))\n" +
                    " WHEN 2 THEN CONCAT(CONCAT(SPLIT_PART(SPLIT_PART(id,'=',1), ',',1),'|')\n" +
                    ",SPLIT_PART(SPLIT_PART(id,'=',2), ',',2))\n" +
                    "WHEN 1 THEN SPLIT_PART(id,'=',1)\n" +
                    "END type_path\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',1), '=',2) AS level1_id\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',1), '=',1) AS level1_type\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',2), '=',2) AS level2_id\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',2), '=',1) AS level2_type\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',3), '=',2) AS level3_id\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',3), '=',1) AS level3_type\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',4), '=',2) AS level4_id\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',4), '=',1) AS level4_type\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',5), '=',2) AS level5_id\n" +
                    ", SPLIT_PART(SPLIT_PART(id,',',5), '=',1) AS level5_type\n" +
                    "FROM one\n" +
                    "WHERE id NOT IN (SELECT DISTINCT id FROM storm_dim_network);\n" +
                    "\n" +
                    "--create sequence storm_dim_net_seq START 1;\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS storm_dim_network_temp;\n" +
                    "CREATE TABLE storm_dim_network_temp -- creating temporary table and assigning level_sids accordingly if exist\n" +
                    "AS\n" +
                    "SELECT levels, \n" +
                    "CAST(NULL AS INTEGER) network_sid,\n" +
                    "s.id, \n" +
                    "s.type,\n" +
                    "s.type_path,\n" +
                    "level1.level1_sid, s.level1_id, s.level1_type\n" +
                    ",level2.level2_sid, s.level2_id, s.level2_type\n" +
                    ",level3.level3_sid, s.level3_id, s.level3_type\n" +
                    ",level4.level4_sid, s.level4_id, s.level4_type \n" +
                    " FROM V_STORM_NETWORK s\n" +
                    "LEFT JOIN (\n" +
                    "SELECT DISTINCT level1_sid,level1_id\n" +
                    "FROM storm_dim_network\n" +
                    "WHERE ISNULL(storm_dim_network.level2_id,'') = '') level1\n" +
                    "on ISNULL(s.level1_id,'') = ISNULL(level1.level1_id,'')\n" +
                    "\n" +
                    "LEFT JOIN (SELECT DISTINCT level2_sid,storm_dim_network.level2_id\n" +
                    "FROM storm_dim_network\n" +
                    "WHERE (ISNULL(storm_dim_network.level3_id,'') = ''\n" +
                    "AND ISNULL(storm_dim_network.level2_id,'') <> '')) level2\n" +
                    "on ISNULL(s.level2_id,'') = ISNULL(level2.level2_id,'')\n" +
                    "\n" +
                    "LEFT JOIN (\n" +
                    "\n" +
                    "SELECT DISTINCT level3_sid,storm_dim_network.level3_id,storm_dim_network.level2_id,storm_dim_network.level3_type\n" +
                    "FROM storm_dim_network\n" +
                    "WHERE ISNULL(storm_dim_network.level4_id,'') = ''\n" +
                    "AND ISNULL(storm_dim_network.level3_id,'') <> ''\n" +
                    "AND ISNULL(storm_dim_network.level2_id,'') <> ''\n" +
                    "\n" +
                    ") level3\n" +
                    "on ISNULL(s.level3_id,'') = ISNULL(level3.level3_id,'')\n" +
                    "AND ISNULL(s.level3_type,'') = ISNULL(level3.level3_type,'')\n" +
                    "AND ISNULL(s.level2_id,'') = ISNULL(level3.level2_id,'')\n" +
                    "\n" +
                    "LEFT JOIN (\n" +
                    "\n" +
                    "SELECT DISTINCT level4_sid,storm_dim_network.level4_id,storm_dim_network.level4_type,storm_dim_network.level3_id,storm_dim_network.level2_id,storm_dim_network.level3_type\n" +
                    "FROM storm_dim_network\n" +
                    "WHERE ISNULL(storm_dim_network.level5_id,'') = ''\n" +
                    "AND ISNULL(storm_dim_network.level3_id,'') <> ''\n" +
                    "AND ISNULL(storm_dim_network.level4_id,'') <> ''\n" +
                    "AND ISNULL(storm_dim_network.level2_id,'') <> ''\n" +
                    "\n" +
                    ") level4\n" +
                    "on ISNULL(s.level4_id,'') = ISNULL(level4.level4_id,'')\n" +
                    "AND ISNULL(s.level4_type,'') = ISNULL(level4.level4_type,'')\n" +
                    "AND ISNULL(s.level3_id,'') = ISNULL(level4.level3_id,'')\n" +
                    "AND ISNULL(s.level3_type,'') = ISNULL(level4.level3_type,'')\n" +
                    "AND ISNULL(s.level2_id,'') = ISNULL(level4.level2_id,'');\n" +
                    "\n" +
                    " DROP TABLE IF EXISTS storm_dim_network_level1;\n" +
                    "CREATE TABLE storm_dim_network_level1 AS\n" +
                    "SELECT DISTINCT level1_id, CAST(NULL AS INTEGER) level1_sid\n" +
                    "FROM storm_dim_network_temp\n" +
                    "where level1_sid is null\n" +
                    "AND network_sid is NULL;\n" +
                    "\n" +
                    "UPDATE storm_dim_network_level1\n" +
                    "SET level1_sid = NEXTVAL('storm_dim_net_seq');\n" +
                    "\n" +
                    "\n" +
                    "UPDATE storm_dim_network_temp\n" +
                    "   SET level1_sid=storm_dim_network_level1.level1_sid\n" +
                    "   FROM storm_dim_network_level1\n" +
                    "   WHERE storm_dim_network_temp.level1_id = storm_dim_network_level1.level1_id;\n" +
                    "   \n" +
                    "DROP TABLE IF EXISTS storm_dim_network_level2;\n" +
                    "CREATE TABLE storm_dim_network_level2 AS\n" +
                    "SELECT DISTINCT level2_id as id, CAST(NULL AS INTEGER) level2_sid\n" +
                    "FROM storm_dim_network_temp\n" +
                    "where level2_sid is null\n" +
                    "AND network_sid is NULL;\n" +
                    "\n" +
                    "UPDATE storm_dim_network_level2\n" +
                    "SET level2_sid = NEXTVAL('storm_dim_net_seq');\n" +
                    "\n" +
                    "\n" +
                    "UPDATE storm_dim_network_temp\n" +
                    "   SET level2_sid=storm_dim_network_level2.level2_sid\n" +
                    "   FROM storm_dim_network_level2\n" +
                    "   WHERE storm_dim_network_temp.level2_id = storm_dim_network_level2.id\n" +
                    "   ;\n" +
                    "   \n" +
                    "DROP TABLE IF EXISTS storm_dim_network_level3;\n" +
                    "CREATE TABLE storm_dim_network_level3 AS\n" +
                    "SELECT DISTINCT id, CAST(NULL AS INTEGER) level3_sid\n" +
                    "FROM storm_dim_network_temp\n" +
                    "where level3_sid is null\n" +
                    "AND network_sid is NULL\n" +
                    "AND LEVELS = 3;\n" +
                    "\n" +
                    "UPDATE storm_dim_network_level3\n" +
                    "SET level3_sid = NEXTVAL('storm_dim_net_seq');\n" +
                    "\n" +
                    "\n" +
                    "UPDATE storm_dim_network_temp\n" +
                    "   SET level3_sid=storm_dim_network_level3.level3_sid\n" +
                    "   FROM storm_dim_network_level3\n" +
                    "   WHERE storm_dim_network_temp.id= storm_dim_network_level3.id;\n" +
                    "\n" +
                    "\n" +
                    "DROP TABLE IF EXISTS storm_dim_network_level4;\n" +
                    "CREATE TABLE storm_dim_network_level4 AS\n" +
                    "SELECT DISTINCT id, CAST(NULL AS INTEGER) level4_sid\n" +
                    "FROM storm_dim_network_temp\n" +
                    "where level4_sid is null\n" +
                    "AND network_sid is NULL\n" +
                    "AND LEVELS = 4;\n" +
                    "\n" +
                    "UPDATE storm_dim_network_level4\n" +
                    "SET level4_sid = NEXTVAL('storm_dim_net_seq');\n" +
                    "\n" +
                    "\n" +
                    "UPDATE storm_dim_network_temp\n" +
                    "   SET level4_sid=storm_dim_network_level4.level4_sid\n" +
                    "   FROM storm_dim_network_level4\n" +
                    "   WHERE storm_dim_network_temp.id= storm_dim_network_level4.id\n" +
                    "   ;\n" +
                    "   \n" +
                    "\n" +
                    "   UPDATE storm_dim_network_temp\n" +
                    "   SET network_sid =\n" +
                    "CASE levels\n" +
                    "WHEN 4 THEN level4_sid\n" +
                    "WHEN 3 THEN level3_sid\n" +
                    "WHEN 2 THEN level2_sid\n" +
                    "WHEN 1 THEN level1_sid\n" +
                    "END;\n" +
                    "\n" +
                    "INSERT INTO \"storm_dim_network\"\n" +
                    "(\"network_sid\"\n" +
                    ",\"id\"\n" +
                    ",\"type\"\n" +
                    ",\"type_path\"\n" +
                    ",\"level1_sid\"\n" +
                    ",\"level1_id\"\n" +
                    ",\"level1_type\"\n" +
                    ",\"level2_sid\"\n" +
                    ",\"level2_id\"\n" +
                    ",\"level2_type\"\n" +
                    ",\"level3_sid\"\n" +
                    ",\"level3_id\"\n" +
                    ",\"level3_type\"\n" +
                    ",\"level4_sid\"\n" +
                    ",\"level4_id\"\n" +
                    ",\"level4_type\"\n" +
                    ",\"vendor\"\n" +
                    ",\"technology\"\n" +
                    ",\"created_time\")\n" +
                    "SELECT \"network_sid\"\n" +
                    ",\"id\"\n" +
                    ",\"type\"\n" +
                    ",\"type_path\"\n" +
                    ",\"level1_sid\"\n" +
                    ",\"level1_id\"\n" +
                    ",\"level1_type\"\n" +
                    ",\"level2_sid\"\n" +
                    ",\"level2_id\"\n" +
                    ",\"level2_type\"\n" +
                    ",\"level3_sid\"\n" +
                    ",\"level3_id\"\n" +
                    ",\"level3_type\"\n" +
                    ",\"level4_sid\"\n" +
                    ",\"level4_id\"\n" +
                    ",\"level4_type\"\n" +
                    ",'Ericsson' \"vendor\"\n" +
                    ",'Gsm' \"technology\"\n" +
                    ",CURRENT_TIMESTAMP \"created_time\"\n" +
                    "FROM storm_dim_network_temp;\n" +
                    "\n" +
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
                    "INSERT INTO storm_alfapomg_dim_network_2g (cell_sid, cellname, msc_sid, mscname, bsc_sid, bscname, district_sid, vendor, technology, start_datetime)\n" +
                    "SELECT DISTINCT network_sid as cell_sid,level3_id as cellname\n" +
                    ",level1_sid as msc_sid, level1_id as mscname\n" +
                    ",level2_sid as bsc_sid, level2_id as bscname\n" +
                    ",0 district_sid,\n" +
                    "vendor, technology, sysdate()\n" +
                    "FROM storm_dim_network\n" +
                    "WHERE vendor = 'Ericsson'\n" +
                    "AND technology = 'Gsm'\n" +
                    "AND type = 'CELL'\n" +
                    "AND network_sid NOT IN (SELECT DISTINCT cell_sid FROM storm_alfapomg_dim_network_2g);";
            */
            //stmt.addBatch(query);
            Integer filesCount=0;
            ResultSet rs = stmt.executeQuery(query);
            while ( rs.next() ) {
                  filesCount = Integer.valueOf(rs.getString("cnt"));
            };

            System.out.println(filesCount);
            System.out.println(stmt.getConnection().getMetaData().getDatabaseProductName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt.executeBatch();
            stmt.close();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
