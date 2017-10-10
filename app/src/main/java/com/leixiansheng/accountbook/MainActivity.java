package com.leixiansheng.accountbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.leixiansheng.accountbook.tool.AccountTool;
import com.leixiansheng.accountbook.view.MonDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.leixiansheng.accountbook.data.KeyValue.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private TextView costDate;
    private TextView costMoney;
    private TextView showCost;
    private ListView costListView;
    private Button add;
    private SimpleAdapter costAdapter;

    private List<Map<String, String>> costList = new ArrayList<>();
    private int sortType = SORT_DATE; //排序方式 类型
    private boolean isSortUp = false; //排序方式 是否升序
    private int queryYear = 0; //查询 年
    private int queryMonth = 0; //查询 月


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        costList = AccountTool.queryAllDB(sortType, isSortUp, queryYear, queryMonth);
        initView();
        setListener();
    }

    private void setListener() {
        add.setOnClickListener(this);
        costDate.setOnClickListener(this);
        costMoney.setOnClickListener(this);
        costListView.setOnItemClickListener(this);
        costListView.setOnItemLongClickListener(this);
    }

    private void initView() {
        add = (Button) findViewById(R.id.add);
        costDate = (TextView) findViewById(R.id.cost_date);
        costMoney = (TextView) findViewById(R.id.cost_money);
        costListView = (ListView) findViewById(R.id.cost_list);
        showCost = (TextView) findViewById(R.id.show_cost);

        costAdapter = new SimpleAdapter(this, costList,
                R.layout.list_item, new String[]{TITLE, DATE, MONEY},
                new int[]{R.id.cost_title, R.id.cost_date, R.id.cost_money});
        costListView.setAdapter(costAdapter);
    }

    /**
     * 新建完成更新列表
     */
    @Override
    protected void onStart() {
        super.onStart();
        updateListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);//得到菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.delete));
                builder.setMessage(getString(R.string.delete_all));

                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AccountTool.deleteAllDB();//删除数据库所有数据
                        updateListView();
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.show();
                break;
            case R.id.query_way:
                isSortUp = true;
                showMonDatePicker();
                break;
            case R.id.query_all:
                if (queryYear != 0 && queryMonth != 0) {
                    queryYear = 0;
                    queryMonth = 0;
                    updateListView();
                }
                break;
            case R.id.total:
                startActivity(new Intent(this, ChartsActivity.class));
                break;
            case R.id.help:
                AlertDialog.Builder help = new AlertDialog.Builder(this);
                help.setView(R.layout.help_dialog);
                help.setTitle(getString(R.string.help));
                help.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *显示自定义日历（只有年月）
     */
    private void showMonDatePicker() {
        final Calendar localCalendar = Calendar.getInstance();
        MonDatePickerDialog monDatePickerDialog = new MonDatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new MonDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        localCalendar.set(Calendar.YEAR, year);
                        localCalendar.set(Calendar.MONTH, monthOfYear);
                        queryYear = year;
                        queryMonth = monthOfYear + 1;
                        updateListView();
                    }
                }, localCalendar.get(Calendar.YEAR), localCalendar.get(Calendar.MONTH), localCalendar.get(Calendar.DATE));
        monDatePickerDialog.myShow();

        // 将对话框的大小按屏幕大小的百分比设置
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = monDatePickerDialog.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() * 0.8); //设置宽度
        monDatePickerDialog.getWindow().setAttributes(lp);

        // 去掉显示日  只显示年月
        ((ViewGroup)((ViewGroup) monDatePickerDialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
    }

    /**
     * 刷新列表
     */
    private void updateListView() {
        costList = AccountTool.queryAllDB(sortType, isSortUp, queryYear, queryMonth);
        costAdapter.notifyDataSetChanged();
        totalCost();
    }

    /**
     * 点击响应
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra(IS_UPDATE, false);
                startActivity(intent);
                break;
            case R.id.cost_date:
                sortType = SORT_DATE;
                isSortUp = !isSortUp;
                updateListView();
                break;
            case R.id.cost_money:
                sortType = SORT_MONEY;
                isSortUp = !isSortUp;
                updateListView();
                break;
        }
    }

    /**
     * 列表点击响应
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, AddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(DATE, costList.get(i).get(DATE));
        bundle.putString(MONEY, costList.get(i).get(MONEY));
        bundle.putString(TITLE, costList.get(i).get(TITLE));
        intent.putExtras(bundle);
        intent.putExtra(IS_UPDATE, true);
        intent.putExtra(POSITION, i);
        startActivity(intent);
    }

    /**
     *长按响应
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String title = costList.get(i).get(TITLE);
        final String date = costList.get(i).get(DATE);
        final String money = costList.get(i).get(MONEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.delete));
        builder.setMessage(getString(R.string.delete_inform) + "  " + title + "  " + date
                + "  " + money + getString(R.string.money));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountTool.deleteDB(title, date, money);
                updateListView();
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.show();
        return true;
    }

    private void totalCost() {
        float original = 0;
        for (int i = 0; i < costList.size(); i++) {
            Float money = Float.valueOf(costList.get(i).get(MONEY));
            original += money;
        }
        showCost.setText(getString(R.string.total_cost) + original + getString(R.string.money));
    }

}
