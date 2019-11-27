package com.finedust.abettertomorrow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.finedust.abettertomorrow.Data.GPS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class PageWeeks extends Fragment {

    LocationManager locationManager;
    Context contextLocation;
    Activity activityLocation;
    double nowLat,nowLon;
    GPS gps = new GPS();

    String jsonStringText = "";
    TextView textCC, textV;
    TextView textWT,textWD,textWY;
    TextView minT,maxT,minD,maxD,minY,maxY;
    ImageView imageT,imageD,imageY;

    public static PageWeeks newInstance() {
        return new PageWeeks();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.page_weeks,container,false);
        contextLocation = getContext();
        activityLocation = getActivity();

        textCC = (TextView)view.findViewById(R.id.textCityCounty);
        textV = (TextView)view.findViewById(R.id.textVillage);
        textWT = (TextView)view.findViewById(R.id.textWeatherTomorrow);
        textWD = (TextView)view.findViewById(R.id.textWeatherDayAfter);
        textWY = (TextView)view.findViewById(R.id.textWeatherYesterday);
        minT = (TextView)view.findViewById(R.id.txtMINTemperatureTomorrow);
        maxT = (TextView)view.findViewById(R.id.txtMAXTemperatureTomorrow);
        imageT = (ImageView)view.findViewById(R.id.weatherImageTomorrow);
        minD = (TextView)view.findViewById(R.id.txtMINTemperatureDayAfter);
        maxD = (TextView)view.findViewById(R.id.txtMAXTemperatureDayAfter);
        imageD = (ImageView)view.findViewById(R.id.weatherImageDayAfter);
        minY = (TextView)view.findViewById(R.id.txtMINTemperatureYesterday);
        maxY = (TextView)view.findViewById(R.id.txtMAXTemperatureYesterday);
        imageY = (ImageView)view.findViewById(R.id.weatherImageYesterday);

        getLocation();

        return view;
    }

    private void getLocation() {

        locationManager = (LocationManager)activityLocation.getSystemService(Context.LOCATION_SERVICE);

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

    public class Task extends AsyncTask<String, Void, String> {

        String clientKey = "c61b1212-1549-4c8e-8dc4-b4a0db42c735";
        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL("https://api2.sktelecom.com/weather/summary?version=1&lat="+nowLat+"&lon="+nowLon);

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

        String city = null; // 현재 위치
        String county = null; // 현재 위치
        String village = null; // 현재 위치
        String tminT = null; String tminD = null; String tminY = null; // 내일, 어제, 모레 최저 기온
        String tmaxT = null; String tmaxD = null; String tmaxY = null; // 내일, 어제, 모레 최고 기온
        String nameT = null; String nameD = null; String nameY = null; // 내일, 어제, 모레 날씨

        try {
            JSONObject json = new JSONObject(jsonStringText);
            JSONObject json2 = json.getJSONObject("weather");
            JSONArray jarray = json2.getJSONArray("summary");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);

                JSONObject station = jObject.getJSONObject("grid");
                city = station.getString("city");
                county = station.getString("county");
                village = station.getString("village");

                JSONObject tomorrow = jObject.getJSONObject("tomorrow");
                JSONObject skyT = tomorrow.getJSONObject("sky");
                nameT = skyT.getString("name");
                JSONObject tempT = tomorrow.getJSONObject("temperature");
                tmaxT = tempT.getString("tmax");
                tminT = tempT.getString("tmin");

                JSONObject dayAfter = jObject.getJSONObject("dayAfterTomorrow");
                JSONObject skyD = dayAfter.getJSONObject("sky");
                nameD = skyD.getString("name");
                JSONObject tempD = dayAfter.getJSONObject("temperature");
                tmaxD = tempD.getString("tmax");
                tminD = tempD.getString("tmin");

                JSONObject yesterday = jObject.getJSONObject("yesterday");
                JSONObject skyY = yesterday.getJSONObject("sky");
                nameY = skyY.getString("name");
                JSONObject tempY = yesterday.getJSONObject("temperature");
                tmaxY = tempY.getString("tmax");
                tminY = tempY.getString("tmin");

                sb.append(city+" "+county);
                textCC.setText(sb.toString());
                sb.setLength(0);
                sb.append(village);
                textV.setText(sb.toString());
                sb.setLength(0);
                sb.append(nameT);
                textWT.setText(sb.toString());
                switch (sb.toString()){
                    case "맑음":
                        imageT.setImageResource(R.drawable.weather_sun);
                        break;
                    case "구름조금":
                        imageT.setImageResource(R.drawable.weather_bit_cloud);
                        break;
                    case "구름많음":
                        imageT.setImageResource(R.drawable.weather_more_cloudy);
                        break;
                    case "흐림":
                        imageT.setImageResource(R.drawable.weather_cloudy);
                        break;
                    case "비":
                        imageT.setImageResource(R.drawable.weather_rain);
                        break;
                    case "눈":
                        imageT.setImageResource(R.drawable.weather_snow);
                        break;
                    case "비 또는 눈":
                        imageT.setImageResource(R.drawable.weather_more_cloudy_rainsnow);
                        break;
                }
                sb.setLength(0);
                sb.append(tmaxT+"℃");
                maxT.setText(sb.toString());
                sb.setLength(0);
                sb.append(tminT+"℃");
                minT.setText(sb.toString());
                sb.setLength(0);
                sb.append(nameD);
                textWD.setText(sb.toString());
                switch (sb.toString()){
                    case "맑음":
                        imageD.setImageResource(R.drawable.weather_sun);
                        break;
                    case "구름조금":
                        imageD.setImageResource(R.drawable.weather_bit_cloud);
                        break;
                    case "구름많음":
                        imageD.setImageResource(R.drawable.weather_more_cloudy);
                        break;
                    case "흐림":
                        imageD.setImageResource(R.drawable.weather_cloudy);
                        break;
                    case "비":
                        imageD.setImageResource(R.drawable.weather_rain);
                        break;
                    case "눈":
                        imageD.setImageResource(R.drawable.weather_snow);
                        break;
                    case "비 또는 눈":
                        imageD.setImageResource(R.drawable.weather_more_cloudy_rainsnow);
                        break;
                }
                sb.setLength(0);
                sb.append(tmaxD+"℃");
                maxD.setText(sb.toString());
                sb.setLength(0);
                sb.append(tminD+"℃");
                minD.setText(sb.toString());
                sb.setLength(0);
                sb.append(nameY);
                textWY.setText(sb.toString());
                switch (sb.toString()){
                    case "맑음":
                        imageY.setImageResource(R.drawable.weather_sun);
                        break;
                    case "구름조금":
                        imageY.setImageResource(R.drawable.weather_bit_cloud);
                        break;
                    case "구름많음":
                        imageY.setImageResource(R.drawable.weather_more_cloudy);
                        break;
                    case "흐림":
                        imageY.setImageResource(R.drawable.weather_cloudy);
                        break;
                    case "비":
                        imageY.setImageResource(R.drawable.weather_rain);
                        break;
                    case "눈":
                        imageY.setImageResource(R.drawable.weather_snow);
                        break;
                    case "비 또는 눈":
                        imageY.setImageResource(R.drawable.weather_more_cloudy_rainsnow);
                        break;
                }
                sb.setLength(0);
                sb.append(tmaxY+"℃");
                maxY.setText(sb.toString());
                sb.setLength(0);
                sb.append(tminY+"℃");
                minY.setText(sb.toString());
                sb.setLength(0);

            }
        } catch (JSONException e) {
            Log.i("JSON PARSER ERROR", e+"");
        }
    }
}
