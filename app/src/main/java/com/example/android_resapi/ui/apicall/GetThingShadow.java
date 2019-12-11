package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;

public class GetThingShadow extends GetRequest {
    public static String temperature;
    final static String TAG = "AndroidAPITest";
    String urlStr;

    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;
        Map<String, String> state = getStateFromJSONString(jsonString);
        //TextView reported_ledTV = activity.findViewById(R.id.reported_led);
        TextView reported_tempTV = activity.findViewById(R.id.temperature);
        temperature = state.get("reported_temperature");
        reported_tempTV.setText(temperature);

        TextView reported_modeTV = activity.findViewById(R.id.motor_step_id);
        String mode = state.get("reported_step");
        reported_modeTV.setText(mode.concat("단계"));
        //reported_ledTV.setText(state.get("reported_LED"));

        //TextView desired_stepTV = activity.findViewById(R.id.desired_);
        //TextView desired_tempTV = activity.findViewById(R.id.desired_temp);
        //desired_tempTV.setText(state.get("desired_temperature"));
        //reported_modeTV.setText(state.get("desired_step"));

    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String tempValue = reported.getString("temperature");
            String stepValue = reported.getString("motor_step");
            output.put("reported_temperature", tempValue);
            output.put("reported_step",stepValue);

//            JSONObject desired = state.getJSONObject("desired");
//            String desired_tempValue = desired.getString("temperature")
//            String desired_stepValue = desired.getString("motor_step");
//            output.put("desired_temperature", desired_tempValue);
//            output.put("desired_step",desired_stepValue);

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}
