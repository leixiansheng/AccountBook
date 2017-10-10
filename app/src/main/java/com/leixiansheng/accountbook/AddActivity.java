package com.leixiansheng.accountbook;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leixiansheng.accountbook.data.CostBean;
import com.leixiansheng.accountbook.tool.AccountTool;

import java.util.Calendar;

import static com.leixiansheng.accountbook.data.KeyValue.DATE;
import static com.leixiansheng.accountbook.data.KeyValue.IS_UPDATE;
import static com.leixiansheng.accountbook.data.KeyValue.MONEY;
import static com.leixiansheng.accountbook.data.KeyValue.TITLE;

/**
 * Created by Leixiansheng on 2017/9/25.
 */

public class AddActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText costTitle;
    private EditText costMoney;
    private Button save;
    private ImageButton back;
    private DatePicker datePicker;
    private TextView showTime;

    private CostBean oldCostBean;
    private Bundle bundle;
    private boolean isUpdate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);

        initView();
        setOnclickListener();
        initData();
        showTimes();
    }

    private void setOnclickListener() {
        findViewById(R.id.add_layout).setOnClickListener(this);
        save.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    /**
     * 显示时间
     */
    private void showTimes() {
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR)); //获取年
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1); //获取月
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)); //获取日

        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)); //获取周
        switch (week) {
            case "1":
                week = getString(R.string.sunday);
                break;
            case "2":
                week = getString(R.string.monday);
                break;
            case "3":
                week = getString(R.string.tuesday);
                break;
            case "4":
                week = getString(R.string.wednesday);
                break;
            case "5":
                week = getString(R.string.thursday);
                break;
            case "6":
                week = getString(R.string.friday);
                break;
            case "7":
                week = getString(R.string.saturday);
                break;
        }
        showTime.setText(year + "-" + month + "-" + day + "(" + week + ")");
    }

    /**
     *如果是数据修改，则添加数据
     */
    private void initData() {
        isUpdate = getIntent().getBooleanExtra(IS_UPDATE,false);

        if (isUpdate) {
            //判断是更新还是添加
            bundle = getIntent().getExtras();
            String date = bundle.getString(DATE);
            String money = bundle.getString(MONEY);
            String title = bundle.getString(TITLE);

            costTitle.setText(title);
            costMoney.setText(money);
            subDate(date);//设置时间

            oldCostBean = new CostBean();
            oldCostBean.setCostTitle(title);
            oldCostBean.setCostMoney(money);
            oldCostBean.setCostDate(date);
        }
    }

    /**
     * 分开年月日，设置时间
     */
    private void subDate(String date) {
        /**
         * split截取方式
         */
        String[] strings = date.split("-");
        int year = Integer.parseInt(strings[0]);
        int month = Integer.parseInt(strings[1]) - 1;
        int day = Integer.parseInt(strings[2]);
        datePicker.updateDate(year, month, day);
    }

/*
    private void subDate(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd");

            int year = Integer.parseInt(sdf0.format(d));
            int month = Integer.parseInt(sdf1.format(d)) - 1;
            int day = Integer.parseInt(sdf2.format(d));

            datePicker.updateDate(year, month, day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/

    /**
     * 保存表格数据
     */
    private void saveData() {
        String title = costTitle.getText().toString();
        String date = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
        String money = costMoney.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(money)) {
            Toast.makeText(this, R.string.save_warning, Toast.LENGTH_SHORT).show();
        } else {
            CostBean costBean = new CostBean();
            costBean.setCostTitle(title);
            costBean.setCostDate(date);
            costBean.setCostMoney(money);

            if (isUpdate) {
                if (title.equals(oldCostBean.getCostTitle()) && date.equals(oldCostBean.getCostDate())
                        && money.equals(oldCostBean.getCostMoney())) {
                    Toast.makeText(this, getString(R.string.update_nothing),Toast.LENGTH_SHORT).show();
                } else {
                    AccountTool.updateDB(costBean, oldCostBean);
                    Toast.makeText(this, getString(R.string.update_success),Toast.LENGTH_SHORT).show();
                }
            } else {
                AccountTool.insertDB(costBean);
                Toast.makeText(this, getString(R.string.add_success),Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void initView() {
        save = (Button) findViewById(R.id.save);
        back = (ImageButton) findViewById(R.id.back);
        costTitle = (EditText) findViewById(R.id.cost_title);
        costMoney = (EditText) findViewById(R.id.cost_money);
        datePicker = (DatePicker) findViewById(R.id.date);
        showTime = (TextView) findViewById(R.id.show_time);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                saveData();
                break;
            case R.id.add_layout:
                //点击屏幕空白区隐藏键盘
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
