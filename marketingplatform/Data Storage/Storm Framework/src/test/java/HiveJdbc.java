import com.wicom.watcher.GenericConstants;

import java.io.FileReader;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;

public class HiveJdbc {

    public void fetchauditentries() throws SQLException {
        String driver = "org.apache.hive.jdbc.HiveDriver";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection connect = DriverManager.getConnection("jdbc:hive2://nex-hdp-14.nexius.com:10000/default", "sm_user", "hdp-08/home");

        Statement state = connect.createStatement();

        //String query = "SELECT DISTINCT t1.audit_sid, t1.FileName, t1.rowCount, t1.status FROM dim_audit_hivehbase t1 JOIN raw_cdr_data t2 ON (t1.audit_sid = t2.auditsid) AND t2.filename=\"cdr_post_data20130123_204_118099.dwh\"";
String query = "SELECT count (*) AS cnt \n" +
        " FROM dim_audit_hivehbase t1 \n" +
        " WHERE t1.FileName like '%C20150426.1800%'\n" +
        " AND status = 'Processed'";

        //String query0 = "set hive.input.format=org.apache.hadoop.hive.ql.io.HiveInputFormat";
       // state.execute(query0);
        System.out.println("Running audit query");
        ResultSet res = state.executeQuery(query);
        //while (res.next()) {
       //     System.out.println(res.getString(1) + "\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + res.getString(4));
       // }
        System.out.println(res.getInt("cnt"));
        connect.close();
    }


    public static void main(String[] args) throws SQLException {


        HiveJdbc h = new HiveJdbc();
        h.fetchauditentries();

    }
}