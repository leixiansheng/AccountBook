package com.leixiansheng.accountbook.data;

import java.io.Serializable;

/**
 * Created by Leixiansheng on 2017/9/23.
 */

public class CostBean implements Serializable{

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
}
