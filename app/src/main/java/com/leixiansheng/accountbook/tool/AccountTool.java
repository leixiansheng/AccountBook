package com.leixiansheng.accountbook.tool;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.leixiansheng.accountbook.data.Account;
import com.leixiansheng.accountbook.data.CostBean;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.leixiansheng.accountbook.data.KeyValue.*;

/**
 * Created by Leixiansheng on 2017/9/23.
 */

public class AccountTool {

    /**
     * 插入数据
     */
    public static void insertDB(CostBean costBean) {
        Connector.getDatabase();
        Account account = new Account();
        account.setCostTitle(costBean.getCostTitle());
        account.setCostDate(costBean.getCostDate());
        account.setCostMoney(costBean.getCostMoney());

        account.save();
    }

    /**
     * 更新表格修改数据
     * @param newCostBean
     * @param oldCostBean
     */
    public static void updateDB(CostBean newCostBean,CostBean oldCostBean) {
        Account account = new Account();
        account.setCostTitle(newCostBean.getCostTitle());
        account.setCostDate(newCostBean.getCostDate());
        account.setCostMoney(newCostBean.getCostMoney());

        account.updateAll(TABLE_TITLE + " = ? and " + TABLE_DATE + " = ? and " + TABLE_MONEY + " = ?",
                oldCostBean.getCostTitle(), oldCostBean.getCostDate(), oldCostBean.getCostMoney());
    }


    /**
     * 条件删除表格数据
     */
    public static void deleteDB(String title, String date, String money) {
        DataSupport.deleteAll(Account.class, TABLE_TITLE + " = ? and " + TABLE_DATE + " = ? and " + TABLE_MONEY + " = ?",
                title, date, money);
    }

    /**
     * 删除所有数据
     */
    public static void deleteAllDB() {
        DataSupport.deleteAll(Account.class);
    }

    /**
     * 查询所有数据，同是转换数据类型
     * @return
     */
    private static List<Map<String, String>> costList = new ArrayList<>();
    private static Map<String, String> map;

    public static List<Map<String, String>> queryAllDB(int sortType, boolean isSortUp, int queryYear, int queryMonth) {
        Connector.getDatabase();
        costList.clear();//清除上次保存数据

        List<Account> accountList = DataSupport.findAll(Account.class);//数据库查询
        for (Account account : accountList) {
            if (queryYear != 0 && queryMonth != 0) {
                String date = account.getCostDate();
                String[] strings = date.split("-");
                int year = Integer.parseInt(strings[0]);
                int month = Integer.parseInt(strings[1]);

                if (queryYear == year && queryMonth == month) {
                    //按年月查询
                    map = new HashMap<>();
                    map.put(TITLE, account.getCostTitle());
                    map.put(DATE, account.getCostDate());
                    map.put(MONEY, account.getCostMoney());
                    costList.add(map);
                }
            } else if (queryYear != 0 && queryMonth == 0) {
                //按年查询
                String date = account.getCostDate();
                String[] strings = date.split("-");
                int year = Integer.parseInt(strings[0]);

                if (queryYear == year) {
                    map = new HashMap<>();
                    map.put(TITLE, account.getCostTitle());
                    map.put(DATE, account.getCostDate());
                    map.put(MONEY, account.getCostMoney());
                    costList.add(map);
                }
            } else {
                //查询所有
                map = new HashMap<>();
                map.put(TITLE, account.getCostTitle());
                map.put(DATE, account.getCostDate());
                map.put(MONEY, account.getCostMoney());
                costList.add(map);
            }
        }
        if (sortType == SORT_DATE && costList.size() > 1) {//没有数据或只有一个数据则不进行排序
            Sort.sortByDate(costList, isSortUp);
        }
        if (sortType == SORT_MONEY && costList.size() > 1) {
            Sort.sortByMoney(costList, isSortUp);
        }
        return costList;
    }
}
