package com.example.parth.evetor;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();


        int position = intent.getIntExtra("position", 0);

        String pos = String.valueOf(position);
        ArrayList<HashMap<String,String>> eventList = (ArrayList<HashMap<String,String>>) intent.getSerializableExtra("eventList");
        String n = eventList.get(position).get("name");
        getSupportActionBar().setTitle(n);
        setContentView(R.layout.activity_third);

        HashMap<String, String>  found = eventList.get(position);

        //receives the values of eventList from an Intent
        String m = eventList.get(position).get("org");
        String description = eventList.get(position).get("description");
        String address = eventList.get(position).get("orgAddress");
        String catString = eventList.get(position).get("categoryString");
        String date1 = eventList.get(position).get("eventdate");
        String price = eventList.get(position).get("costAdult");
        String website = eventList.get(position).get("website");


        //sets the fetched eventList values on to the event detail page

        TextView tx = (TextView) findViewById(R.id.textView2);
        TextView desc = (TextView) findViewById(R.id.textView4);
        TextView orgAddress = (TextView) findViewById(R.id.textView6);
        TextView date = (TextView) findViewById(R.id.textView9);
        TextView categoryString = (TextView) findViewById(R.id.textView8);
        TextView cost = (TextView) findViewById(R.id.textView12);
        TextView hyperlink = (TextView) findViewById(R.id.textView14);

        tx.setText(m);
        desc.setText(description);
        orgAddress.setText(address);
        categoryString.setText(catString);
        date.setText(date1);
        cost.setText(price);
        hyperlink.setText(website);

    }
}
