package com.example.screamtime;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ScreamTimeWidgetConfigure extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screamtime_widget_configuration);
        setResult(RESULT_CANCELED);
        Button configScreamTime = (Button) findViewById(R.id.setup_screamtime);
        configScreamTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storePreferences();
                showAppWidget();
            }
        });


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);

    }

    private void storePreferences() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        EditText warnUnlocksET = findViewById(R.id.warnung_unlocks);
        int warnUnlocks = Integer.parseInt(warnUnlocksET.getText().toString());
        editor.putInt("warnUnlocks", warnUnlocks);

        EditText errorUnlocksET = findViewById(R.id.error_unlocks);
        int errorUnlocks = Integer.parseInt(errorUnlocksET.getText().toString());
        editor.putInt("errorUnlocks", errorUnlocks);

        EditText warnScreentimeET = findViewById(R.id.warnung_screentime);
        long warnScreentime = Utils.formatToSingleDuration(warnScreentimeET.getText().toString());
        editor.putLong("warnScreentime", warnScreentime);

        EditText errorScreentimeET = findViewById(R.id.error_screentime);
        long errorScreentime = Utils.formatToSingleDuration(errorScreentimeET.getText().toString());
        editor.putLong("errorScreentime", errorScreentime);

        EditText filterET = findViewById(R.id.filter);
        String filter = filterET.getText().toString();
        editor.putString("filter", filter);

        editor.apply();
    }


    private void showAppWidget() {
        int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
            }

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }

    }
}
