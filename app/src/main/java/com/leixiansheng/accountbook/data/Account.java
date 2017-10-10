package com.leixiansheng.accountbook.data;

import org.litepal.crud.DataSupport;

/**
 * Created by Leixiansheng on 2017/9/23.
 */

public class Account extends DataSupport {

    private int id;
    private String costTitle;
    private String costDate;
    private String costMoney;

    public String getCostDate() {
        return costDate;
    }

    public void setCostDate(String costDate) {
        this.costDate = costDate;
    }

    public String getCostMoney() {
        return costMoney;
    }

    public void setCostMoney(String costMoney) {
        this.costMoney = costMoney;
    }

    public String getCostTitle() {
        return costTitle;
    }

    public void setCostTitle(String costTitle) {
        this.costTitle = costTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
