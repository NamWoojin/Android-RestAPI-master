package com.example.android_resapi.ui.apicall;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.android_resapi.R;
import com.example.android_resapi.httpconnection.GetRequest;
import com.example.android_resapi.ui.DeviceActivity;

public class GetThings extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetThings(Activity activity, String urlStr) {

        super(activity);
        this.urlStr = urlStr;
    }

    @Override
    protected void onPreExecute() {
        try {
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            activity.finish();
            e.printStackTrace();
        }
        TextView message = activity.findViewById(R.id.message);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        TextView message = activity.findViewById(R.id.message);
        if (jsonString == null) {
            message.setText("디바이스 없음");
            return;
        }
        message.setText("");
        ArrayList<Thing> arrayList = getArrayListFromJSONString(jsonString);

        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.txtList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
        txtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Thing thing = (Thing)adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(activity, DeviceActivity.class );
                intent.putExtra("thingShadowURL", urlStr+"/"+thing.name);
                activity.startActivity(intent);
            }
        });
    }

    protected ArrayList<Thing> getArrayListFromJSONString(String jsonString) {
        ArrayList<Thing> output = new ArrayList();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("things");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                Thing thing = new Thing(jsonObject.getString("thingName"),
                        jsonObject.getString("thingArn"));

                output.add(thing);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    class Thing {
        String name;
        String arn;
        HashMap<String, String> tags;

        public Thing(String name, String arn) {
            this.name = name;
            this.arn = arn;
            tags = new HashMap<String, String>();
        }

        public String toString() {
            return String.format("이름 = %s \nARN = %s \n", name, arn);
        }


    }
}

