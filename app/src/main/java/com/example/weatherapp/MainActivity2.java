package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    String City = "";
    TextView tvDiaDiem2;
    ListView lvCacNgayTiepTheo;
    CustomAdapter customAdapter;
    ArrayList<ThoiTiet> mangthoitiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        String units = intent.getStringExtra("units");
        String doCF = intent.getStringExtra("doCF");

        Anhxa();

        if(city.equals("")){
            City = "hanoi";
            Get7DaysData(City, units, doCF);
        }else{
            City = city;
            Get7DaysData(City, units, doCF);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    private void Anhxa(){
        tvDiaDiem2 = findViewById(R.id.tv_diadiem2);
        lvCacNgayTiepTheo = findViewById(R.id.lv_cacngaytieptheo);
        mangthoitiet = new ArrayList<ThoiTiet>();
        customAdapter = new CustomAdapter(MainActivity2.this,mangthoitiet);
        lvCacNgayTiepTheo.setAdapter(customAdapter);
    }

    public void Get7DaysData(String data, String units, String doCF) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity2.this);
        String lang = this.getResources().getString(R.string.lang) + "&";
        String langua = getResources().getString(R.string.langua);
        String coun = getResources().getString(R.string.coun);
        Locale locale = new Locale ( langua , coun );
        String url = "https://api.openweathermap.org/data/2.5/forecast/daily?q=" + data + "&" + lang + "units=" + units + "&cnt=7&appid=53fbf527d52d4d773e828243b90c1f8e"; //be8d3e323de722ff78208a7dbb2dcd6f";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String tentp = jsonObjectCity.getString("name");
                            tvDiaDiem2.setText(tentp);

                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for(int i = 0;i < jsonArrayList.length();i++){
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String ngay = jsonObjectList.getString("dt");
                                long l = Long.valueOf(ngay);
                                Date date = new Date(l*1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MM-yyyy", locale);
                                String Ngay  = simpleDateFormat.format(date);

                                JSONObject jsonObjectNhietDo = jsonObjectList.getJSONObject("temp");
                                String max = jsonObjectNhietDo.getString("max");
                                String min = jsonObjectNhietDo.getString("min");

                                Double a = Double.valueOf(max);
                                Double b = Double.valueOf(min);
                                String NhietdoMax = String.valueOf(a.intValue());
                                String NhietdoMin = String.valueOf(b.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String trangThai = jsonObjectWeather.getString("description");
                                String hinhTrangThai = jsonObjectWeather.getString("icon");

                                mangthoitiet.add(new ThoiTiet(Ngay,trangThai,hinhTrangThai,NhietdoMax,NhietdoMin, doCF));

                            }
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity2.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
}