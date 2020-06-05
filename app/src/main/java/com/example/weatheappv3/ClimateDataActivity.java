package com.example.weatheappv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClimateDataActivity extends AppCompatActivity {

    private  RecyclerView recyclerView;
    private  ArrayList<ClimateDataList> arrayList;


    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate_data);

        test = findViewById(R.id.tvid);


        recyclerView = findViewById(R.id.clrclv1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();


        String obcityid = hasID();
        getWeatherStats(obcityid);








    }

    public String hasID(){
        String cityID = null;
        if (getIntent().hasExtra("ID")){

             cityID = getIntent().getStringExtra("ID");



        }

        return  cityID;
    }


    public  void getWeatherStats(final String cityid){

        String wurl = "https://api.meteostat.net/v1/climate/normals?station=".concat(cityid).concat("&key=wMzoCfs8");
        Log.i("final stat url", wurl);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, wurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject jsonObject = response.getJSONObject("data");


                    JSONObject temp = jsonObject.getJSONObject("temperature");
                    JSONObject rain = jsonObject.getJSONObject("precipitation");
                    JSONObject pressure = jsonObject.getJSONObject("pressure");

                    for (int i = 202001; i <= 202012; i++) {

                        String month = String.valueOf(i).concat("01");


                    //String startmonth = "20200101";

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

                    try {
                        Date date = simpleDateFormat.parse(month);
                        String monthString = (String) DateFormat.format("MMM", date); // Jan
                        monthString = monthString.toUpperCase();

                        String monthlytemp = temp.getString(monthString);
                        String monthlyrain = rain.getString(monthString);
                        String monthlypress = pressure.getString(monthString);

                        arrayList.add(new ClimateDataList(monthString, monthlytemp.concat(" C"), monthlyrain.concat(" mm"), monthlypress.concat(" hPa")));
                        ClimateAdapter climateAdapter = new ClimateAdapter(arrayList, getApplicationContext());
                        recyclerView.setAdapter(climateAdapter);
                        test.setText(cityid);

                        Log.i("temp: ", monthString);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


       // return list;
    }
}
