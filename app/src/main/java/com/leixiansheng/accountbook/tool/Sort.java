package com.leixiansheng.accountbook.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.leixiansheng.accountbook.data.KeyValue.*;

/**
 * Created by Leixiansheng on 2017/9/25.
 */

public class Sort {

    /**
     * 通过创建时间排序
     * @param costList
     */
    public static void sortByDate(List<Map<String, String>> costList, final boolean isSortUp) {
        Collections.sort(costList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                String time1 = o1.get(DATE);
                String time2 = o2.get(DATE);
//                return date1.compareTo(date2);        //时间格式这样比较不行

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = sdf.parse(time1);
                    Date date2 = sdf.parse(time2);
                    if (isSortUp) {
                        return (int) ((date1.getTime() - date2.getTime()) / (24 * 3600 * 1000));//降序排列
                    } else {
                        return (int) ((date2.getTime() - date1.getTime())/(24*3600*1000));//升序排列
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    /**
     * 按消费排序
     *
     * @param costList
     */
    public static void sortByMoney(List<Map<String, String>> costList, final boolean isSortUp) {
        if (costList == null || costList.size() <= 0) {
            return;
        }
        Collections.sort(costList, new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                //放大100倍，转换成整型比较
                int money1 = (int)(Float.valueOf(o1.get(MONEY))*100);
                int money2 = (int)(Float.valueOf(o2.get(MONEY))*100);
                if (isSortUp) {
                    return money2 - money1;//小到大
                } else {
                    return money1-money2;//大到小
                }
            }
        });
    }

}
