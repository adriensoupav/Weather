package com.example.weather;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private RequestQueue requestQueue;
    TextView mDate, mDetails,mTemp,mCity, mPressure, mHumid,mTime,mSunrise,mSunset, mFeels;
    String PLACE="toronto";
    ImageView imgIcon;
    EditText editText;
    Button button1;




    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mDate = findViewById(R.id.mDate);
    mCity = findViewById(R.id.mCity);
    mTemp = findViewById(R.id.mTemp);
    mPressure=findViewById(R.id.mPressure);
    mHumid=findViewById(R.id.mHumid);
    mTime=findViewById(R.id.mTime);
    imgIcon=findViewById(R.id.imgIcon);
    mSunrise=findViewById(R.id.mSunrise);
    mSunset=findViewById(R.id.mSunset);
    mFeels=findViewById(R.id.mFeels);


    mDetails = findViewById(R.id.mDetails);
    requestQueue = Volley.newRequestQueue(this);
    display();
}
@Override
        public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.search, menu);
    MenuItem menuItem=menu.findItem(R.id.action_search);
    SearchView searchView=(SearchView)menuItem.getActionView();
    searchView.setQueryHint("Type name of city here..");
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            PLACE=query;
            display();
            InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(getCurrentFocus()!=null)
            {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return true;
        }


        @Override
        public boolean onQueryTextChange(String newText)
        {
            return false;
        }

    });
    return super.onCreateOptionsMenu(menu);
}





        public void display() {
            String url="http://api.openweathermap.org/data/2.5/weather?q="+PLACE+"&appid=5bfa20d6cac55a533893cbbc1e3019a7&units=metric";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object=array.getJSONObject(0);
                    int tempC=(int)Math.round(main_object.getDouble("temp"));
                   String temp=String.valueOf(tempC);
                  mTemp.setText(temp);
                  String details=object.getString("description");
                  String city=response.getString("name");
                  String icon=object.getString("icon");
                  mDetails.setText(details);
                  mCity.setText(city);
                  // date  formatting
                    Calendar calendar= Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("EEE,MMMM dd");
                    String formatted_date=simpleDateFormat.format(calendar.getTime());
                    //time formatting
                    JSONObject sys=response.getJSONObject("sys");

                    long timeS=sys.getLong("sunset");
                    long timeSR=sys.getLong("sunrise");
                    timeS=timeS*1000;
                    timeSR=timeSR*1000;

                    SimpleDateFormat sdf=new SimpleDateFormat(("HH:mm:ss"));
                    String formatted_timeS=sdf.format(timeS);
                    String formatted_timeSR=sdf.format(timeSR);

                    //display date and time
                    mDate.setText(formatted_date);
                    mSunset.setText(formatted_timeS);
                    mSunrise.setText(formatted_timeSR);

                    // image
                    String imageUri="http://openweathermap.org/img/w/"+icon+".png";
                    Uri myUri= Uri.parse(imageUri);
                    Picasso.get().load(myUri).resize(200,200).into(imgIcon);
                    int pressure=(int)Math.round(main_object.getDouble("pressure"));
                    String pressureC=String.valueOf(pressure);
                    mPressure.setText(pressureC+" hPa");

                    int humidity=(int)Math.round(main_object.getDouble("humidity"));
                    String humidityC=String.valueOf(humidity);
                    mHumid.setText(humidityC+" g.kg^-1");

                    int feels=(int)Math.round(main_object.getDouble("feels_like"));
                    String feelsC=String.valueOf(feels);
                    mFeels.setText(feelsC+" °");




                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);



    }
}