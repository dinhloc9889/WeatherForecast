package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final int NOTIFICATION_ID = 1;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    RadioGroup radiogpDonVi, radiogpNgonNgu;
    RadioButton radiobtnC, radiobtnF, radiobtnVI, radiobtnEN;
    TextView tvDiaDiem, tvNhietDo, tvTrangThai, tvGioCapNhat, tvNgayCapNhat;
    ImageButton imgbtnTimKiem, imgbtnRefresh;
    CircleImageView cirimgTrangThai;
    TextView tvApSuat, tvDoAm, tvMay, tvGio, tvBinhMinh, tvHoangHon;
    Button btnNgayTiepTheo,btnC, btnF ;
    String City = "hanoi";

    RecyclerView rcvThoiTietGio;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<ThoiTietGio> mangthoitietgio;

    String units = "metric";
    String doCF = "ºC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Anhxa();

        actionToolBar();

        GetCurrentWeatherData("hanoi", units, doCF);

        Get3HourForecastData("hanoi", units, doCF);

        navigationView.setNavigationItemSelectedListener(this);

        radiogpDonVi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radiobtn_c:
                        units = "metric";
                        doCF = "ºC";
                        GetCurrentWeatherData(City, units, doCF);
                        mangthoitietgio.clear();
                        Get3HourForecastData(City, units, doCF);
                        break;
                    case R.id.radiobtn_f:
                        units = "imperial";
                        doCF = "ºF";
                        GetCurrentWeatherData(City, units, doCF);
                        mangthoitietgio.clear();
                        Get3HourForecastData(City, units, doCF);
                        break;
                }
                onBackPressed();
            }
        });

        radiogpNgonNgu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radiobtn_vi:
                        Changelanguage("vi");
                        break;
                    case R.id.radiobtn_en:
                        Changelanguage("en");
                        break;
                }
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        imgbtnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogChangeLocation(Gravity.CENTER);
            }
        });

        imgbtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Updating.....",Toast.LENGTH_SHORT).show();
                Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                startActivity(refresh);
                finish();
            }
        });

        btnNgayTiepTheo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("name", City);
                intent.putExtra("units", units);
                intent.putExtra("doCF", doCF);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        BroadcastReceiverWeather broadcastReceiver = new BroadcastReceiverWeather();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(broadcastReceiver, filter);
    }

    private void Anhxa(){

        drawerLayout = findViewById(R.id.drawerlayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationview);
        View viewHeader = navigationView.getHeaderView(0);
        radiobtnC = viewHeader.findViewById(R.id.radiobtn_c);
        radiobtnF= viewHeader.findViewById(R.id.radiobtn_f);
        radiobtnVI = viewHeader.findViewById(R.id.radiobtn_vi);
        radiobtnEN= viewHeader.findViewById(R.id.radiobtn_en);
        radiogpDonVi = viewHeader.findViewById(R.id.radiogp_donvi);
        radiogpNgonNgu= viewHeader.findViewById(R.id.radiogp_ngonngu);
        tvDiaDiem =  findViewById(R.id.tv_diadiem);
        imgbtnTimKiem =  findViewById(R.id.imgbtn_timkiem);
        imgbtnRefresh =  findViewById(R.id.imgbtn_refresh);
        tvNhietDo =  findViewById(R.id.tv_nhietdo);
        tvTrangThai =  findViewById(R.id.tv_trangthai);
        cirimgTrangThai =  findViewById(R.id.cirimg_trangthai);
        tvApSuat =  findViewById(R.id.tv_apsuat);
        tvDoAm =  findViewById(R.id.tv_doam);
        tvMay =  findViewById(R.id.tv_may);
        tvGio =  findViewById(R.id.tv_gio);
        tvBinhMinh =  findViewById(R.id.tv_binhminh);
        tvHoangHon =  findViewById(R.id.tv_hoanghon);
        tvGioCapNhat =  findViewById(R.id.tv_giocapnhat);
        tvNgayCapNhat =  findViewById(R.id.tv_ngaycapnhat);
        btnNgayTiepTheo =  findViewById(R.id.btn_ngaytieptheo);
        rcvThoiTietGio =  findViewById(R.id.rcv_thoitietgio);
        mangthoitietgio = new ArrayList<ThoiTietGio>();

        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, mangthoitietgio);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false);
        rcvThoiTietGio.setHasFixedSize(true);
        rcvThoiTietGio.setLayoutManager(linearLayoutManager);
        rcvThoiTietGio.setFocusable(false);
        rcvThoiTietGio.setAdapter(recyclerViewAdapter);
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_menu_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    private void Changelanguage(String language){
        Locale locale = new Locale(language);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void GetCurrentWeatherData(String data, String units, String doCF) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String lang = getResources().getString(R.string.lang) + "&";
        String langua = getResources().getString(R.string.langua);
        String coun = getResources().getString(R.string.coun);
        Locale locale = new Locale ( langua , coun );
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + data + "&" + lang + "units=" + units + "&appid=2990856eb64e6d081d1aac9839639918";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String ngay = response.getString("dt");
                            String tendiadiem = response.getString("name");
                            tvDiaDiem.setText(tendiadiem);

                            long l = Long.valueOf(ngay);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm a", locale);
                            String Gio = simpleDateFormat1.format(date);
                            tvGioCapNhat.setText(Gio);
                            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("EEEE, dd-MM-yyyy", locale);
                            String Ngay = simpleDateFormat2.format(date);
                            tvNgayCapNhat.setText(Ngay);

                            JSONArray jsonArrayWeather = response.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String trangThai = jsonObjectWeather.getString("description");
                            String hinh = jsonObjectWeather.getString("icon");

                            tvTrangThai.setText(trangThai);
                            Picasso.get().load("https://openweathermap.org/img/wn/"+hinh+"@2x.png")
                                    .error(R.drawable.ic_baseline_image_not_supported_24)
                                    .into(cirimgTrangThai);

                            JSONObject jsonObjectMain = response.getJSONObject("main");
                            String nhietDo = jsonObjectMain.getString("temp");
                            String apSuat = jsonObjectMain.getString("pressure");
                            String doAm = jsonObjectMain.getString("humidity");

                            Double t = Double.parseDouble(nhietDo);
                            String NhietDo = String.valueOf(t.intValue());

                            tvNhietDo.setText(NhietDo + doCF);
                            tvApSuat.setText(getResources().getString(R.string.pressure) + " " + apSuat+ "hpa");
                            tvDoAm.setText(getResources().getString(R.string.humidity) + " " + doAm+"%");


                            JSONObject jsonObjectWind = response.getJSONObject("wind");
                            String gio = jsonObjectWind.getString("speed");
                            tvGio.setText(getResources().getString(R.string.windspeed) + " " + gio+"m/s");

                            JSONObject jsonObjectClouds = response.getJSONObject("clouds");
                            String may = jsonObjectClouds.getString("all");
                            tvMay.setText(getResources().getString(R.string.clouds) + " " + may+"%");

                            JSONObject jsonObjectSys = response.getJSONObject("sys");

                            String binhMinh = jsonObjectSys.getString("sunrise");
                            long bm = Long.parseLong(binhMinh);
                            Date bmdate = new Date(bm*1000L);
                            SimpleDateFormat simpleDateFormatbm = new SimpleDateFormat("HH:mm a", locale);
                            String BinhMinh = simpleDateFormatbm.format(bmdate);
                            tvBinhMinh.setText(getResources().getString(R.string.sunrise) + " " + BinhMinh);

                            String hoangHon = jsonObjectSys.getString("sunset");
                            long hh = Long.parseLong(hoangHon);
                            Date hhdate = new Date(hh*1000L);
                            SimpleDateFormat simpleDateFormathh = new SimpleDateFormat("HH:mm a", locale);
                            String HoangHon = simpleDateFormathh.format(hhdate);
                            tvHoangHon.setText(getResources().getString(R.string.sunset) + " " + HoangHon);

                            sendCustomsNotification(tendiadiem,trangThai, NhietDo, hinh, units, doCF);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void sendCustomsNotification(String tendiadiem, String trangThai, String NhietDo, String hinh, String units, String doCF){
        String contentNoti1 = getResources().getString(R.string.contentnoti1);
        String contentNoti2 = getResources().getString(R.string.contentnoti2);
        String contentNoti3 = getResources().getString(R.string.contentnoti3);
        double nhietDoNoti = Double.parseDouble(NhietDo);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_rain);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_weather_normal);
//        ("https://openweathermap.org/img/wn/"+hinh+"@2x.png");
//        LoadImageInternet loadImageInternet = new LoadImageInternet();
//        loadImageInternet.execute("https://openweathermap.org/img/wn/"+hinh+"@2x.png");
//        .setLargeIcon(loadImageInternet.doInBackground())

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT);

        if(trangThai.startsWith("mưa") || trangThai.endsWith("mưa") || trangThai.startsWith("giông") || trangThai.endsWith("rain")){
            Notification notification = new NotificationCompat.Builder(this, WeatherNotiChannel.CHANNEL_ID)
                    .setLargeIcon(bitmap1)
                    .setSmallIcon(R.drawable.icon_weather)
                    .setContentTitle(tendiadiem + " - " + trangThai+ " - " + NhietDo + doCF)
                    .setContentText(contentNoti1)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentNoti1))
                    .build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }else if(units.equals("metric") && nhietDoNoti > 37 || units.equals("imperial") && nhietDoNoti > 100){
            Notification notification = new NotificationCompat.Builder(this, WeatherNotiChannel.CHANNEL_ID)
                    .setLargeIcon(bitmap2)
                    .setSmallIcon(R.drawable.icon_weather)
                    .setContentTitle(tendiadiem + " - " + trangThai+ " - " + NhietDo + doCF)
                    .setContentText(contentNoti2)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentNoti2))
                    .build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }else{
            Notification notification = new NotificationCompat.Builder(this, WeatherNotiChannel.CHANNEL_ID)
                    .setLargeIcon(bitmap2)
                    .setSmallIcon(R.drawable.icon_weather)
                    .setContentTitle(tendiadiem + " - " + trangThai+ " - " + NhietDo + doCF)
                    .setContentText(contentNoti3)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(contentNoti3))
                    .build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(NOTIFICATION_ID, notification);
        }
    }

    public void Get3HourForecastData(String data, String units, String doCF) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String lang = getResources().getString(R.string.lang) + "&";
        String langua = getResources().getString(R.string.langua);
        String coun = getResources().getString(R.string.coun);
        Locale locale = new Locale ( langua , coun );
        String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + data + "&" + lang + "&units=" + units + "&cnt=8&appid=2990856eb64e6d081d1aac9839639918";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for(int i = 0;i < jsonArrayList.length();i++){
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);

                                String giodt = jsonObjectList.getString("dt_txt");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm a", locale);
                                Date date = simpleDateFormat.parse(giodt);
                                String gio = simpleDateFormat2.format(date);

                                JSONObject jsonObjectMain = jsonObjectList.getJSONObject("main");
                                String nhietDo = jsonObjectMain.getString("temp");

                                Double t = Double.valueOf(nhietDo);
                                String nhietDoGio = String.valueOf(t.intValue());


                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String trangThaiGio = jsonObjectWeather.getString("description");
                                String hinhTrangThaiGio = jsonObjectWeather.getString("icon");

                                ThoiTietGio thoiTietGio = new ThoiTietGio(nhietDoGio,hinhTrangThaiGio,trangThaiGio,gio, doCF);
                                mangthoitietgio.add(thoiTietGio);
                            }
                            recyclerViewAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(stringRequest);
    }

    public void ShowDialogChangeLocation(int gravity){
        final Dialog dialogChange = new Dialog(MainActivity.this);
        dialogChange.requestWindowFeature(Window.FEATURE_NO_TITLE);// Bo title
        dialogChange.setContentView(R.layout.activity_dialog_thaydoidiadiem);

        Window window = dialogChange.getWindow();
        if(window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialogChange.setCanceledOnTouchOutside(true);

        EditText edtNhapDiaDiem = dialogChange.findViewById(R.id.edt_nhapdiadiem);
        Button btnHuy = dialogChange.findViewById(R.id.btn_huy);
        Button btnTimKiem = dialogChange.findViewById(R.id.btn_timkiem);

        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChange.cancel();
            }
        });

        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = edtNhapDiaDiem.getText().toString().trim();
                if(city.equals("")){
                    City = "ha noi";
                    GetCurrentWeatherData(City, units, doCF);
                    mangthoitietgio.clear();
                    Get3HourForecastData(City, units, doCF);
                }
                else{
                    City = city;
                    GetCurrentWeatherData(City, units, doCF);
                    mangthoitietgio.clear();
                    Get3HourForecastData(City, units, doCF);
                }
                dialogChange.dismiss();
            }
        });
        dialogChange.show();
    }

}
