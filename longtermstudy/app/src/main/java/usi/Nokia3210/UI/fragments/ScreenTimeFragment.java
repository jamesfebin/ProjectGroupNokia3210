package usi.Nokia3210.UI.fragments;
import usi.Nokia3210.MyApplication;
import usi.Nokia3210.gathering.gatheringServices.ApplicationLogs.CustomUsageStats;
import usi.Nokia3210.local.database.tables.WiFiTable;
import usi.Nokia3210.remote.database.upload.AlarmService;
import usi.Nokia3210.remote.database.upload.UploadAlarmReceiver;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import java.util.concurrent.TimeUnit;
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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.app.Application;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
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
    private Context context;
    private PackageManager pm;
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

    private void addDataSet(){



        //List<PieEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<UsageStats> stats =getUsageStatistics(UsageStatsManager.INTERVAL_DAILY);

        // sort stats for TotalTimeInForeground (biggest to smallest)
//        stats.sort(Comparator.naturalOrder());

        ArrayList<PieEntry> entries = new ArrayList<>();

        int count=10;

        for (int i=0;i<stats.size();i++)
        {
            // stai facendo una sola entri devi fare la entri n volte tante quante le app che usi
            // serve indicie nel for
            //float hstats =  (stats.get(i).getTotalTimeInForeground()/3600000);

            if (getDurationBreakdown(stats.get(i).getTotalTimeInForeground())>10) {

              entries.add(new PieEntry(getDurationBreakdown(stats.get(i).getTotalTimeInForeground()), getApplicationName(getContext(),stats.get(i).getPackageName())));
//                entries.add(new PieEntry(stats.get(i).getTotalTimeInForeground(), stats.get(i).getPackageName()));
                    // to get name only
//                    String[] appname = stats.get(i).getPackageName().split(".");
//                    entries.add(new PieEntry(stats.get(i).getTotalTimeInForeground(), appname[appname.length - 1]));

            }

          //  Log.d("ScreenFrag", s.getPackageName());
        }




        // create data set
        PieDataSet dataset = new PieDataSet(entries, "Applications usage");
        dataset.setSliceSpace(3);
        dataset.setSelectionShift(5);
        dataset.setValueTextSize(15);
       // dataset.setValueFormatter(new PercentFormatter());
        // add colors
        // ArrayList<Integer>colors= new ArrayList<>();
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);


        // add legend to chart -> does that need to be in xml? is xml complete?
        //Legend l = pieChart.getLegend();
        
        //l.setPosition(LegendPosition.BELOW_CHART_LEFT);

        //l.setXEntrySpace(7);
        //l.setYEntrySpace(5);
        pieChart.getLegend().setEnabled(false);

        //animate
        pieChart.animateY(1500);

        // create pie data object
        PieData data = new PieData(dataset);

        //enable hole and configure
        pieChart.setDrawEntryLabels(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setHoleRadius(40);
        pieChart.setTransparentCircleRadius(10);

        pieChart.setRotationAngle(1);
        pieChart.setRotationEnabled(true);



       // pieChart.setUsePercentValues(true);
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

//        boolean checking = queryUsageStats.isEmpty();
//
//        Log.d("ScreenFrag2", checking + "");

        for (int i = 0; i < queryUsageStats.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            customUsageStats.usageStats = queryUsageStats.get(i);

            ApplicationLogData appData = new ApplicationLogData(true);
            appData.setTotalTimeInForeground(customUsageStats.usageStats.getTotalTimeInForeground());
            appData.setAppPackage(customUsageStats.usageStats.getPackageName());
        }

        return queryUsageStats;
    }



    private String getApplicationName(Context context, String data) {

        final PackageManager pckManager = context.getPackageManager();
        ApplicationInfo applicationInformation;
        try {
            applicationInformation = pckManager.getApplicationInfo(data, 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInformation = null;
        }

        String title = (String) (applicationInformation != null ? pckManager.getApplicationLabel(applicationInformation) : "(unknown)");
        return title;

    }





    public static long getDurationBreakdown(long millis) {
        if(millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);


        return(minutes);
    }


}

