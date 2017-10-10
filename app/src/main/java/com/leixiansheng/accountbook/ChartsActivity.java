package com.leixiansheng.accountbook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leixiansheng.accountbook.tool.AccountTool;
import com.leixiansheng.accountbook.view.MonDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.formatter.SimpleLineChartValueFormatter;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

import static com.leixiansheng.accountbook.data.KeyValue.DATE;
import static com.leixiansheng.accountbook.data.KeyValue.MONEY;
import static com.leixiansheng.accountbook.data.KeyValue.SORT_DATE;
import static com.leixiansheng.accountbook.data.KeyValue.TITLE;

/**
 * Created by Leixiansheng on 2017/9/27.
 */

public class ChartsActivity extends AppCompatActivity implements View.OnClickListener{

    private LineChartView chart;
    private LineChartData data;
    private Button queryYear;
    private Button queryMonth;
    private ImageButton back;
    private TextView dateTitle;

    private Map<String, Float> tableMoney = new TreeMap<>();
    private Map<String, String> tableDate = new TreeMap<>();
    private Map<String, String> tableTitle = new TreeMap<>();
    private List<Map<String, String>> costList;

    private Calendar localCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_charts);

        initView();
        setOnclickListener();
        intData(); //默认查询时间
        getValues(costList,false);
        getData();
    }

    private void setOnclickListener() {
        chart.setOnValueTouchListener(new ValueTouchListener());
        queryYear.setOnClickListener(this);
        queryMonth.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void initView() {
        chart = (LineChartView) findViewById(R.id.chart);
        queryYear = (Button) findViewById(R.id.query_year);
        queryMonth = (Button) findViewById(R.id.query_month);
        back = (ImageButton) findViewById(R.id.back);
        dateTitle = (TextView) findViewById(R.id.date_tv);
    }

    private void intData() {
        int year = localCalendar.get(Calendar.YEAR);
        int month = localCalendar.get(Calendar.MONTH) + 1;
        dateTitle.setText(year + "-" + month);
        costList = AccountTool.queryAllDB(SORT_DATE, true, year, month);
    }

    /**
     * 图标属性设置
     */
    private void getData() {
        List<Line> lines = new ArrayList<>();//线条集合
        List<PointValue> values = new ArrayList<>();//数据点集合
        List<AxisValue> axisValues = new ArrayList<>();//横坐标数据集合

        LineChartValueFormatter chartValueFormatter = new SimpleLineChartValueFormatter(1);//设置表格小数点显示位数

        int index = 0;
        for (Float value : tableMoney.values()) {
//            Log.i("TAG", String.valueOf(value));
            values.add(new PointValue(index, value));
            index++;
        }
        int index2 = 0;
        for (String value : tableDate.values()) {
            axisValues.add(new AxisValue(index2).setLabel(value));
            index2++;
        }

        Line line = new Line(values);//线条设置
        line.setFormatter(chartValueFormatter); //设置表格小数点显示位数
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(ValueShape.CIRCLE); //节点的形状
        line.setHasLabels(true); //坐标轴是否有备注
        line.setCubic(false);//曲线是否平滑
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabelsOnlyForSelected(false); //标签是否只能选中
        line.setHasLines(true); //是否显示折线
        line.setHasPoints(true); //是否显示节点
        line.setPointColor(ChartUtils.COLORS[1]);
        lines.add(line);

        Axis axisX = new Axis();//X坐标设置
        Axis axisY = new Axis().setHasLines(true);//Y坐标
        axisX.setName(getString(R.string.date));
//        axisY.setName(getString(R.string.money));
        axisX.setValues(axisValues);//设置横坐标值

        data = new LineChartData(lines);//数据设置
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        chart.setLineChartData(data);//数据添加到表格
    }

    /**
     * 获取数据
     *
     * @param costList
     */
    private void getValues(List<Map<String, String>> costList, boolean onlyYear) {
        if (costList != null) {
            tableMoney.clear();
            tableDate.clear();
            tableTitle.clear();
            for (int i = 0; i < costList.size(); i++) {
                //将costDate  2017.9.1 修改为 2017.09.01 格式
                String costDate = costList.get(i).get(DATE);
                String costTitle = costList.get(i).get(TITLE);
                Float costMoney = Float.valueOf(costList.get(i).get(MONEY));

                String[] date = costDate.split("-");
                String year = date[0];
                String month = date[1];
                String day = date[2];
                if (month.length() < 2) {
                    month = "0" + month;
                }
                if (day.length() < 2) {
                    day = "0" + day;
                }

                if (onlyYear) {
                    costDate = year + "-" + month;
                } else {
                    costDate = year + "-" + month + "-" + day;
                }

                if (!tableMoney.containsKey(costDate)) {
                    tableMoney.put(costDate, costMoney);
                    if (onlyYear) {
                        tableDate.put(costDate, month);
                    } else {
                        tableDate.put(costDate, day);
                        tableTitle.put(costDate, costTitle);
                    }
                } else {
                    Float originMoney = tableMoney.get(costDate);
                    tableMoney.put(costDate, originMoney + costMoney);
                    String title = tableTitle.get(costDate);
                    tableTitle.put(costDate, (title + "、" + costTitle));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.query_year:
                showDatePicker(true);//只显示 年
                break;
            case R.id.query_month:
                showDatePicker(false);
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 显示时间选择框
     * @param onlyYear
     */
    private void showDatePicker(final boolean onlyYear) {
        MonDatePickerDialog monDatePickerDialog = new MonDatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new MonDatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        if (onlyYear) {
                            dateTitle.setText(year + getString(R.string.query_year));
                            getValues(AccountTool.queryAllDB(SORT_DATE, true, year, 0),true);
                            getData();
                        } else {
                            dateTitle.setText(year + "-" + (monthOfYear+1));
                            getValues(AccountTool.queryAllDB(SORT_DATE, true, year, monthOfYear+1),false);
                            getData();
                        }
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
        if (onlyYear) {
            ((ViewGroup)((ViewGroup) monDatePickerDialog.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
        }
    }

    /**
     * 点击响应
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            List<String> costTitle = new ArrayList<>();
            for (String title : tableTitle.values()) {
                costTitle.add(title);
            }
//            Toast.makeText(ChartsActivity.this, "金额："+value.getY()+"元", Toast.LENGTH_SHORT).show();
            Toast.makeText(ChartsActivity.this, costTitle.get((int) value.getX()), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }
    }
}
