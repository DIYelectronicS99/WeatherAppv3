package com.example.weatheappv3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //public double[] coordinates;

    EditText city;
    Button button;
    TextView textView;

    private RecyclerView recyclerView;
    private ArrayList<RowDatalist> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rclv1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();

        city = findViewById(R.id.et1);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.tvsoil);


        if (city.getText().toString() !=""){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getcitynames(city.getText().toString());

                }
            });
        }


    }

    public void getcitynames(String inputcity) {

        //final int[] citycoor = new int[2];


        String url = "https://api.openweathermap.org/data/2.5/weather?q=".concat(inputcity).concat("&appid=562f305cbac12c6a01b39d7ca75675d0");

        Log.i("final city url: ", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject object = response.getJSONObject("coord");
                    double lat = object.getInt("lat");
                    double lon = object.getInt("lon");


                    getstations(lat,lon);


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

        //return coordinates;


    }

    public void getstations(double lat, double lon) {

        String stationurl = "https://api.meteostat.net/v1/stations/nearby?lat=".concat(String.valueOf(lat)).concat("&lon=").concat(String.valueOf(lon)).concat("&limit=5&key=wMzoCfs8");
        Log.i("weather station url: ", stationurl);
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stationurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i=0; i< jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String viewid = jsonObject.getString("id");
                        String viewname = jsonObject.getString("name");

                        arrayList.add(new RowDatalist(viewid, viewname));
                        RowAdapter rowAdapter = new RowAdapter(arrayList, getApplicationContext());
                        recyclerView.setAdapter(rowAdapter);


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



        String soil = "https://rest.soilgrids.org/query?lon=".concat(String.valueOf(lon)).concat("&lat=").concat(String.valueOf(lat)).concat("&attributes=TAXNWRB");
        final JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, soil, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response1) {

                try {

                    JSONObject jsonObjectnew = response1.getJSONObject("properties");
                    String soiltype = jsonObjectnew.getString("TAXNWRBMajor");

                    textView.setText(soiltype);




                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue2 = Volley.newRequestQueue(this);
        queue2.add(request2);

    }

}


