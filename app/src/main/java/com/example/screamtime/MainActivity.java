package com.example.screamtime;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //TODO: remove
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Pair<Integer, Map<String, Long>> usageData = Utils.filterUsageData(getApplicationContext());

        TextView tf_Data = (TextView) findViewById(R.id.tf_test);

        tf_Data.setText("Unlocks: " + usageData.first + "\n");
        for(Map.Entry<String, Long> appData : usageData.second.entrySet())
        {
            tf_Data.append(Utils.formatSingleDuration(appData.getValue()) + " by " + appData.getKey());
            tf_Data.append("\n");
        }
        tf_Data.append("Total: " + Utils.formatSingleDuration(Utils.getTotalDuration(usageData.second)));
        tf_Data.append("\n");
        tf_Data.append("Top App: " + Utils.getTopApp(usageData.second));
    }


}