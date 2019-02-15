package com.example.parth.evetor;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.example.parth.evetor.R.layout.activity_second;


public class SecondActivity extends AppCompatActivity {

    private String TAG = SecondActivity.class.getSimpleName();

    ListView lv;

    ArrayList<Event> EventList = new ArrayList<Event>();

    Button button;
    EditText editText;
    ProgressBar progressBar ;
    com.example.parth.evetor.ListAdapter adapter;


    //URL of the JSON array
    private static String url = "http://app.toronto.ca/cc_sr_v1_app/data/edc_eventcal_APR";

    ArrayList<HashMap<String,String>> eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_second);
        init();


        eventList = new ArrayList<>();

    }

    public void init(){

        lv = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.search);
        editText.setVisibility(View.GONE);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                editText.setVisibility(View.VISIBLE);
            }

        });


        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        lv.setTextFilterEnabled(true);

        //Passes position, id and eventList using intent to the ThirdActivity
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.setClass(SecondActivity.this, ThirdActivity.class);
                i.putExtra("position", position);
                i.putExtra("id", id);
                i.putExtra("eventList", eventList);



                startActivity(i);
            }
        });


        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence stringVar, int start, int before, int count) {

                adapter.getFilter().filter(stringVar.toString());
            }
        });

        new ParseJSonDataClass(this).execute();


    }


    private class ParseJSonDataClass extends AsyncTask<Void,Void,Void> {

        public Context context;

        public ParseJSonDataClass(Context context) {

            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        //fetches JSON data and parse it into EventList Array
        @Override
        protected Void doInBackground(Void... voids){

            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if(jsonStr != null){
                try{

                    JSONArray events = new JSONArray(jsonStr);
                    Event eventTemp;

                    //Runs until it reaches the length of the JSON Array and
                    //stores appropriate values into the EventList Array
                    for(int i = 0; i < events.length(); i++){

                        JSONObject c = events.getJSONObject(i);
                        JSONObject eventO = c.getJSONObject("calEvent");
                        String name = eventO.getString("eventName");

                        JSONArray dateArr = eventO.getJSONArray("dates");
                        JSONObject eveDate;
                        Date currentTime = Calendar.getInstance().getTime();
                        String eventdate = null;
                        String sysdate = null;
                        for(int j = 0; j < dateArr.length(); j++) {
                            eveDate = dateArr.getJSONObject(j);
                            String dte = eveDate.getString("startDateTime");

                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date d1 = new Date();
                            Date d2 = new Date();
                            sysdate = formatter.format(currentTime);
                            try {
                                d1 = sdf.parse(sysdate);
                                d2 = sdf.parse(dte.substring(8,10) + "/" + dte.substring(5,7) + "/" + dte.substring(0,4));

                                if(d2.after(d1) || d2.equals(d1)){
                                    eventdate = dte.substring(8,10) + "/" + dte.substring(5,7) + "/" + dte.substring(0,4);
                                    break;
                                }
                                else{
                                    eventdate = "N/A";
                                }
                            } catch (ParseException ex) {
                            }

                        }

                        String org = "";
                        int j = i;
                        if(eventO.has("orgName")) {
                            org = eventO.getString("orgName");
                        }

                        JSONObject cost;
                        String costAdult;

                        if(eventO.has("cost")){
                            cost = eventO.getJSONObject("cost");
                            if(cost.has("adult")) {
                                costAdult = "$" + cost.getString("adult") + " (May vary)";
                            }else{
                                costAdult="N/A";
                            }

                        }else{
                            costAdult="N/A";
                        }

                        String website = eventO.getString("eventWebsite");
                        String description = eventO.getString("description");
                        String orgAddress = eventO.getString("orgAddress");
                        String categoryString = eventO.getString("categoryString");
                        eventTemp = new Event(name, categoryString, eventdate);
                        EventList.add(eventTemp);

                        HashMap<String, String> event = new HashMap<>();


                        event.put("name", name);
                        event.put("org", org);
                        event.put("description" , description);
                        event.put("orgAddress" , orgAddress);
                        event.put("categoryString",categoryString);
                        event.put("eventdate", eventdate);
                        event.put("costAdult",costAdult);
                        event.put("website", website);

                        eventList.add(event);

                    }


                }catch(final JSONException e){
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        public void run(){
                            Toast.makeText(SecondActivity.this, "JSON parsing error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            }else{
                Log.e(TAG, "Could not get the JSON from server ");
                runOnUiThread(new Runnable() {
                    public void run(){
                        Toast.makeText(SecondActivity.this, "Could not get the JSON from server.",
                                Toast.LENGTH_SHORT).show();
                    }
                });


            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            if(progressBar.isShown()){

            }

            progressBar.setVisibility(View.INVISIBLE);


            adapter = new com.example.parth.evetor.ListAdapter(
                    SecondActivity.this, R.layout.list_item, EventList) {
            };

            lv.setAdapter(adapter);

        }


    }
}
