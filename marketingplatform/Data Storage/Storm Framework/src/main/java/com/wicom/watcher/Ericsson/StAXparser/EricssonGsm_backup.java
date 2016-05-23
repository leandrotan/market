package com.wicom.watcher.Ericsson.StAXparser;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Charbel Hobeika on 4/9/2015.
 */

public class EricssonGsm_backup {
    private List<String> measObjInstId = new ArrayList<String>();
    private List<String> MeasType = new ArrayList<String>();
    private List<String> iValue = new ArrayList<String>();


    public void setmeasObjInstId(String measObjInstId) {
        this.measObjInstId.add(measObjInstId);
    }

    public List<String> getmeasObjInstId() {
        return  measObjInstId;
    }

    public List<String> getMeasType() {
       return MeasType;
    }
    public void setMeasType(String MeasType) {
        this.MeasType.add(MeasType);

    }
    public List<String> getiValue() {
        return iValue;
   }


    public void setiValue(String iValue) {
        this.iValue.add(iValue);
    }

    // split a list onto sublists if multiple measObjInstId exist under the same MeasType
    public static <T extends Object> List<List<T>> split(List<T> list, int targetSize) {
        List<List<T>> lists = new ArrayList<List<T>>();
        for (int i = 0; i < list.size(); i += targetSize) {
            lists.add(list.subList(i, Math.min(i + targetSize, list.size())));
        }
        return lists;
    }

    public String PrintFunction() {

        String newLine = System.lineSeparator();
        int measObjInstId_cnt = this.measObjInstId.size();
        int MeasType_cnt = this.MeasType.size();
        int iValue_cnt = 0;

        // combining iValues and measObjInstId if multiple measObjInstId exist under the same MeasType
        List<String> combined = new ArrayList<String>();

        for (int i = 0; i < this.measObjInstId.size(); i++) {
            combined.add(this.measObjInstId.get(i));
            for (int j = 0; j < this.MeasType.size(); j++) {
                //combined.add(this.MeasType.get(j)+"="+this.iValue.get(iValue_cnt));
                combined.add(this.iValue.get(iValue_cnt));
                iValue_cnt++;
            }
        }
        if (measObjInstId_cnt > 1) {
            int targetSize = MeasType_cnt+1;
            List<List<String>> lists = split(combined, targetSize);
            return String.valueOf(lists.toString()+newLine);
        }
        return String.valueOf(combined+newLine);

    }

}
