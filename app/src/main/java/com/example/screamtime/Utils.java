package com.example.screamtime;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static Pair<Integer, Map<String,Long>> filterUsageData(Context context) {
        /*HashMap<Integer, String> dbg = new HashMap<>();
        dbg.put(2, "ACTIVITY_PAUSED");
        dbg.put(1, "ACTIVITY_RESUMED");
        dbg.put(23, "ACTIVITY_STOPPED");
        dbg.put(5, "CONFIGURATION_CHANGE");
        dbg.put(26, "DEVICE_SHUTDOWN");
        dbg.put(27, "DEVICE_STARTUP");
        dbg.put(19, "FOREGROUND_SERVICE_START");
        dbg.put(20, "FOREGROUND_SERVICE_STOP");
        dbg.put(18, "KEYGUARD_HIDDEN");
        dbg.put(17, "KEYGUARD_SHOWN");
        dbg.put(2, "MOVE_TO_BACKGROUND");
        dbg.put(1, "MOVE_TO_FOREGROUND");
        dbg.put(15, "SCREEN_INTERACTIVE");
        dbg.put(16, "SCREEN_NON_INTERACTIVE");
        dbg.put(8, "SHORTCUT_INVOCATION");
        dbg.put(11, "STANDBY_BUCKET_CHANGED");
        dbg.put(7, "USER_INTERACTION");*/



        UsageEvents.Event event = new UsageEvents.Event();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String filterName = sharedPrefs.getString("filter", "");



        if(usageStatsManager != null) {

            Calendar todayMidnight = Calendar.getInstance();
            todayMidnight.set(Calendar.HOUR_OF_DAY, 0);
            todayMidnight.set(Calendar.MINUTE, 0);
            todayMidnight.set(Calendar.SECOND, 0);
            todayMidnight.set(Calendar.MILLISECOND, 0);


            long start = todayMidnight.getTimeInMillis();
            long end = System.currentTimeMillis();

            UsageEvents usageEvents = usageStatsManager.queryEvents(start, end);
            int unlockCnt = 0;
            Map<String, Long> usageTimes = new HashMap<String, Long>();
            Map<String, Long> currentStartTime = new HashMap<String, Long>();
            while (usageEvents.hasNextEvent())
            {
                usageEvents.getNextEvent(event);

                String packageName = event.getPackageName();
                if(packageName.contains(filterName))
                    continue;

                int eventType = event.getEventType();
                long eventTimestamp = event.getTimeStamp();


                if (eventType == UsageEvents.Event.KEYGUARD_HIDDEN) {
                    unlockCnt++;
                }

                else if (eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentStartTime.put(packageName, eventTimestamp);

                } else if (eventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                    usageTimes.putIfAbsent(packageName, 0L);
                    if (currentStartTime.containsKey(packageName)) {
                        long duration = eventTimestamp - currentStartTime.get(packageName);
                        usageTimes.put(packageName, usageTimes.get(packageName) + duration);
                        currentStartTime.remove(packageName);
                    }
                }
            }
            return new Pair<Integer, Map<String,Long>>(unlockCnt, usageTimes);
        }
        return null;
    }

    public static String formatSingleDuration(long timestamp)
    {
        long hours =  ((timestamp / (1000*60*60)) % 24);
        long minutes =  ((timestamp / (1000*60)) % 60);
        long seconds =  (timestamp / 1000) % 60 ;

        String formattedDuration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return formattedDuration;
    }

    public static long formatToSingleDuration(String strDuration) {
        String[] arr = strDuration.split(":");
        Duration duration = Duration.ZERO;
        strDuration = "PT" + arr[0] + "M" + arr[1] + "S";
        duration = Duration.parse(strDuration);
        return duration.toMillis();

    }


    public static long getTotalDuration(Map<String, Long> usageTimes)
    {
        long totalDuration = 0L;
        for(Map.Entry<String, Long> usageTime: usageTimes.entrySet())
        {
            totalDuration += usageTime.getValue();
        }
        return totalDuration;
    }

    public static Pair<String,Long> getTopApp(Map<String, Long> usageTimes)
    {
        String topAppName = "";
        long topAppTime = 0L;

        for(Map.Entry<String,Long> usageTime : usageTimes.entrySet())
        {
            if(usageTime.getValue() > topAppTime)
            {
                topAppName = usageTime.getKey();
                topAppTime = usageTime.getValue();
            }
        }


        return new Pair(topAppName, topAppTime);
    }
}
