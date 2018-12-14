package usi.Nokia3210.UI.fragments;
import usi.Nokia3210.gathering.gatheringServices.ApplicationLogs.CustomUsageStats;
import usi.Nokia3210.local.database.tables.WiFiTable;
import usi.Nokia3210.remote.database.upload.AlarmService;
import usi.Nokia3210.remote.database.upload.UploadAlarmReceiver;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
//this is the pakcage to be imported i don't have it
//import com.xxmassdeveloper.mpchartexample.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.RelativeLayout;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import usi.Nokia3210.R;
import usi.Nokia3210.UI.views.HomeView;
import usi.Nokia3210.UI.views.RegistrationView;
import usi.Nokia3210.local.database.controllers.LocalStorageController;
import usi.Nokia3210.local.database.controllers.SQLiteController;
import usi.Nokia3210.local.database.db.LocalSQLiteDBHelper;
import usi.Nokia3210.local.database.tableHandlers.ApplicationLogData;
import usi.Nokia3210.local.database.tables.ApplicationLogsTable;
import usi.Nokia3210.local.database.tables.UserTable;
import usi.Nokia3210.remote.database.controllers.SwitchDriveController;
public class ScreenTimeFragment extends Fragment {

    private LocalStorageController localController;
    SwitchDriveController switchDriveController;
    String androidID;
    private RegistrationView registrationView;
    private HomeView homeView;
    PieChart pieChart;
    UsageStatsManager mUsageStatsManager;


    public ScreenTimeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        localController = SQLiteController.getInstance(getContext());
        View root = inflater.inflate(R.layout.fragment_screentime, null);
        mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);

        pieChart = root.findViewById(R.id.pieChartView);

        addDataSet();

        return root;
    }

    private void addDataSet() {
        //List<PieEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<UsageStats> stats =getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);


        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(4f, 0));
        entries.add(new PieEntry(8f, 1));
        entries.add(new PieEntry(6f, 2));
        entries.add(new PieEntry(12f, 3));
        entries.add(new PieEntry(18f, 4));
        entries.add(new PieEntry(9f, 5));


       // for (UsageStats s : stats)
       // {
            // stai facendo una sola entri devi fare la entri n volte tante quante le app che usi
            // serve indicie nel for
       //     entries.add(new PieEntry(s.getTotalTimeInForeground()));
        //    labels.add(s.getPackageName());
         //   Log.d("ScreenFrag", s.getPackageName());
       // }


        // create data set
        PieDataSet dataset = new PieDataSet(entries, "Applications");
        dataset.setSliceSpace(2);
        dataset.setValueTextSize(12);

        // add colors
        // ArrayList<Integer>colors= new ArrayList<>();
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        // add legend to chart -> does that need to be in xml? is xml complete?
        //pieChart.getLegend().setEnabled(false);


        //we get error here
        // create pie data object
        PieData data = new PieData(dataset);
        data.setValueFormatter(new PercentFormatter());
        //set value
        //set volor
        pieChart.setData(data);
        pieChart.invalidate();
    }


    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DAY_OF_MONTH, -1);
        cal1.add(Calendar.HOUR, 23);
        cal1.add(Calendar.MINUTE, 0);


        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DAY_OF_MONTH, 0);
        cal2.add(Calendar.HOUR, 23);
        cal2.add(Calendar.MINUTE, 0);

        long start = cal1.getTimeInMillis();
        long end = cal2.getTimeInMillis();

        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(intervalType, start, end);

        boolean checking = queryUsageStats.isEmpty();

        Log.d("ScreenFrag2", checking + "");

        for (int i = 0; i < queryUsageStats.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = queryUsageStats.get(i);

            ApplicationLogData appData = new ApplicationLogData(true);
            appData.setAppPackage(customUsageStats.usageStats.getPackageName());
            appData.setTotalTimeInForeground(customUsageStats.usageStats.getTotalTimeInForeground());
        }

        return queryUsageStats;
    }

}