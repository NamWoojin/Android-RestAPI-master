package com.example.android_resapi.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android_resapi.R;
import com.example.android_resapi.ui.apicall.GetLog;

public class LogActivity extends AppCompatActivity {
    String getLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_log);

        Intent intent = getIntent();
        getLogsURL = intent.getStringExtra("getLogsURL");
        Log.i(TAG, "getLogsURL="+getLogsURL);

        Button startDateBtn = findViewById(R.id.start_date_button);
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date1 = (TextView)findViewById(R.id.textView_date1);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2019, 12, 0);

                dialog.show();


            }
        });

        Button startTimeBtn = findViewById(R.id.start_time_button);
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time1 = (TextView)findViewById(R.id.textView_time1);
                        textView_Time1.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();

            }
        });


        Button endDateBtn = findViewById(R.id.end_date_button);
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        textView_Date2 = (TextView)findViewById(R.id.textView_date2);
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth));
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(LogActivity.this, callbackMethod, 2019, 12, 0);

                dialog.show();


            }
        });

        Button endTimeBtn = findViewById(R.id.end_time_button);
        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        TextView textView_Time2 = (TextView)findViewById(R.id.textView_time2);
                        textView_Time2.setText(String.format("%d:%d", hourOfDay, minute));
                    }
                };

                TimePickerDialog dialog = new TimePickerDialog(LogActivity.this, listener, 0, 0, false);
                dialog.show();

            }
        });

        Button start = findViewById(R.id.log_start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetLog(LogActivity.this,getLogsURL).execute();
            }
        });
    }
}
