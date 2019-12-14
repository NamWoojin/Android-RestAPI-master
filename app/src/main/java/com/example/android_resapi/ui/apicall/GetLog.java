package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;
import com.example.android_resapi.ui.MyXAxisValueFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

public class GetLog extends GetRequest {
    private LineChart lineChart;
    private PieChart pieChart;
    final static String TAG = "AndroidAPITest";
    String urlStr;
    String mode;
    public GetLog(Activity activity, String urlStr,String mode) {
        super(activity);
        this.urlStr = urlStr;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        try {

            TextView textView_Date1 = activity.findViewById(R.id.textView_date1);
            TextView textView_Date2 = activity.findViewById(R.id.textView_date2);

            String params = String.format("?from=%s:00&to=%s:00",textView_Date1.getText().toString()+"00:00",
                    textView_Date2.getText().toString()+"23:59");

            Log.i(TAG,"urlStr="+urlStr+params);
            url = new URL(urlStr+params);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        TextView message = activity.findViewById(R.id.message2);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {

        lineChart = (LineChart)activity.findViewById(R.id.chart);
        pieChart = (PieChart)activity.findViewById((R.id.piechart));
        TextView message = activity.findViewById(R.id.message2);
        if (jsonString == null) {
            message.setText("로그 없음");
            return;
        }
        message.setText("");
        getArrayListFromJSONString(jsonString);


    }

    protected void getArrayListFromJSONString(String jsonString) {
        List<Entry> entries = new ArrayList<>();

        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");

            Log.i(TAG, "jsonString="+jsonString);

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            if(mode.equals("temp")){
                lineChart.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);


                int num = jsonArray.length() / 30;

                String[] labels = new String[num];
                for (int i = 0; i < num; i++) {
                    JSONObject jsonObject = (JSONObject)jsonArray.get(i*30);
                    entries.add(new Entry(i, Float.parseFloat(jsonObject.getString("temperature"))));

                    labels[i] = jsonObject.getString("timestamp");
                }


                LineDataSet dataset = new LineDataSet(entries, "temperature");
                dataset.setLineWidth(2);
                dataset.setCircleRadius(6);
                dataset.setCircleColor(Color.parseColor("#FFFE9C88"));
                //dataset.setCircleColorHole(Color.rgb(245,217,97));
                dataset.setColor(Color.parseColor("#FFFE9C88"));
                //dataset.setDrawCircleHole(true);
                dataset.setDrawCircles(true);
                dataset.setDrawHorizontalHighlightIndicator(false);
                dataset.setDrawHighlightIndicators(false);
                dataset.setDrawValues(true);
                LineData lineData = new LineData(dataset);
                lineChart.setData(lineData);
                lineData.setValueTextSize(10);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setLabelRotationAngle(20);
                xAxis.setLabelCount(jsonArray.length()/30);
                xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));

                YAxis yLAxis = lineChart.getAxisLeft();
                YAxis yRAxis = lineChart.getAxisRight();
                yRAxis.setDrawLabels(false);
                yRAxis.setDrawAxisLine(false);
                yRAxis.setDrawGridLines(false);
                yLAxis.setTextColor(Color.BLACK);

                Description description = new Description();
                description.setText("");

                lineChart.setDoubleTapToZoomEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDescription(description);
                lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
                lineChart.invalidate();
            }

            else if(mode.equals("step")){
                lineChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);

                pieChart.setUsePercentValues(true);
                pieChart.setExtraOffsets(5,10,5,5);
                pieChart.setDrawHoleEnabled(false);
                int countZero = 0;
                int countOne = 0;
                int countTwo=0;
                int countThree=0;
                String led;
                ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = (JSONObject)jsonArray.get(i);
                    led = jsonObject.getString("motor_step");
                    if(led.equals("0"))
                        countZero++;
                    else if(led.equals("1"))
                        countOne++;
                    else if(led.equals("2"))
                        countTwo++;
                    else if(led.equals("3"))
                        countThree++;

                }
                yValues.add(new PieEntry(countZero,"OFF"));
                yValues.add(new PieEntry(countOne,"1단계"));
                yValues.add(new PieEntry(countTwo,"2단계"));
                yValues.add(new PieEntry(countThree,"3단계"));

//                pieChart.animateY(1000,Easing.EasingOption.EaseInOutElastic);

                Description description = new Description();
                description.setText("");
                pieChart.setDescription(description);

                PieDataSet dataSet = new PieDataSet(yValues,"motor_step");
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

                PieData data = new PieData(dataSet);
                data.setValueTextSize(13f);
                data.setValueTextColor(Color.WHITE);

                pieChart.setData(data);
            }




        } catch (JSONException e) {
            //Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }

    }

    class Tag {
        String temperature;
        String LED;
        String timestamp;

        public Tag(String temp, String led, String time) {
            temperature = temp;
            LED = led;
            timestamp = time;
        }

        public String toString() {
            return String.format("[%s] Temperature: %s, LED: %s", timestamp, temperature, LED);
        }
    }
}
