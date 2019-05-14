package com.example.Wireless_AC_Energy_Meter.Activity.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.Wireless_AC_Energy_Meter.Data.Day;
import com.example.Wireless_AC_Energy_Meter.Data.Month;
import com.example.Wireless_AC_Energy_Meter.Data.Period;
import com.example.Wireless_AC_Energy_Meter.Data.State;
import com.example.Wireless_AC_Energy_Meter.Helper.SQLiteHandler;
import com.example.Wireless_AC_Energy_Meter.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StatisticFragment extends Fragment {

    private BarChart barChart;
    private PieChart piechart;
    private HorizontalBarChart horizontalChart;
    private SQLiteHandler db;

    Spinner spinner;

    ArrayList<Day> days;
    ArrayList<Period> periods;
    ArrayList<Month> months;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        spinner = view.findViewById(R.id.spinner);
        barChart = view.findViewById(R.id.barChart);
        piechart = view.findViewById(R.id.pieChart);
        horizontalChart = view.findViewById(R.id.horizontalChart);

        //create spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterdayDate = dateFormat.format(cal.getTime()); //your formatted date here
        setOnItemSelectedListener(yesterdayDate);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        days = db.getDaysDetails();
        periods = db.getPeriodsDetails();
        months = db.getMonthsDetails();


        return view;
    }

    private void showDaysBarChart(String type) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        float i = 0;
        for (Day day : days) {
            switch (type) {
                case "Price":
                    barEntries.add(new BarEntry(i++, (float) (day.getEnergy() * State.STATE_PRICE)));
                    break;
                case "Energy":
                    barEntries.add(new BarEntry(i++, (float) day.getEnergy()));
                    break;
                case "Power":
                    barEntries.add(new BarEntry(i++, (float) day.getPower()));
                    break;
                case "Current":
                    barEntries.add(new BarEntry(i++, (float) day.getCurrent()));
                    break;
                case "Voltage":
                    barEntries.add(new BarEntry(i++, (float) day.getVoltage()));
                    break;
                default:
                    break;
            }
        }
        BarDataSet set = new BarDataSet(barEntries, "Days");
        set.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData data = new BarData(set);
        //data.setBarWidth(0.5f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.animateXY(1000, 1000);
        barChart.setScaleYEnabled(false);
        barChart.setOnChartValueSelectedListener(onChartValueSelectedListener);
        XAxis xAxis = barChart.getXAxis();
        //to show day in x axis
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return days.get((int) value).getDay();
            }
        });
        YAxis yLAxis = barChart.getAxisLeft();
        YAxis yRAxis = barChart.getAxisRight();
        yLAxis.setAxisMinimum(0f);
        yRAxis.setAxisMinimum(0f);
        yRAxis.setEnabled(false);
        xAxis.setLabelRotationAngle(-90f);
        //xAxis.setLabelCount(yValuesEnergy.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.fitScreen();
        barChart.invalidate();
    }

    private void showPeriodsPieChart(String selectedDay, String type) {
        piechart.clear();
        piechart.setUsePercentValues(false);
        piechart.getDescription().setEnabled(false);
        piechart.setExtraOffsets(5, 10, 5, 5);
        piechart.setDragDecelerationFrictionCoef(0.95f);
        piechart.setDrawHoleEnabled(true);
        piechart.setHoleColor(Color.WHITE);
        piechart.setTransparentCircleRadius(61f);
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        //Night Morning Afternoon Evening
        //  0       1       2       3
        for (Period period : periods) {
            if (period.getDay().equals(selectedDay))
                switch (type) {
                    case "Price":
                        pieEntries.add(new PieEntry((float) (period.getEnergy() * State.STATE_PRICE), period.getPeriod()));
                        break;
                    case "Energy":
                        pieEntries.add(new PieEntry((float) period.getEnergy(), period.getPeriod()));
                        break;
                    case "Power":
                        pieEntries.add(new PieEntry((float) period.getPower(), period.getPeriod()));
                        break;
                    case "Current":
                        pieEntries.add(new PieEntry((float) period.getCurrent(), period.getPeriod()));
                        break;
                    case "Voltage":
                        pieEntries.add(new PieEntry((float) period.getVoltage(), period.getPeriod()));
                        break;
                    default:
                        break;
                }

        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "Period");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        // Setting Data
        PieData pieData = new PieData(dataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        piechart.setData(pieData);
        piechart.setDrawEntryLabels(true);
        piechart.invalidate();

    }

    private void showMonthsHorizontalChart(String type) {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        float i = 0;
        for (Month month : months) {
            switch (type) {
                case "Price":
                    barEntries.add(new BarEntry(i++, (float) (month.getEnergy() * State.STATE_PRICE)));
                    break;
                case "Energy":
                    barEntries.add(new BarEntry(i++, (float) month.getEnergy()));
                    break;
                case "Power":
                    barEntries.add(new BarEntry(i++, (float) month.getPower()));
                    break;
                case "Current":
                    barEntries.add(new BarEntry(i++, (float) month.getCurrent()));
                    break;
                case "Voltage":
                    barEntries.add(new BarEntry(i++, (float) month.getVoltage()));
                    break;
                default:
                    break;
            }
        }
        BarDataSet set = new BarDataSet(barEntries, "Months");
        set.setColors(ColorTemplate.JOYFUL_COLORS);
        BarData data = new BarData(set);
        horizontalChart.setData(data);
        horizontalChart.setScaleXEnabled(false);
        YAxis yRAxis = horizontalChart.getAxisRight();
        YAxis yLAxis = horizontalChart.getAxisLeft();
        yLAxis.setAxisMinimum(0f);
        yRAxis.setEnabled(false);
        XAxis xAxis = horizontalChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String[] month = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                return month[Integer.valueOf(months.get((int) value).getMonth()) - 1];
            }
        });
        xAxis.setLabelCount(barEntries.size());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalChart.invalidate();
    }

    private OnChartValueSelectedListener onChartValueSelectedListener = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            String selectedDay = days.get((int) e.getX()).getDay();
            Toast.makeText(getActivity(), selectedDay, Toast.LENGTH_SHORT).show();
            showPeriodsPieChart(selectedDay, spinner.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected() {

        }
    };

    private void setOnItemSelectedListener(final String date) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), type, Toast.LENGTH_SHORT).show();
                showDaysBarChart(type);
                showPeriodsPieChart(date, type);
                showMonthsHorizontalChart(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}

