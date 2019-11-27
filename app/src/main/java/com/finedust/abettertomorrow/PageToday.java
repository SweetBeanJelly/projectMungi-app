package com.finedust.abettertomorrow;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.finedust.abettertomorrow.Data.GPS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class PageToday extends Fragment {

    int curYear, curDay, curHour, curMinute, curMonth, curNoon;
    Calendar calendar;
    String noon = "";
    Date curMillis;
    TimerTask second;
    TextView textDate,textTime;
    private final Handler handler = new Handler();
    private final Handler myhandlerPM2 = new Handler();
    private final Handler myhandlerPM10 = new Handler();

    LocationManager locationManager;
    Context contextLocation;
    Activity activityLocation;
    public double nowLat,nowLon;
    GPS gps = new GPS();

    String jsonStringText = "";
    TextView textNowTemp, textMINTemp, textMAXTemp;
    TextView textLoation, textWeather;
    TextView t1,t2,t3,t4,t5;

    ImageView weatherImage;

    // 미세먼지
    ImageView colorG,colorS,colorB;
    TextView fdPM2, fdPM10;
    String htmlPageUrl = "https://cptdesign.azurewebsites.net";
    ImageView fdImage;
    WebView mWebView;
    int PM2Int,PM10Int;

    public static PageToday newInstance() {
        return new PageToday();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.page_today,container,false);
        contextLocation = getContext();
        activityLocation = getActivity();

        textDate = (TextView)view.findViewById(R.id.textDate);
        textTime = (TextView)view.findViewById(R.id.textTime);

        textMINTemp = (TextView)view.findViewById(R.id.txtMINTemperature);
        textMAXTemp = (TextView)view.findViewById(R.id.txtMAXTemperature);
        textNowTemp = (TextView)view.findViewById(R.id.temperatureTextView);
        textLoation = (TextView)view.findViewById(R.id.textLocation);
        textWeather = (TextView)view.findViewById(R.id.weatherTextView);
        weatherImage = (ImageView)view.findViewById(R.id.weatherImage);

        colorG = (ImageView)view.findViewById(R.id.colorGood);
        colorS = (ImageView)view.findViewById(R.id.colorSoso);
        colorB = (ImageView)view.findViewById(R.id.colorBad);

        fdPM2 = (TextView)view.findViewById(R.id.txtFineDust1);
        fdPM10 = (TextView)view.findViewById(R.id.txtFineDust2);
        mWebView = (WebView)view.findViewById(R.id.webview);
        fdImage = (ImageView)view.findViewById(R.id.imageViewFd);

        t1 = (TextView)view.findViewById(R.id.txt1);
        t2 = (TextView)view.findViewById(R.id.txt2);
        t3 = (TextView)view.findViewById(R.id.txt3);
        t4 = (TextView)view.findViewById(R.id.txt4);
        t5 = (TextView)view.findViewById(R.id.txt5);

        getDate();
        getLocation();
        FineDustHTML();

        return view;
    }

//    View.OnClickListener myClickListener = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(View v)
//        {
//            switch (v.getId())
//            {
//                case R.id.
//            }
//        }
//    };

    // 시간
    private void getDate() {
        second = new TimerTask() {
            private String tag;
            @Override
            public void run() {
                Log.d(tag,curYear+"."+curMonth+"."+curDay+"."+curHour+"."+curMinute);
                Update();
            }
        };
        Timer timer = new Timer();
        timer.schedule(second, 0, 1000);
    }
    protected void Update() {
        calendar = Calendar.getInstance();
        curMillis = calendar.getTime();
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH)+1;
        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        curHour = calendar.get(Calendar.HOUR_OF_DAY);
        curNoon = calendar.get(Calendar.AM_PM);
        if(curNoon == 0) noon = "오전";
        else {
            noon = "오후";
            curHour -= 12;
        }
        curMinute = calendar.get(Calendar.MINUTE);

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                textDate.setText(curYear+"년 "+curMonth+"월 "+curDay+"일");
                textTime.setText(noon+" "+curHour+"시 "+curMinute+"분");
            }
        };
        handler.post(updater);
    }

    // 위치
    private void getLocation() {

        locationManager = (LocationManager)activityLocation.getSystemService(Context.LOCATION_SERVICE);

        // Setting
        int permissionCheck1 = ContextCompat.checkSelfPermission(contextLocation, Manifest.permission.INTERNET);
        if (permissionCheck1 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activityLocation, new String[]{Manifest.permission.INTERNET}, 1);

        int permissionCheck2 = ContextCompat.checkSelfPermission(contextLocation, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck2 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activityLocation, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        int permissionCheck3 = ContextCompat.checkSelfPermission(contextLocation, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck3 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(activityLocation, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) activityLocation.getSystemService(Context.LOCATION_SERVICE);
        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록
        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // 위치값이 갱신되면 이벤트 발생
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            gps.setLatitude(latitude); // 클래스 변수에 위도 대입
            gps.setLongitube(longitude);  // 클래스 변수에 경도 대입
            nowLat=latitude; nowLon=longitude;
            // 위치값에 따라 날씨
            try {
                jsonStringText = new Task().execute().get();
                weatherJsonParser();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    // 날씨 ( URL JSON Parsing )
    public class Task extends AsyncTask<String, Void, String> {

        String clientKey = "c61b1212-1549-4c8e-8dc4-b4a0db42c735";
        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("https://api2.sktelecom.com/weather/current/minutely/?version=1&lat="+nowLat+"&lon="+nowLon);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("appKey", clientKey);
                conn.setDoInput(true);

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();
                    reader.close();
                } else {
                    Log.i("Connect Error", conn.getResponseCode()+"");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
    public void weatherJsonParser() {

        StringBuffer sb = new StringBuffer();

        String name = null; // 현재 위치
        String code = null; // 하늘 상태
        String tc = null; // 현재 기온
        String tmin = null; // 최저 기온
        String tmax = null; // 최고 기온
        String wdir = null; // 풍향
        String wspd = null; // 풍속
        String sinceOntime = null; // 1시간 누적 강수량
        String type = null; // 강수 형태
        String humidity = null; // 상대 습도

        try {
            JSONObject json = new JSONObject(jsonStringText);
            JSONObject json2 = json.getJSONObject("weather");
            JSONArray jarray = json2.getJSONArray("minutely");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);

                JSONObject station = jObject.getJSONObject("station");
                name = station.getString("name");
                JSONObject wind = jObject.getJSONObject("wind");
                wdir = wind.getString("wdir");
                wspd = wind.getString("wspd");
                JSONObject pre = jObject.getJSONObject("precipitation");
                sinceOntime = pre.getString("sinceOntime");
                type = pre.getString("type");

                JSONObject sky = jObject.getJSONObject("sky");
                code= sky.getString("name");
                JSONObject temp = jObject.getJSONObject("temperature");
                tc = temp.getString("tc");
                tmax = temp.getString("tmax");
                tmin = temp.getString("tmin");
                humidity = jObject.getString("humidity");

                sb.append(name);
                textLoation.setText(sb.toString());
                sb.setLength(0);
                sb.append(wdir);
                // 풍향 계산
                float sbNumber;
                sbNumber = Float.parseFloat(sb.toString());
                if(sbNumber>=0.00 && sbNumber<=44.99) {
                    sb.setLength(0);
                    sb.append("북풍");
                } else if(sbNumber>=45.00 && sbNumber<=89.99) {
                    sb.setLength(0);
                    sb.append("북동풍");
                } else if(sbNumber>=90.00 && sbNumber<=134.99) {
                    sb.setLength(0);
                    sb.append("동풍");
                } else if(sbNumber>=135.00 && sbNumber<=179.99) {
                    sb.setLength(0);
                    sb.append("남동풍");
                } else if(sbNumber>=180.00 && sbNumber<=224.99) {
                    sb.setLength(0);
                    sb.append("남풍");
                } else if(sbNumber>=225.00 && sbNumber<=269.99) {
                    sb.setLength(0);
                    sb.append("남서풍");
                } else if(sbNumber>=270.00 && sbNumber<=314.99) {
                    sb.setLength(0);
                    sb.append("서풍");
                } else if(sbNumber>=315.00 && sbNumber<=359.99) {
                    sb.setLength(0);
                    sb.append("북서풍");
                } else {
                    sb.setLength(0);
                    sb.append("북풍");
                }
                t1.setText(sb.toString());
                sb.setLength(0);
                sb.append(wspd+"m/s");
                t2.setText(sb.toString());
                sb.setLength(0);
                sb.append("강수량 : "+sinceOntime+"mm");
                t3.setText(sb.toString());
                sb.setLength(0);
                sb.append(type); // 강수 형태
                switch (sb.toString()) {
                    case "0":
                        sb.setLength(0);
                        sb.append("현상 없음");
                        break;
                    case "1":
                        sb.setLength(0);
                        sb.append("비가 내려요");
                        break;
                    case "2":
                        sb.setLength(0);
                        sb.append("비 또는 눈이 내려요");
                        break;
                    case "3":
                        sb.setLength(0);
                        sb.append("눈이 내려요");
                        break;
                }
                t4.setText(sb.toString());
                sb.setLength(0);
                sb.append("습도 : "+humidity+"%");
                t5.setText(sb.toString());
                sb.setLength(0);

                sb.append(code);
                textWeather.setText(sb.toString());
                switch (sb.toString()) {
                    case "맑음":
                        weatherImage.setImageResource(R.drawable.weather_sun);
                        break;
                    case "구름조금":
                        weatherImage.setImageResource(R.drawable.weather_bit_cloud);
                        break;
                    case "구름많음":
                        weatherImage.setImageResource(R.drawable.weather_more_cloudy);
                        break;
                    case "흐림":
                        weatherImage.setImageResource(R.drawable.weather_cloudy);
                        break;
                    case "흐리고 비":
                        weatherImage.setImageResource(R.drawable.weather_rain);
                        break;
                    case "구름많고 비":
                        weatherImage.setImageResource(R.drawable.weather_more_cloudy_rain);
                        break;
                    case "구름많고 눈":
                        weatherImage.setImageResource(R.drawable.weather_cloudy_snow);
                        break;
                    case "구름많고 비 또는 눈":
                        weatherImage.setImageResource(R.drawable.weather_more_cloudy_rainsnow);
                        break;
                    case "흐리고 눈":
                        weatherImage.setImageResource(R.drawable.weather_snow);
                        break;
                    case "흐리고 비 또는 눈":
                        weatherImage.setImageResource(R.drawable.weather_more_cloudy_rainsnow);
                        break;
                    case "흐리고 낙뢰":
                        weatherImage.setImageResource(R.drawable.weather_storm);
                        break;
                    case "뇌우/비":
                        weatherImage.setImageResource(R.drawable.weather_storm_rainsnow);
                        break;
                    case "뇌우/눈":
                        weatherImage.setImageResource(R.drawable.weather_storm_rainsnow);
                        break;
                    case "뇌우/비 또는 눈":
                        weatherImage.setImageResource(R.drawable.weather_storm_rainsnow);
                        break;
                }
                sb.setLength(0);
                sb.append(tc+"°");
                textNowTemp.setText(sb.toString());
                sb.setLength(0);
                sb.append(tmin+"℃");
                textMINTemp.setText(sb.toString());
                sb.setLength(0);
                sb.append(tmax+"℃");
                textMAXTemp.setText(sb.toString());
                sb.setLength(0);

            }
        } catch (JSONException e) {
            Log.i("JSON PARSER ERROR", e+"");
        }
    }

    // HTML
    private void FineDustHTML() {

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new AndroidPM2(), "dustPM2");
        mWebView.addJavascriptInterface(new AndroidPM10(), "dustPM10");

        if (Build.VERSION.SDK_INT >= 15) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 23){
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mWebView.loadUrl(htmlPageUrl);
        fdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebView.loadUrl("javascript:testpm2()");
                mWebView.loadUrl("javascript:testpm10()");
                Animation startAnimation = AnimationUtils.loadAnimation(activityLocation, R.anim.blink_animation);
                colorB.clearAnimation();
                colorG.clearAnimation();
                colorS.clearAnimation();
                if (PM2Int <= 20) {
                    colorG.startAnimation(startAnimation);
                } else if (PM2Int <= 37) {
                    colorS.startAnimation(startAnimation);
                } else if (PM2Int >= 38) {
                    colorB.startAnimation(startAnimation);
                }
            }
        });
    }

    private class AndroidPM2 {
        @JavascriptInterface
        public void getPM2(final String num) {
            myhandlerPM2.post(new Runnable() {
                @Override
                public void run() {
                    fdPM2.setText("");
                    fdPM2.setText(num);
                    PM2Int=Integer.parseInt(num);
                }
            });
        }
    }
    private class AndroidPM10 {
        @JavascriptInterface
        public void getPM10(final String num) {
            myhandlerPM10.post(new Runnable() {
                @Override
                public void run() {
                    fdPM10.setText("");
                    fdPM10.setText(num);
                    PM10Int=Integer.parseInt(num);
                }
            });
        }
    }
}