package com.finedust.abettertomorrow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PageReference extends Fragment {

    ImageView btnQuestion1,btnQuestion2;
    Activity activity;

    public static PageReference newInstance() {
        return new PageReference();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View view=inflater.inflate(R.layout.page_reference,container,false);
        activity = getActivity();

        btnQuestion1 = (ImageView)view.findViewById(R.id.imageQuestion1);
        btnQuestion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fineDustPM10Show();
            }
        });
        btnQuestion2 = (ImageView)view.findViewById(R.id.imageQuestion2);
        btnQuestion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fineDustPM25Show();
            }
        });
        return view;
    }

    public void fineDustPM10Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyAlertDialogStyle);
        builder.setTitle("미세먼지 (부유먼지)");
        builder.setMessage(" 우리 눈에 보이지 않는 아주 작은 물질로 대기 중에 오랫동안 떠다니거나 흩날려 내려오는 직경 10㎛ 이하의 입자상 물질을 말합니다." +
                " 석탄, 석유 등의 화석연료가 연소될 때 또는 제조업, 자동차 매연 등의 배출가스에서 나오며, 기관지를 거쳐 폐에 흡착되어 각종 폐질환을 유발하는 대기오염물질입니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    public void fineDustPM25Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.MyAlertDialogStyle);
        builder.setTitle("초 미세먼지 (미세먼지)");
        builder.setMessage(" 인체 내 기관지 및 폐 깊숙한 곳까지 침투하기 쉬워 기관지, 폐 등에 붙어 각종 질환을 유발할 수 있으며, 장기간 미세먼지에 노출되면 면역력이" +
                "급격히 저하되어 감기, 천식, 기관지염 등의 호흡기 질환은 물론 심혈관 질환, 피부질환, 안구질환 등 각종 질병에 노출될 수 있습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

}