import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Charbel Hobeika on 3/19/2015.
 */
public class testmyClass {

    public static void main(String[] args) throws Exception {

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               if (name.contains("C20150426.1800"))
                   return true;
               else return false;
            }
        };

        File f = new File("D:\\Data\\Transformer\\incoming");
        File[] list = f.listFiles(filter);

        if (list.length == 5) {
            File file1 = new File(String.valueOf(list[0]));
            File file2 = new File(String.valueOf(list[1]));
            File file3 = new File(String.valueOf(list[2]));
            File file4 = new File(String.valueOf(list[3]));
            File file5 = new File(String.valueOf(list[4]));

            File finalGeneric = new File("D:\\Data\\Transformer\\incoming\\test3.csv");

            String file1Str = FileUtils.readFileToString(file1);
            String file2Str = FileUtils.readFileToString(file2);
            String file3Str = FileUtils.readFileToString(file3);
            String file4Str = FileUtils.readFileToString(file4);
            String file5Str = FileUtils.readFileToString(file5);

            FileUtils.write(finalGeneric, file1Str);
            FileUtils.write(finalGeneric, file2Str, true);
            FileUtils.write(finalGeneric, file3Str, true);
            FileUtils.write(finalGeneric, file4Str, true);
            FileUtils.write(finalGeneric, file5Str, true);
        }
   /*






                String populateNetworkAndDictionary = String.valueOf(testmyClass.class.getResource("populateNetworkAndDictionary.sql")); //"/src/main/resources/populateNetworkAndDictionary.sql";
        String csvFileName = "C20150426.1800+0300-20150426.1900+0300_NBSC7_1000.csv";
        System.out.println(populateNetworkAndDictionary);
        System.out.println(populateNetworkAndDictionary);
        HbaseAudit hba = new HbaseAudit("dim_audit");
        hba.logAudit("test","file1");

String xmlFileName = "C20150408.0800+0300-20150408.0900+0300_NBSC7_1000";
        String date_sid = xmlFileName.substring(1, 9);
        String hour_sid = xmlFileName.substring(10,12);
        String second_sid = "0";
        String date_id=null;
        String BSC = xmlFileName.substring(xmlFileName.indexOf("_")+1,xmlFileName.lastIndexOf("_"));
        try {

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
            date_id = sdf2.format(sdf1.parse(date_sid));
        } catch (ParseException e) {
            //LOGGER.warn(e.getMessage());
        }

        System.out.println(date_sid + "," + date_id+ "," +hour_sid+ "," +second_sid);
        System.out.println(BSC);

*/
}
}