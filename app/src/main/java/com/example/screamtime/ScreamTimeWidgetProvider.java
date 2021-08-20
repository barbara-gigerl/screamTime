package com.example.screamtime;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class ScreamTimeWidgetProvider extends AppWidgetProvider {
    private PendingIntent pendingIntent;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.i("ScreamTime", "ScreamTime in update");
        Pair<Integer, Map<String, Long>> usageData = Utils.filterUsageData(context);
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = null;
        String topApp = Utils.getTopApp(usageData.second).first;
        try {
            ai = pm.getApplicationInfo( topApp, 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String topAppName = (String) (ai != null ? pm.getApplicationLabel(ai) : topApp);
        int numUnlocks = usageData.first;
        String totalScreentime = Utils.formatSingleDuration(Utils.getTotalDuration(usageData.second));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        int warnUnlocks = sharedPrefs.getInt("warnUnlocks", 15);
        int errorUnlocks = sharedPrefs.getInt("errorUnlocks", 30);

        long warnScreentime = sharedPrefs.getLong("warnScreentime", 0);

        long errorScreentime = sharedPrefs.getLong("errorScreentime", 0);


        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.screamtime_widget_provider);

            views.setTextViewText(R.id.unlocks, "Entsperrt: " + numUnlocks + " Mal");
            if(numUnlocks >= warnUnlocks)
            {
                views.setTextColor(R.id.unlocks, context.getColor(R.color.warn));
            }

            if (numUnlocks >= errorUnlocks)
            {
                views.setTextColor(R.id.unlocks, context.getColor(R.color.err));
            }
            views.setTextViewText(R.id.total_screen_time, "Gesamte Bildschirmzeit: " + totalScreentime);

            if(Utils.getTotalDuration(usageData.second)  >= warnScreentime ) {
                views.setTextColor(R.id.unlocks, context.getColor(R.color.warn));
            }

            if(Utils.getTotalDuration(usageData.second)  >= errorScreentime ) {
                views.setTextColor(R.id.unlocks, context.getColor(R.color.err));
            }

            views.setTextViewText(R.id.top_app, "Meist genutzt: " + topAppName);
            views.setTextViewText(R.id.last_updated, "Zuletzt geupdated: " + new SimpleDateFormat("dd.MM.YYYY HH:mm:ss").format(new Date()));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }


    }

}
